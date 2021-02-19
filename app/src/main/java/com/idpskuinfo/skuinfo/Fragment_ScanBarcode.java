package com.idpskuinfo.skuinfo;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.idpskuinfo.skuinfo.db.CurrencyHelper;
import com.idpskuinfo.skuinfo.db.SkuHelper;
import com.idpskuinfo.skuinfo.db.UpdateHelper;

public class Fragment_ScanBarcode extends Fragment implements View.OnClickListener {
    EditText edtSkuCode;
    Button btnSearchData;
    SkuHelper skuHelper;
    CurrencyHelper currencyHelper;
    UpdateHelper updateHelper;
    TextView TxtSkuNumber, TxtSkuDescription, TxtSkuRetail;
    TextView TxtLastRate, TxtLastUpdate, TxtTimeUpdate;
    private String skuCode;
    private static final String TAG = Fragment_ScanBarcode.class.getSimpleName();

    public Fragment_ScanBarcode() {
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
        return inflater.inflate(R.layout.fragment__scan_barcode, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //open table sqlite
        skuHelper = SkuHelper.getInstance(getContext());
        skuHelper.open();
        currencyHelper = CurrencyHelper.getInstance(getContext());
        currencyHelper.open();
        updateHelper = UpdateHelper.getInstance(getContext());
        updateHelper.open();
        //------------------------------------------------------------------------------------------

        edtSkuCode = view.findViewById(R.id.edtskucode);
        btnSearchData = view.findViewById(R.id.btnsearch);
        TxtSkuNumber = view.findViewById(R.id.edtskunumber);
        TxtSkuDescription = view.findViewById(R.id.edtskudescription);
        TxtSkuRetail = view.findViewById(R.id.edtretailidr);
        TxtLastRate = view.findViewById(R.id.edtlastrate);
        TxtLastUpdate = view.findViewById(R.id.edtlastupdate);
        TxtTimeUpdate = view.findViewById(R.id.edttimeupdate);

        TxtSkuNumber.setText("Sku Number");
        TxtSkuDescription.setText("Sku Description");
        TxtSkuRetail.setText(String.format("%,.2f", Float.valueOf("0")));

        Cursor cur_rate = currencyHelper.queryById("USD");
        if (cur_rate.getCount() > 0) {
            if (cur_rate != null) {
                cur_rate.moveToFirst();
                TxtLastRate.setText(String.format("%,.2f", Float.valueOf(cur_rate.getString(cur_rate.getColumnIndex("curret")))));
            }
        } else {
            TxtLastRate.setText("0");
        }

        Cursor cur_update = updateHelper.queryAll();
        if (cur_update.getCount() > 0) {
            if (cur_update != null) {
                cur_update.moveToFirst();
                TxtLastUpdate.setText(cur_update.getString(cur_update.getColumnIndex("updatedate")));
                TxtTimeUpdate.setText(cur_update.getString(cur_update.getColumnIndex("updatetime")));
            }
        } else {
            TxtLastUpdate.setText("Not Found");
            TxtTimeUpdate.setText("Not Found");
        }

        currencyHelper.close();
        updateHelper.close();

        btnSearchData.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnsearch) {
            if (edtSkuCode.length() == 0) {
                edtSkuCode.requestFocus();
                edtSkuCode.setError("Upc/SKU Can't empty!");
            } else {
                Cursor cursor = skuHelper.queryById(String.valueOf(edtSkuCode.getText()));
                Log.d(TAG, "counter : " + cursor.getCount());
                if (cursor.getCount() > 0) {
                    if (cursor != null) {
                        cursor.moveToFirst();

                        //VIEW DATA
                        TxtSkuNumber.setText(cursor.getString(cursor.getColumnIndex("skuid")));
                        TxtSkuDescription.setText(cursor.getString(cursor.getColumnIndex("description")));
                        TxtSkuRetail.setText(String.format("%,.0f", Float.valueOf(cursor.getString(cursor.getColumnIndex("retailprice")))));
                        //----------------

                    } else {
                        TxtSkuNumber.setText("Sku Number");
                        TxtSkuDescription.setText("Sku Description");
                        TxtSkuRetail.setText(String.format("%,.0f", Float.valueOf("0")));
                        Toast.makeText(getContext(), "Cannot open data!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Data Not Match!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}