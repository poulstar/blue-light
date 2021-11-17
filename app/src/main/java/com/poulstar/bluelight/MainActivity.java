package com.poulstar.bluelight;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    Boolean hasPermission;
    BluetoothAdapter bluetoothAdapter;
    View root;
    TextView txtName, txtType;
    BottomNavigationView navView;
    View fragmentContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        SharedPreferences preferences = getSharedPreferences("My Pref", MODE_PRIVATE);
        hasPermission = preferences.getBoolean("has permission", false);
        Log.i("BLUETOOTH", "" + hasPermission);
        if(!hasPermission) {
            checkPermission();
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.layDevices, new MainFragment());

        root = findViewById(R.id.rootView);
        navView = findViewById(R.id.navView);
        fragmentContainer = findViewById(R.id.mainFragment);

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeBtn:
                        replaceFragment(new MainFragment());
                        break;
                    case R.id.settingBtn:
                        replaceFragment(new SettingFragment());
                        break;
                }
                return true;
            }
        });

    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFragment, fragment);
        fragmentTransaction.commit();
    }


    private void checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.i("BLUE", ""+checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION));
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                };
                requestPermissions(permissions, 5);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 5) {
            if(grantResults[0] == RESULT_OK) {
                SharedPreferences preferences = getSharedPreferences("My Pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("has permission", true);
                editor.commit();
            }
        }
    }
}
