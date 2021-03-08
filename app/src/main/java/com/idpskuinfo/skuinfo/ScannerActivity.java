package com.idpskuinfo.skuinfo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements View.OnClickListener, ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private FloatingActionButton fabFlashLight, fabclose;
    public static final Integer EXTRA_DATA = 101;
    private Boolean IsActive = false;
    private static final String TAG = ScannerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        mScannerView = findViewById(R.id.zxscan);
        fabclose = findViewById(R.id.fabclose);
        fabFlashLight = findViewById(R.id.fabflashlight);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        fabFlashLight.setOnClickListener(this);
        fabclose.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "camera used");

        if (ContextCompat.checkSelfPermission(ScannerActivity.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    50);
        }

        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mScannerView !=null){
            Log.d(TAG, "camera release");
            mScannerView.stopCameraPreview();
            mScannerView.stopCamera();
            mScannerView = null;
        }
        Log.d(TAG, "onPause");
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fabflashlight){
            if (!IsActive) {
                IsActive = true;
                fabFlashLight.setImageResource(R.drawable.ic_flash_on);
                mScannerView.setFlash(true);
            } else {
                IsActive = false;
                fabFlashLight.setImageResource(R.drawable.ic_flash_off);
                mScannerView.setFlash(false);
            }
        }else if(v.getId() == R.id.fabclose){
            mScannerView.stopCameraPreview();
            mScannerView.stopCamera();
            onBackPressed();
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.v("TAG", rawResult.getText()); // Prints scan results
        Log.v("TAG", rawResult.getBarcodeFormat().toString());
        Intent resultIntent = new Intent();
        resultIntent.putExtra("RESULT", rawResult.getText());
        setResult(EXTRA_DATA, resultIntent);
        mScannerView.resumeCameraPreview(this);
        finish();
    }
}