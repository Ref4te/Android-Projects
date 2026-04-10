package com.Geo.lab12;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private TextView tvOut;
    private TextView tvLon;
    private TextView tvLat;
    private LocationManager mlocManager;
    private LocationListener mlocListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOut = (TextView) findViewById(R.id.textView1);
        tvLon = (TextView) findViewById(R.id.longitude);
        tvLat = (TextView) findViewById(R.id.latitude);

        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mlocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                tvLat.setText("Latitude: " + location.getLatitude());
                tvLon.setText("Longitude: " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {
                tvOut.setText("GPS is turned on...");
            }

            @Override
            public void onProviderDisabled(String provider) {
                tvOut.setText("GPS is not turned on...");
            }
        };

        checkLocationPermissions();
    }

    private void checkLocationPermissions() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            startGpsUpdates();
        }
    }

    private void startGpsUpdates() {
        try {
            mlocManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    10,
                    mlocListener
            );

            if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                tvOut.setText("GPS is turned on. Waiting for satellites...");
            } else {
                tvOut.setText("GPS is not turned on...");
            }
        } catch (SecurityException e) {
            tvOut.setText("Permission error: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGpsUpdates();
            } else {
                tvOut.setText("Permission denied. GPS won't work.");
                Toast.makeText(this, "Нужно разрешение для работы GPS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mlocManager != null) {
            mlocManager.removeUpdates(mlocListener);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startGpsUpdates();
        }
    }
}