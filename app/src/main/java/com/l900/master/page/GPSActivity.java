package com.l900.master.page;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.l900.master.R;
import com.l900.master.tools.MyLocationManager;

public class GPSActivity extends Activity  {

    private TextView desText;
    private MyLocationManager mLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        desText = this.findViewById(R.id.text);IntentFilter filter = new IntentFilter();
        filter.addAction("com.starmini.location.action");
        registerReceiver(mReceiver,filter);
        String x = String.valueOf(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        Log.e("19001","x:"+x);
    }

    Intent services ;
    @Override
    protected void onResume() {
        super.onResume();
        services = new Intent(this,SosLocationService.class);
        startService(services);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        stopService(services);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.starmini.location.action")){
                Log.e("1900","action:"+intent.getAction());
                int type = intent.getIntExtra("type",0);
                double longtitude = intent.getDoubleExtra("longtitude",0.0);
                double latitude = intent.getDoubleExtra("latitude",0.0);
                String locationDescribe,addr;
                if (type==2){

                    locationDescribe = intent.getStringExtra("locationDescribe");
                    addr = intent.getStringExtra("addr");
                    desText.setText("网络定位： longtitude: "+longtitude + " latitude :"+latitude+"  type "+type +"  locationDescribe:"+locationDescribe+" addr:"+addr);
                    Log.e("1900"," longtitude: "+longtitude + " latitude :"+latitude+"  type "+type +"  locationDescribe:"+locationDescribe+" addr:"+addr);
                }else {
                    Log.e("1900"," longtitude: "+longtitude + " latitude :"+latitude+"  type "+type);
                    desText.setText("离线定位： longtitude: "+longtitude + " latitude :"+latitude+"  type "+type  );
                }
            }
        }
    };
}
