package com.l900.master.tools;


import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;

public class MyLocationManager {
    private static Context mContext;
    private LocationManager gpsLocationManager;
    private LocationManager networkLocationManager;
    private static final int MINTIME = 2000;
    private static final int MININSTANCE = 2;
    private static MyLocationManager instance;
    private Location lastLocation = null;
    private static LocationCallBack mCallback;

    public static void init(Context c, LocationCallBack callback) {
        mContext = c;
        mCallback = callback;
    }

    private MyLocationManager() {

        gpsLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location gpsLocation = gpsLocationManager
                .getLastKnownLocation(GPS_PROVIDER);
        gpsLocationManager.requestLocationUpdates(GPS_PROVIDER, MINTIME, MININSTANCE, locationListener);

        networkLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
         Location networkLocation = gpsLocationManager
                .getLastKnownLocation(GPS_PROVIDER);
        networkLocationManager.requestLocationUpdates(NETWORK_PROVIDER, MINTIME, MININSTANCE, locationListener);

    }

    public static MyLocationManager getInstance() {
        if (null == instance) {
            instance = new MyLocationManager();
        }
        return instance;
    }

    private void updateLocation(Location location) {
        lastLocation = location;
        mCallback.onCurrentLocation(location);
    }

    private final LocationListener locationListener = new LocationListener() {

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }

        public void onLocationChanged(Location location) {
            updateLocation(location);
        }
    };

    public Location getMyLocation() {
        return lastLocation;
    }

    public interface LocationCallBack {

        void onCurrentLocation(Location location);
    }

    public void destoryLocationManager() {
        gpsLocationManager.removeUpdates(locationListener);
        networkLocationManager.removeUpdates(locationListener);
    }
}
