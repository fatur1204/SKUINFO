package com.idpskuinfo.skuinfo;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.idpskuinfo.skuinfo.data.LoadActivity;
import com.idpskuinfo.skuinfo.db.CurrencyHelper;
import com.idpskuinfo.skuinfo.db.SkuHelper;
import com.idpskuinfo.skuinfo.ftp.MyFTPClientFunctions;
import com.idpskuinfo.skuinfo.setting.SettingModel;
import com.idpskuinfo.skuinfo.setting.SettingPreference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Fragment_Update extends Fragment implements View.OnClickListener {
    private static final String TAG = Fragment_Update.class.getSimpleName();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static int RECORD_UPDATE = 0;
    private final int ALERT_DIALOG_DELETE = 101;
    Button btnupdate;
    ProgressDialog progressDialog;
    boolean bdata, bdatacurr = false;
    private String hostname, port, username, password, dateupdate;
    private TextView TxtHostName, TxtPort, TxtUserName, TxtPassword;
    private ProgressBar progressBar;
    private Boolean bSKUMASTER, bCURRENCY, bUPDATE_DATA, bCONNECTION = false;
    private TextView TxtLineLog;
    private SettingModel settingModel;
    private SettingPreference settingPreference;
    private MyFTPClientFunctions ftpclient = null;
    private SkuHelper skuHelper;
    private CurrencyHelper currencyHelper;

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
            btnupdate.setEnabled(false);
            showExistingPreference();

            String TimeUpdate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            Log.d(TAG, "time update : " + TimeUpdate + "-" + dateupdate);
            if (TimeUpdate.trim().equals(dateupdate.trim())) {
                showAlertDialog(ALERT_DIALOG_DELETE);
            } else {
                if ((hostname.length() < 1) && (port.length() < 1) && (username.length() < 1) && (password.length() < 1)) {
                    Toast.makeText(getContext(), "Please complete ftp description", Toast.LENGTH_LONG).show();
                    btnupdate.setEnabled(true);
                } else {
                    new LoadTask().execute();
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
        dateupdate = settingModel.getDateUpdate().isEmpty() ? "" : settingModel.getDateUpdate();

        Log.d(TAG, "HOSTNAME: " + hostname);
        Log.d(TAG, "PORT: " + port);
        Log.d(TAG, "USERNAME: " + username);
        Log.d(TAG, "PASSWORD: " + password);

        TxtHostName.setText(hostname);
        TxtPort.setText(port);
        TxtUserName.setText(username);
        TxtPassword.setText(password);
    }

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_DELETE;
        String dialogTitle, dialogMessage;

        dialogMessage = getResources().getString(R.string.msgupdate);
        dialogTitle = getResources().getString(R.string.msgtitle);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.msgbtnyes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new LoadTask().execute();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.msgbtnno), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        btnupdate.setEnabled(true);
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "resultcode : " + resultCode);
        Log.d(TAG, "requestcode : " + requestCode);

        if (requestCode == 101) {
            skuHelper = SkuHelper.getInstance(getContext());
            skuHelper.open();
            currencyHelper = CurrencyHelper.getInstance(getContext());
            currencyHelper.open();

            int totalcount = skuHelper.queryAll().getCount();
            TxtLineLog.append("total sku update : " + totalcount + "\n");
            int totalrate = currencyHelper.queryAll().getCount();
            TxtLineLog.append("total rate update : " + totalrate + "\n");

            int total_record = RECORD_UPDATE;
            Log.d(TAG, "total : " + total_record);

            if (totalcount != total_record) {
                TxtLineLog.append("update data failed...");
            } else {

                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                saveSetting(date);
                TxtLineLog.append("update data successfully...[finish]");
            }

            skuHelper.close();
            currencyHelper.close();
        }
    }

    private void saveSetting(String dateupdate) {
        SettingPreference userPreference = new SettingPreference(getContext());
        settingModel.setDateUpdate(dateupdate);
        userPreference.setSetting(settingModel);
    }

    private class LoadTask extends AsyncTask<String, Integer, Boolean> {
        double sizesku =0;

        @Override
        protected void onPreExecute() {
            /*progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("File downloading ...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog.show();*/

            progressDialog = ProgressDialog.show(getContext(), "Please Wait!", "Process Download...", false, false);
            //progressBar.setVisibility(View.VISIBLE);


            File file_sku = new File(getContext().getFilesDir().toString(), "skumaster.txt");
            file_sku.delete();
            File file_rate = new File(getContext().getFilesDir().toString(), "skurate.txt");
            file_rate.delete();
            Log.d(TAG + " PreExceute", "On pre Exceute......");
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Log.d(TAG + " DoINBackGround", "On doInBackground...");
            boolean status = false;
            int count = 0;
            bSKUMASTER = false;
            bCURRENCY = false;
            bUPDATE_DATA = false;
            bCONNECTION = false;

            status = ftpclient.ftpConnect(hostname, username, password, Integer.parseInt(port));
            if (status == true) {
                bCONNECTION = true;
                Log.d(TAG, "Connection Success");
                publishProgress(1);
                try {
                    sizesku = ftpclient.getFileSize("/SKUINFO/skumaster.txt");
                    sizesku = sizesku + ftpclient.getFileSize("/SKUINFO/skurate.txt");
                    sizesku = (sizesku/1024)/1000;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                bdata = ftpclient.ftpDownload(ftpclient.ftpGetCurrentWorkingDirectory() + "SKUINFO/skumaster.txt", getContext().getFilesDir().toString() + "/skumaster.txt");
                bdatacurr = ftpclient.ftpDownload(ftpclient.ftpGetCurrentWorkingDirectory() + "SKUINFO/skurate.txt", getContext().getFilesDir().toString() + "/skurate.txt");

            } else {
                Log.d(TAG, "Connection failed");
                publishProgress(1);
                ftpclient.ftpDisconnect();
            }
            return status;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
            if (bCONNECTION) {
                TxtLineLog.append("ftp status [connected]...\n");
            } else {
                TxtLineLog.append("Connection ftp failed...\n");
            }
        }


        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            Log.d(TAG + " onPostExecute", "" + result);
            btnupdate.setEnabled(true);

            if (!bCONNECTION) {
                Toast.makeText(getContext(), "Connection ftp failed!", Toast.LENGTH_LONG).show();
            } else {
                if ((bdata = true) && (bdatacurr = true)) {
                    Log.d(TAG,"SIZE FILE : "+sizesku);
                    TxtLineLog.append("Download file complete ["+String.format("%,.2f", sizesku)+"MB]...\n");
                    TxtLineLog.append("disconnect...\n");
                    ftpclient.ftpDisconnect();

                    Intent mIntent = new Intent(getContext(), LoadActivity.class);
                    startActivityForResult(mIntent, LoadActivity.NUMBER_RESULT);
                } else {
                    TxtLineLog.append("File doesn't exists...\n");
                    TxtLineLog.append("disconnect...\n");
                    ftpclient.ftpDisconnect();
                    Toast.makeText(getContext(), "File doesn't exists", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
