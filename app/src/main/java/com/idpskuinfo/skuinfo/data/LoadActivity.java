package com.idpskuinfo.skuinfo.data;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.idpskuinfo.skuinfo.R;

import java.io.File;
import java.lang.ref.WeakReference;

import static com.idpskuinfo.skuinfo.data.SkuDataService.CANCEL_MESSAGE;
import static com.idpskuinfo.skuinfo.data.SkuDataService.FAILED_MESSAGE;
import static com.idpskuinfo.skuinfo.data.SkuDataService.PREPARATION_MESSAGE;
import static com.idpskuinfo.skuinfo.data.SkuDataService.SUCCESS_MESSAGE;
import static com.idpskuinfo.skuinfo.data.SkuDataService.UPDATE_MESSAGE;

interface HandlerCallback {
    void preparation();

    void updateProgress(long progress);

    void loadSuccess();

    void loadFailed();

    void loadCancel();
}

public class LoadActivity extends AppCompatActivity implements HandlerCallback {
    public static final int NUMBER_RESULT = 101;
    Messenger mBoundService;
    boolean mServiceBound;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = new Messenger(service);
            mServiceBound = true;
        }
    };
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_load);

        progressBar = findViewById(R.id.progress_bar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Intent mBoundServiceIntent = new Intent(LoadActivity.this, SkuDataService.class);
        Messenger mActivityMessenger = new Messenger(new IncomingHandler(this));
        mBoundServiceIntent.putExtra(SkuDataService.ACTIVITY_HANDLER, mActivityMessenger);
        bindService(mBoundServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        File file_sku = new File(getFilesDir().toString(), "skumaster.txt");
        file_sku.delete();
        File file_rate = new File(getFilesDir().toString(), "skurate.txt");
        file_rate.delete();

        showSystemUI();
        unbindService(mServiceConnection);

        Intent intent = new Intent();
        intent.putExtra("MESSAGE", "DATA TERKIRIM");
        setResult(NUMBER_RESULT, intent);
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void preparation() {
        Toast.makeText(this, "Load Data start", Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateProgress(long progress) {
        Log.e("PROGRESS", "updateProgress: " + progress);
        progressBar.setProgress((int) progress);
    }

    @Override
    public void loadSuccess() {
        Toast.makeText(this, "Load data success", Toast.LENGTH_LONG).show();
        //startActivity(new Intent(MainActivity.this, MahasiswaActivity.class));
        finish();
    }

    @Override
    public void loadFailed() {
        Toast.makeText(this, "Load data failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void loadCancel() {
        finish();
    }


    private static class IncomingHandler extends Handler {
        WeakReference<HandlerCallback> weakCallback;

        IncomingHandler(HandlerCallback callback) {
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PREPARATION_MESSAGE:
                    weakCallback.get().preparation();
                    break;
                case UPDATE_MESSAGE:
                    Bundle bundle = msg.getData();
                    long progress = bundle.getLong("KEY_PROGRESS");
                    weakCallback.get().updateProgress(progress);
                    break;
                case SUCCESS_MESSAGE:
                    weakCallback.get().loadSuccess();
                    break;
                case FAILED_MESSAGE:
                    weakCallback.get().loadFailed();
                    break;
                case CANCEL_MESSAGE:
                    weakCallback.get().loadCancel();
                    break;
            }
        }
    }
}