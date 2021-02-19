package com.idpskuinfo.skuinfo;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.idpskuinfo.skuinfo.db.CurrencyHelper;
import com.idpskuinfo.skuinfo.db.DatabaseContract;
import com.idpskuinfo.skuinfo.db.SkuHelper;
import com.idpskuinfo.skuinfo.db.UpdateHelper;
import com.idpskuinfo.skuinfo.ftp.MyFTPClientFunctions;
import com.idpskuinfo.skuinfo.setting.SettingModel;
import com.idpskuinfo.skuinfo.setting.SettingPreference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Fragment_Update extends Fragment implements View.OnClickListener {
    private static final String TAG = Fragment_Update.class.getSimpleName();
    private final int MILLIS_IN_SEC = 5000;
    Button btnupdate;
    private Fragment_Update fragment_update;
    SkuHelper skuHelper;
    CurrencyHelper currencyHelper;
    UpdateHelper updateHelper;
    ContentValues values = new ContentValues();
    private String hostname, port, username, password;
    private TextView TxtHostName, TxtPort, TxtUserName, TxtPassword;
    private Button btnDownload;
    private ProgressBar progressBar;
    ProgressDialog progressDialog;
    private Boolean bSKUMASTER, bCURRENCY, bUPDATE_DATA, bCONNECTION = false;
    private TextView TxtLineLog;

    private SettingModel settingModel;
    private SettingPreference settingPreference;

    private MyFTPClientFunctions ftpclient = null;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public Fragment_Update() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__update, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        skuHelper = SkuHelper.getInstance(getContext());
        //skuHelper.open();
        currencyHelper = CurrencyHelper.getInstance(getContext());
        //currencyHelper.open();
        updateHelper = UpdateHelper.getInstance(getContext());
        //updateHelper.open();

        btnupdate = view.findViewById(R.id.btnupdate);
        TxtHostName = view.findViewById(R.id.txthostname);
        TxtPort = view.findViewById(R.id.txtport);
        TxtUserName = view.findViewById(R.id.txtusername);
        TxtPassword = view.findViewById(R.id.txtpassword);
        progressBar = view.findViewById(R.id.progress_bar);
        TxtLineLog = view.findViewById(R.id.edtlinelog);

        settingPreference = new SettingPreference(getContext());
        showExistingPreference();

        ftpclient = new MyFTPClientFunctions();

        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    (Activity) getContext(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        btnupdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnupdate) {
            //download data from ftp
            TxtLineLog.setText("");
            Log.d(TAG, "start 1");
            //downloadata();
            new LoadTask().execute();
        }
    }

    private class LoadTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            skuHelper.open();
            currencyHelper.open();
            updateHelper.open();

            progressDialog = ProgressDialog.show(getContext(), "Please Wait!", "Process Download and Update...", false, false);
            //progressBar.setVisibility(View.VISIBLE);
            Log.d(TAG + " PreExceute", "On pre Exceute......");
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Log.d(TAG + " DoINBackGround", "On doInBackground...");
            boolean status = false;
            bSKUMASTER = false;
            bCURRENCY = false;
            bUPDATE_DATA = false;
            bCONNECTION = false;

            status = ftpclient.ftpConnect(hostname, username, password, 21);
            if (status == true) {
                Log.d(TAG, "Connection Success");
                ftpclient.ftpDownload(ftpclient.ftpGetCurrentWorkingDirectory() + "skumaster.txt", getContext().getFilesDir().toString() + "/skumaster.txt");
                ftpclient.ftpDownload(ftpclient.ftpGetCurrentWorkingDirectory() + "skurate.txt", getContext().getFilesDir().toString() + "/skurate.txt");


                //Load data SKU MASTER----------------------------------------------------------------------
                Log.d(TAG, "start 2");
                Cursor qrycek = skuHelper.queryAll();
                if (qrycek.getCount() > 0) {
                    if (qrycek != null) {
                        long delquery = skuHelper.deleteAll();
                        //Log.d(TAG, "value delete: " + delquery);
                        bCONNECTION = delquery != 0;
                    }
                }


                BufferedReader reader = null;
                String mLine = "";
                int record = 0;
                String SkuCode = "";
                String SkuDescription = "";
                String SkuRetail = "";
                String SkuType = "";
                try {
                    //reader = new BufferedReader(new InputStreamReader(getActivity().getAssets().open("skumaster.txt"), "UTF-8"));
                    File file = new File(getActivity().getFilesDir().toString(), "skumaster.txt");
                    reader = new BufferedReader(new FileReader(file));
                    while ((mLine = reader.readLine()) != null) {
                        //Log.d(TAG, "DATAKU : " + mLine);
                        String SkuMaster = mLine.trim();
                        String[] SkuMasterList = SkuMaster.split(",");
                        SkuCode = SkuMasterList[0];
                        SkuDescription = SkuMasterList[1];
                        SkuRetail = SkuMasterList[2];
                        SkuType = SkuMasterList[3];

                        //insert into table sqlite sku master
                        values.clear();
                        values.put(DatabaseContract.NoteColumns.SKUID, SkuCode.trim());
                        values.put(DatabaseContract.NoteColumns.DESCRIPTION, SkuDescription.trim());
                        values.put(DatabaseContract.NoteColumns.RETAIL_PRICE, SkuRetail.trim());
                        values.put(DatabaseContract.NoteColumns.SKUTYPE, SkuType.trim());
                        long result = skuHelper.insert(values);
                        bSKUMASTER = result > 0;
                    }
                } catch (IOException e) {
                    //log the exception
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            //log the exception
                        }
                    }
                }
                //Load data SKU MASTER END------------------------------------------------------------------

                //Load data Currency------------------------------------------------------------------------
                Cursor qrycek_curr = currencyHelper.queryAll();
                if (qrycek_curr.getCount() > 0) {
                    if (qrycek_curr != null) {
                        long delquery = currencyHelper.deleteAll();
                    }
                }


                String CurrId = "";
                String CurrDes = "";
                String CurrDate = "";
                String CurrRate = "";
                int i = 0;
                try {
                    reader = new BufferedReader(
                            new InputStreamReader(getActivity().getAssets().open("curency.txt"), StandardCharsets.UTF_8));
                    // do reading, usually loop until end of file reading
                    while ((mLine = reader.readLine()) != null) {
                        String Currency = mLine.trim();
                        String[] CurrencyList = Currency.split(",");
                        if (i > 0) {
                            CurrId = CurrencyList[1];
                            CurrDes = CurrencyList[0];
                            CurrRate = CurrencyList[2];
                        } else {
                            CurrDate = CurrencyList[0];
                        }

                        //insert into table sqlite sku master
                        if (i > 0) {
                            values.clear();
                            values.put(DatabaseContract.CurrColumns.CURRID, CurrId.trim());
                            values.put(DatabaseContract.CurrColumns.CURDES, CurrDes.trim());
                            values.put(DatabaseContract.CurrColumns.CURRDATE, CurrDate.trim());
                            values.put(DatabaseContract.CurrColumns.CUR_RET, CurrRate.trim());

                            long result = currencyHelper.insert(values);
                            bCURRENCY = result > 0;
                        }

                        i++;
                    }

                } catch (IOException e) {
                    //log the exception
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            //log the exception
                        }
                    }
                }
                //Load data Currency End--------------------------------------------------------------------

                //insert into last update
                Cursor qrycek_update = updateHelper.queryAll();
                if (qrycek_update.getCount() > 0) {
                    if (qrycek_update != null) {
                        long delquery = updateHelper.deleteAll();
                    }
                }

                String dateTime;
                Date calendar = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                dateTime = simpleDateFormat.format(calendar.getTime());
                Log.d(TAG, "time now:" + dateTime);

                values.clear();
                values.put(DatabaseContract.UpdateColumns.UPDATEDATE, CurrDate.trim());
                values.put(DatabaseContract.UpdateColumns.UPDATETIME, dateTime);
                long result = updateHelper.insert(values);
                bUPDATE_DATA = result > 0;
                //------------------------------------------------------------------------------------------
            } else {
                Log.d(TAG, "Connection failed");
                TxtLineLog.append("Connection ftp failed...\n");
                ftpclient.ftpDisconnect();
            }
            return status;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.INVISIBLE);
            progressDialog.dismiss();
            Log.d(TAG + " onPostExecute", "" + result);

            ftpclient.ftpDisconnect();
            skuHelper.close();
            currencyHelper.close();
            updateHelper.close();


            if (!bCONNECTION) {
                Toast.makeText(getContext(), "Connection into ftp failed!", Toast.LENGTH_LONG).show();
            } else {
                if ((bSKUMASTER = true) && (bCURRENCY = true) && (bUPDATE_DATA = true)) {
                    TxtLineLog.append("Update data sucessfuly...\n");
                    Toast.makeText(getContext(), "Update complete", Toast.LENGTH_LONG).show();
                } else {
                    TxtLineLog.append("Update data failed...\n");
                    Toast.makeText(getContext(), "Update failed", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    private void showExistingPreference() {
        settingModel = settingPreference.getSetting();
        populateView(settingModel);
    }

    private void populateView(SettingModel settingModel) {
        hostname = settingModel.getHostName().isEmpty() ? "" : settingModel.getHostName();
        port = settingModel.getPort().isEmpty() ? "" : settingModel.getPort();
        username = settingModel.getUserName().isEmpty() ? "" : settingModel.getUserName();
        password = settingModel.getPassword().isEmpty() ? "" : settingModel.getPassword();

        Log.d(TAG, "HOSTNAME: " + hostname);
        Log.d(TAG, "PORT: " + port);
        Log.d(TAG, "USERNAME: " + username);
        Log.d(TAG, "PASSWORD: " + password);

        TxtHostName.setText(hostname);
        TxtPort.setText(port);
        TxtUserName.setText(username);
        TxtPassword.setText(password);
    }
}