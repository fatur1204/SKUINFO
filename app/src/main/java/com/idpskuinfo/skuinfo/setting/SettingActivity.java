package com.idpskuinfo.skuinfo.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.idpskuinfo.skuinfo.R;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SettingActivity.class.getSimpleName();
    private EditText edtHostName, edtPort, edtUserName, edtPassword;
    private Button btnSave, btnCancel, btnAdd;
    private final String FIELD_REQUIRED = "Field tidak boleh kosong";

    private SettingModel settingModel;
    private SettingPreference settingPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        edtHostName = findViewById(R.id.edtHostname);
        edtPort = findViewById(R.id.edtport);
        edtUserName = findViewById(R.id.edtusername);
        edtPassword = findViewById(R.id.edtpassword);
        btnSave = findViewById(R.id.btnsave);
        btnCancel = findViewById(R.id.btncancel);
        btnAdd = findViewById(R.id.btnadd);

        setActionBarTitle("Setting");

        settingPreference = new SettingPreference(this);
        showExistingPreference();

        btnSave.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    private void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnsave) {
            Log.d(TAG, "SAVE DATE PROSES");
            String HostName = edtHostName.getText().toString().trim();
            String Port = edtPort.getText().toString().trim();
            String UserName = edtUserName.getText().toString().trim();
            String Password = edtPassword.getText().toString().trim();

            Log.d(TAG, "hostname:" + HostName + " port:" + Port + " username:" + UserName + " pass:" + Password);

            if (TextUtils.isEmpty(HostName)) {
                edtHostName.setError(FIELD_REQUIRED);
                return;
            }

            if (TextUtils.isEmpty(Port)) {
                edtPort.setError(FIELD_REQUIRED);
                return;
            }

            if (TextUtils.isEmpty(UserName)) {
                edtUserName.setError(FIELD_REQUIRED);
                return;
            }

            if (TextUtils.isEmpty(Password)) {
                edtPassword.setError(FIELD_REQUIRED);
                return;
            }

            saveSetting(HostName, Port, UserName, Password);
            btnAdd.setEnabled(true);
            btnSave.setEnabled(false);
            setActiveEdit(false);
        } else if (v.getId() == R.id.btnadd) {
            setActiveEdit(true);
            btnSave.setEnabled(true);
            btnAdd.setEnabled(false);
            edtHostName.requestFocus();
            edtHostName.selectAll();
        } else if (v.getId() == R.id.btncancel) {
            this.onBackPressed();
        }
    }

    private void showExistingPreference() {
        settingModel = settingPreference.getSetting();
        populateView(settingModel);
    }

    private void saveSetting(String hostname, String port, String username, String password) {
        SettingPreference userPreference = new SettingPreference(this);
        settingModel.setHostName(hostname);
        settingModel.setPort(port);
        settingModel.setUserName(username);
        settingModel.setPassword(password);
        userPreference.setSetting(settingModel);
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    private void populateView(SettingModel settingModel) {
        edtHostName.setText(settingModel.getHostName().isEmpty() ? "" : settingModel.getHostName());
        edtPort.setText(settingModel.getPort().isEmpty() ? "" : settingModel.getPort());
        edtUserName.setText(settingModel.getUserName().isEmpty() ? "" : settingModel.getUserName());
        edtPassword.setText(settingModel.getPassword().isEmpty() ? "" : settingModel.getPassword());

        btnSave.setEnabled(false);
        btnAdd.setText("Edit Data");
        setActiveEdit(false);
    }

    private void setActiveEdit(Boolean active) {
        if (active) {
            edtHostName.setEnabled(true);
            edtPort.setEnabled(true);
            edtUserName.setEnabled(true);
            edtPassword.setEnabled(true);
        } else {
            edtHostName.setEnabled(false);
            edtPort.setEnabled(false);
            edtUserName.setEnabled(false);
            edtPassword.setEnabled(false);
        }
    }
}