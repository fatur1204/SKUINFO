package com.idpskuinfo.skuinfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.idpskuinfo.skuinfo.setting.SettingActivity;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Bundle bundle = new Bundle();
    private BottomNavigationView navigation;

    private boolean loadFragment(Fragment fragment) {
        Log.d(TAG, "frag_name : " + fragment);
        fragment.setArguments(bundle);
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bundle.putInt("message", 0);
        loadFragment(new Fragment_ScanBarcode());

        navigation = findViewById(R.id.navigationView);
        navigation.setOnNavigationItemSelectedListener(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle("Scan Barcode");
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_scan:
                bundle.putInt("message", 0);
                getSupportActionBar().setTitle("Scan Barcode");
                fragment = new Fragment_ScanBarcode();
                break;

            case R.id.navigation_rate:
                bundle.putInt("message", 0);
                getSupportActionBar().setTitle("Exchange Rate");
                fragment = new Fragment_ListRate();
                break;

            case R.id.navigation_update:
                getSupportActionBar().setTitle("Update");
                fragment = new Fragment_Update();
                break;
        }


        return loadFragment(fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_setting) {
            Intent mIntent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(mIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}