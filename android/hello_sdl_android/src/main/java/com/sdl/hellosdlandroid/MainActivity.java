package com.sdl.hellosdlandroid;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.BLUETOOTH_CONNECT;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (BuildConfig.TRANSPORT.equals("MULTI") || BuildConfig.TRANSPORT.equals("MULTI_HB")) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!checkPermission()) {
                    requestPermission();
                }
            } else {
                //If we are connected to a module we want to start our SdlService
                SdlReceiver.queryForConnectedService(this);
            }
        } else {
            Intent proxyIntent = new Intent(this, SdlService.class);
            startService(proxyIntent);
        }
    }

    private boolean checkPermission() {
        int btConnectPermission = ContextCompat.checkSelfPermission(getApplicationContext(), BLUETOOTH_CONNECT);

        return btConnectPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{BLUETOOTH_CONNECT}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean connectAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean scanAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (connectAccepted && scanAccepted) {
                        SdlReceiver.queryForConnectedService(this);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
