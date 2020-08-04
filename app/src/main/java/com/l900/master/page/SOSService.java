package com.l900.master.page;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class SOSService extends Service {
    public SOSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
    }


    private LocationManager locationManager;
    private String provider ;
    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);
        provider = locationManager.getBestProvider(criteria, false);
        Log.e("1900","GPSIsOpen:"+GPSIsOpen());

    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Location location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider, 2000, 5, listener);
        if (location!=null)
            Log.e("1900","getLastKnownLocation :"+" 纬度："+location.getLatitude()+ " \n 经度："+location.getLongitude());
        Log.e("1900","onStartCommand:");
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean GPSIsOpen(){
        if (locationManager==null){
            return false;
        }
        return locationManager.isLocationEnabled();
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.e("1900","onLocationChanged:");
            if (location!=null){
                Log.e("1900","location:"+" 纬度："+location.getLatitude()+ " \n 经度："+location.getLongitude());
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("1900","onStatusChanged:");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("1900","onProviderEnabled:");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("1900","onProviderDisabled:");
        }
    };
}
