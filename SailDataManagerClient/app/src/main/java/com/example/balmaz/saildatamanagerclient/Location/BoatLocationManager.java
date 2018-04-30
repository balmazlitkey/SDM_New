package com.example.balmaz.saildatamanagerclient.Location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by balmaz on 2018. 04. 16..
 */

public class BoatLocationManager {

    private Context mContext;
    private LocationListener mListener;
    private LocationManager mLocMan;

    public BoatLocationManager(Context aContext, LocationListener listener) {
        mContext = aContext;
        this.mListener = listener;
        mLocMan = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startLocationMonitoring() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocMan.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        100, 100, mListener);
            }
        }
    }

    public void stopLocationMonitoring() {
        if (mLocMan != null) {
            mLocMan.removeUpdates(mListener);
        }
    }
}
