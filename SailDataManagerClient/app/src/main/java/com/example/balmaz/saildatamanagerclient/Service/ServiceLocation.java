package com.example.balmaz.saildatamanagerclient.Service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;

import com.example.balmaz.saildatamanagerclient.Location.BoatLocationManager;

/**
 * Created by balmaz on 2018. 04. 16..
 */

public class ServiceLocation extends Service implements LocationListener {
    public static final String BR_NEW_LOCATION = "BR_NEW_LOCATION";
    public static final String KEY_LOCATION = "KEY_LOCATION";

    private BoatLocationManager boatLocationManager = null;
    private boolean locationMonitorRunning = false;

    private Location firstLocation = null;
    private Location lastLocation = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        firstLocation = null;

        if (!locationMonitorRunning) {
            locationMonitorRunning = true;
            boatLocationManager = new BoatLocationManager(getApplicationContext(), this);
            boatLocationManager.startLocationMonitoring();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (boatLocationManager != null) {
            boatLocationManager.stopLocationMonitoring();
        }

        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (firstLocation == null) {
            firstLocation = location;
        }
        lastLocation = location;

        Intent intent = new Intent(BR_NEW_LOCATION);
        intent.putExtra(KEY_LOCATION, location);
        sendBroadcast(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TBD
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TBD
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TBD
    }
}

