package com.idpskuinfo.skuinfo.data;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.idpskuinfo.skuinfo.db.CurrencyHelper;
import com.idpskuinfo.skuinfo.db.DatabaseContract;
import com.idpskuinfo.skuinfo.db.SkuHelper;
import com.idpskuinfo.skuinfo.db.UpdateHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

interface LoadDataCallback {
    void onPreLoad();
    void onProgressUpdate(long progress);
    void onLoadSuccess();
    void onLoadFailed();
    void onLoadCancel();
}

public class SkuDataService extends Service {
    public static final int PREPARATION_MESSAGE = 0;
    public static final int UPDATE_MESSAGE = 1;
    public static final int SUCCESS_MESSAGE = 2;
    public static final int FAILED_MESSAGE = 3;
    public static final int CANCEL_MESSAGE = 4;
    public static final String ACTIVITY_HANDLER = "activity_handler";
    private String TAG = SkuDataService.class.getSimpleName();
    private Messenger mActivityMessenger;
    private final LoadDataCallback myCallback = new LoadDataCallback() {
        @Override
        public void onPreLoad() {
            sendMessage(PREPARATION_MESSAGE);
        }

        @Override
        public void onLoadCancel() {
            sendMessage(CANCEL_MESSAGE);
        }

        @Override
        public void onProgressUpdate(long progress) {
            try {
                Message message = Message.obtain(null, UPDATE_MESSAGE);
                Bundle bundle = new Bundle();
                bundle.putLong("KEY_PROGRESS", progress);
                message.setData(bundle);
                mActivityMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLoadSuccess() {
            sendMessage(SUCCESS_MESSAGE);
        }

        @Override
        public void onLoadFailed() {
            sendMessage(FAILED_MESSAGE);
        }
    };
    private LoadDataAsync loadData;

    public SkuDataService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        loadData = new LoadDataAsync(this, myCallback);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loadData.cancel(true);
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        mActivityMessenger = intent.getParcelableExtra(ACTIVITY_HANDLER);
        loadData.execute();
        return mActivityMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        loadData.cancel(true);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind: ");
    }

    public void sendMessage(int messageStatus) {
        Message message = Message.obtain(null, messageStatus);
        try {
            mActivityMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static class LoadDataAsync extends AsyncTask<Void, Integer, Boolean> {
        private static final double MAX_PROGRESS = 100;
        private final String TAG = LoadDataAsync.class.getSimpleName();
        private final WeakReference<Context> context;
        private final WeakReference<LoadDataCallback> weakCallback;
        private ContentValues values = new ContentValues();

        LoadDataAsync(Context context, LoadDataCallback callback) {
            this.context = new WeakReference<>(context);
            this.weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            weakCallback.get().onPreLoad();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SkuHelper skuHelper = SkuHelper.getInstance(context.get());
            CurrencyHelper currencyHelper = CurrencyHelper.getInstance(context.get());
            UpdateHelper updateHelper = UpdateHelper.getInstance(context.get());
            Boolean firstRun = true;
            if (firstRun) {
                ArrayList<SkuModel> skuModels = preLoadRaw();
                skuHelper.open();
                skuHelper.deleteAll();
                double progress = 30;
                publishProgress((int) progress);
                double progressMaxInsert = 80.0;
                double progressDiff = (progressMaxInsert - progress) / skuModels.size();
                boolean isInsertSuccess;

                //query insert currency-------------------------------------------------------------
                currencyHelper.open();
                currencyHelper.deleteAll();
                BufferedReader reader = null;
                String mLine = "";

                String CurrId = "";
                String CurrDes = "";
                String CurrDate = "";
                String CurrRate = "";
                int i = 0;
                try {
                    File file = new File(context.get().getFilesDir().toString(), "skurate.txt");
                    reader = new BufferedReader(new FileReader(file));
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
                        }
                        i++;
                    }
                    Log.d(TAG, "insert currency");
                    currencyHelper.close();
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
                //Load data Currency End------------------------------------------------------------

                //insert into last update
                updateHelper.open();
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
                Log.d(TAG, "insert update");
                updateHelper.close();
                //----------------------------------------------------------------------------------


                //Gunakan ini untuk insert query dengan menggunakan standar query
                try {
                    skuHelper.beginTransaction();
                    for (SkuModel model : skuModels) {
                        if (isCancelled()) {
                            break;
                        } else {
                            skuHelper.insertTransaction(model);
                            progress += progressDiff;
                            publishProgress((int) progress);
                        }
                    }

                    if (isCancelled()) {
                        isInsertSuccess = false;
                        //appPreference.setFirstRun(true);
                        weakCallback.get().onLoadCancel();
                    } else {
                        skuHelper.setTransactionSuccess();
                        isInsertSuccess = true;
                        //appPreference.setFirstRun(false);
                    }
                } catch (Exception e) {
                    // Jika gagal maka do nothing
                    Log.e(TAG, "doInBackground: Exception");
                    isInsertSuccess = false;
                } finally {
                    skuHelper.endTransaction();
                }
                //akhir dari standar query
                skuHelper.close();
                publishProgress((int) MAX_PROGRESS);
                return isInsertSuccess;
            } else {
                try {
                    synchronized (this) {
                        this.wait(2000);
                        publishProgress(50);
                        this.wait(2000);
                        publishProgress((int) MAX_PROGRESS);
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            weakCallback.get().onProgressUpdate(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                weakCallback.get().onLoadSuccess();
            } else {
                weakCallback.get().onLoadFailed();
            }
        }

        private ArrayList<SkuModel> preLoadRaw() {
            ArrayList<SkuModel> skuModels = new ArrayList<>();
            String line;
            BufferedReader reader;
            try {
                File file = new File(context.get().getFilesDir().toString(), "skumaster.txt");
                reader = new BufferedReader(new FileReader(file));
                while ((line = reader.readLine()) != null) {
                    //Log.d(TAG, "DATAKU : "+ line);
                    String[] splitstr = line.split(",");
                    SkuModel skuModel;
                    skuModel = new SkuModel();
                    skuModel.setSkucode(splitstr[0].trim());
                    skuModel.setSkudes(splitstr[1].trim());
                    skuModel.setSkuret(splitstr[2].trim());
                    skuModel.setSkutype(splitstr[3].trim());
                    skuModels.add(skuModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return skuModels;
        }
    }
}