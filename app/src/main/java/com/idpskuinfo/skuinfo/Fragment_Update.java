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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Fragment_Update extends Fragment implements View.OnClickListener {
    private static final String TAG = Fragment_Update.class.getSimpleName();
    Button btnupdate;
    private Fragment_Update fragment_update;
    SkuHelper skuHelper;
    CurrencyHelper currencyHelper;
    UpdateHelper updateHelper;
    ContentValues values = new ContentValues();
    EditText edtData;

    public Fragment_Update(){
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
    }

    @Override
    public void onClick(View v) {
        /*values.put(DatabaseContract.NoteColumns.SKUID,"110111");
        values.put(DatabaseContract.NoteColumns.DESCRIPTION,"FATHUR SKU");
        values.put(DatabaseContract.NoteColumns.RETAIL_PRICE,"10000");
        values.put(DatabaseContract.NoteColumns.SKUTYPE, "L");
        long result = skuHelper.insert(values);
        Log.d(TAG, "RESULT : " + result);
        if(result > 0){
            Toast.makeText(getContext(),"successfully sku", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(),"failed sku", Toast.LENGTH_SHORT).show();
        }

        values.clear();

        //add data currency
        values.put(DatabaseContract.CurrColumns.CURRID,"IDR");
        values.put(DatabaseContract.CurrColumns.CURRDATE,"17-02-2021");
        values.put(DatabaseContract.CurrColumns.CUR_RET,"14500");
        long result_cur = currencyHelper.insert(values);
        Log.d(TAG, "RESULT : " + result_cur);
        if(result_cur > 0){
            Toast.makeText(getContext(),"successfully currency", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(),"failed currency", Toast.LENGTH_SHORT).show();
        }

        values.clear();

        //add data currency
        values.put(DatabaseContract.UpdateColumns.UPDATEDATE,"17-02-2021");
        values.put(DatabaseContract.UpdateColumns.UPDATETIME,"21:58:10");
        long result_update = updateHelper.insert(values);
        Log.d(TAG, "RESULT : " + result_update);
        if(result_cur > 0){
            Toast.makeText(getContext(),"successfully UPDATE", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(),"failed UPDATE", Toast.LENGTH_SHORT).show();
        }*/

        //clear data SKUMASTER
        Cursor qrycek = skuHelper.queryAll();
        if(qrycek.getCount() >0){
            if(qrycek !=null){
                long delquery =  skuHelper.deleteAll();
                Log.d(TAG, "value delete: " + delquery);
                if(delquery ==0){
                    Toast.makeText(getContext(),"Clear Data failed!", Toast.LENGTH_SHORT).show();
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
                SkuCode =mLine.substring(0,9);
                SkuDescription = mLine.substring(9,39);
                SkuRetail = mLine.substring(40,53);
                SkuType = mLine.substring(53,54);


                //insert into table sqlite sku master
                values.clear();
                values.put(DatabaseContract.NoteColumns.SKUID,SkuCode.trim());
                values.put(DatabaseContract.NoteColumns.DESCRIPTION,SkuDescription.trim());
                values.put(DatabaseContract.NoteColumns.RETAIL_PRICE,SkuRetail.trim());
                values.put(DatabaseContract.NoteColumns.SKUTYPE, SkuType.trim());
                long result = skuHelper.insert(values);
                Log.d(TAG, "RESULT : " + result + "-" + SkuCode);
                if(result > 0){
                    Toast.makeText(getContext(),"successfully sku", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"failed sku", Toast.LENGTH_SHORT).show();
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
    }
}