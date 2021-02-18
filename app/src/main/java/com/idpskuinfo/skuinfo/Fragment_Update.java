package com.idpskuinfo.skuinfo;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.idpskuinfo.skuinfo.db.CurrencyHelper;
import com.idpskuinfo.skuinfo.db.DatabaseContract;
import com.idpskuinfo.skuinfo.db.SkuHelper;
import com.idpskuinfo.skuinfo.db.UpdateHelper;
import com.idpskuinfo.skuinfo.setting.SettingModel;
import com.idpskuinfo.skuinfo.setting.SettingPreference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Fragment_Update extends Fragment implements View.OnClickListener {
    private static final String TAG = Fragment_Update.class.getSimpleName();
    Button btnupdate;
    private Fragment_Update fragment_update;
    SkuHelper skuHelper;
    CurrencyHelper currencyHelper;
    UpdateHelper updateHelper;
    ContentValues values = new ContentValues();
    EditText edtData;
    private String hostname,port,username,password;

    private SettingModel settingModel;
    private SettingPreference settingPreference;

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
        skuHelper.open();
        currencyHelper = CurrencyHelper.getInstance(getContext());
        currencyHelper.open();
        updateHelper = UpdateHelper.getInstance(getContext());
        updateHelper.open();

        btnupdate = view.findViewById(R.id.btnupdate);
        btnupdate.setOnClickListener(this);

        settingPreference = new SettingPreference(getContext());
        showExistingPreference();
    }

    @Override
    public void onClick(View v) {
        //Load data SKU MASTER----------------------------------------------------------------------
        Cursor qrycek = skuHelper.queryAll();
        if (qrycek.getCount() > 0) {
            if (qrycek != null) {
                long delquery = skuHelper.deleteAll();
                Log.d(TAG, "value delete: " + delquery);
                if (delquery == 0) {
                    Toast.makeText(getContext(), "Clear Data failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        BufferedReader reader = null;
        String mLine = "";
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getActivity().getAssets().open("skumaster.txt"), "UTF-8"));
            // do reading, usually loop until end of file reading
            while ((mLine = reader.readLine()) != null) {
                //Log.d(TAG, "DATAKU : " + mLine);
                String SkuCode, SkuDescription, SkuRetail, SkuType = "";
                SkuCode = mLine.substring(0, 9);
                SkuDescription = mLine.substring(9, 39);
                SkuRetail = mLine.substring(40, 53);
                SkuType = mLine.substring(53, 54);

                //insert into table sqlite sku master
                values.clear();
                values.put(DatabaseContract.NoteColumns.SKUID, SkuCode.trim());
                values.put(DatabaseContract.NoteColumns.DESCRIPTION, SkuDescription.trim());
                values.put(DatabaseContract.NoteColumns.RETAIL_PRICE, SkuRetail.trim());
                values.put(DatabaseContract.NoteColumns.SKUTYPE, SkuType.trim());
                long result = skuHelper.insert(values);
                Log.d(TAG, "RESULT : " + result + "-" + SkuCode);
                if (result > 0) {
                    Toast.makeText(getContext(), "successfully sku", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "failed sku", Toast.LENGTH_SHORT).show();
                }
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
                Log.d(TAG, "value delete: " + delquery);
                if (delquery == 0) {
                    Toast.makeText(getContext(), "Clear Data failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }


        String CurrId = "";
        String CurrDes = "";
        String CurrDate = "";
        String CurrRate = "";
        int i = 0;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getActivity().getAssets().open("curency.txt"), "UTF-8"));
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
                    if (result > 0) {
                        Toast.makeText(getContext(), "successfully currency", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "failed currency", Toast.LENGTH_SHORT).show();
                    }
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
                Log.d(TAG, "value delete: " + delquery);
                if (delquery == 0) {
                    Toast.makeText(getContext(), "Clear Data failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        String dateTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        dateTime = simpleDateFormat.format(calendar.getTime());
        Log.d(TAG, "time now:" + dateTime);

        values.clear();
        values.put(DatabaseContract.UpdateColumns.UPDATEDATE, CurrDate.trim());
        values.put(DatabaseContract.UpdateColumns.UPDATETIME, dateTime);
        long result = updateHelper.insert(values);
        if (result > 0) {
            Toast.makeText(getContext(), "successfully add update datetime", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "failed add update datetime", Toast.LENGTH_SHORT).show();
        }
        //------------------------------------------------------------------------------------------
    }

    private void showExistingPreference() {
        settingModel = settingPreference.getSetting();
        populateView(settingModel);
    }

    private void populateView(SettingModel settingModel) {
        hostname =settingModel.getHostName().isEmpty() ? "" : settingModel.getHostName();
        port =settingModel.getPort().isEmpty() ? "" : settingModel.getPort();
        username =settingModel.getUserName().isEmpty() ? "" : settingModel.getUserName();
        password =settingModel.getPassword().isEmpty() ? "" : settingModel.getPassword();

        Log.d(TAG, "HOSTNAME: "+hostname);
        Log.d(TAG, "PORT: "+port);
        Log.d(TAG, "USERNAME: "+username);
        Log.d(TAG, "PASSWORD: "+password);
    }
}