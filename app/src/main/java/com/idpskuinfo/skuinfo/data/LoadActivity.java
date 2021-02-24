package com.idpskuinfo.skuinfo.data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.idpskuinfo.skuinfo.MainActivity;
import com.idpskuinfo.skuinfo.R;

import java.io.File;
import java.lang.ref.WeakReference;

import static com.idpskuinfo.skuinfo.data.SkuDataService.PREPARATION_MESSAGE;
import static com.idpskuinfo.skuinfo.data.SkuDataService.UPDATE_MESSAGE;
import static com.idpskuinfo.skuinfo.data.SkuDataService.FAILED_MESSAGE;
import static com.idpskuinfo.skuinfo.data.SkuDataService.SUCCESS_MESSAGE;
import static com.idpskuinfo.skuinfo.data.SkuDataService.CANCEL_MESSAGE;

public class LoadActivity extends AppCompatActivity implements HandlerCallback {
    Messenger mBoundService;
    boolean mServiceBound;
    private ProgressBar progressBar;
    public static final int NUMBER_RESULT = 101;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        unbindService(mServiceConnection);

        Intent intent=new Intent();
        intent.putExtra("MESSAGE", "DATA TERKIRIM");
        setResult(NUMBER_RESULT,intent);
        finish();
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

interface HandlerCallback {
    void preparation();
    void updateProgress(long progress);
    void loadSuccess();
    void loadFailed();
    void loadCancel();
}