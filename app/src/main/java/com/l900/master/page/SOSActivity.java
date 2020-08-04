package com.l900.master.page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.location.PoiRegion;
import com.com.baidu.location.service.LocationService;
import com.l900.master.R;
import com.l900.master.tools.DatabaseHelper;
import com.l900.master.tools.EmergencyContact;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SOSActivity extends Activity implements View.OnClickListener {

    private static long NUMBER_110=110L;
    private static long NUMBER_119=119L;
    private static long NUMBER_120=120L;

    private ListView mEmergencyListView;
    private DatabaseHelper mDatabaseHelper;
    private List<EmergencyContact> mData;
    private EmergencyContactAdapter mEmergencyContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SDKInitializer.initialize(getApplicationContext());
        String sha1 = sHA1(this);
        Log.e("1900"," sha1 "+  sha1);
        setContentView(R.layout.layout_sos);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        decorView.setSystemUiVisibility(uiOptions);

        findViewById(R.id.ll_exit).setOnClickListener(this);
        findViewById(R.id.btn_exit_sos).setOnClickListener(this);
        findViewById(R.id.rl_110).setOnClickListener(this);
        findViewById(R.id.rl_119).setOnClickListener(this);
        findViewById(R.id.rl_120).setOnClickListener(this);
        findViewById(R.id.btn_send_sos_msg).setOnClickListener(this);
        mEmergencyListView = findViewById(R.id.sos_emergency_list);
        mDatabaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        initData();

        mEmergencyContactAdapter = new EmergencyContactAdapter(mData,this.getApplicationContext());
        mEmergencyListView.setAdapter(mEmergencyContactAdapter);
        mEmergencyListView.setOnItemClickListener(itemClick);
    }

    private LocationService locationService;

    @Override
    protected void onResume() {
        super.onResume();

        locationService = new LocationService(this);
        locationService.registerListener(mListener);
        LocationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationService.unregisterListener(mListener);
        locationService.stop();

    }

    private void initData() {
        Cursor cursor = mDatabaseHelper.query();
        if (mData==null){
            mData = new ArrayList<>();
        }else {
            mData.clear();
        }
        if (cursor.getCount() != 0){
            while(cursor.moveToNext()){
                String tempName = cursor.getString(cursor.getColumnIndex("name"));
                String tempTel = cursor.getString(cursor.getColumnIndex("tel"));
                Log.e("1900","initData  tel:"+ tempTel+" name :"+tempName);
                mData.add(new EmergencyContact(tempName,tempTel));
            }
        }
        Log.e("1900","msg: getCount:"+cursor.getCount());
        Log.e("1900","msg: size:"+mData.size());
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit_sos:
            case R.id.ll_exit:
                SOSActivity.this.finish();
                break;
            case R.id.rl_110:
                call(NUMBER_110);
                break;
            case R.id.rl_119:
                call(NUMBER_119);
                break;
            case R.id.rl_120:
                call(NUMBER_120);
                break;
        }
    }

    private void call(long number){
        Intent call = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse(String.format("tel:%d", number));

        call.setData(data);
        call.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    
	}

    AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(id == -1) {
                return;
            }
            Log.e("1900","click:position:"+position);
            EmergencyContact contact = mData.get(position);
            Log.e("1900","contact:name:"+contact.getName()+" tel:"+contact.getTel());
        }
    };

    class EmergencyContactAdapter extends BaseAdapter {

        private List<EmergencyContact> list;
        private Context mContext;

        public EmergencyContactAdapter(List<EmergencyContact> list, Context context) {
            this.list = list;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder ;
            if (convertView==null){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.emergency_contact_list,parent,false);
                holder = new ViewHolder();

                holder.nameView = convertView.findViewById(R.id.name);
                holder.telView = convertView.findViewById(R.id.tel);
                holder.ib_del = convertView.findViewById(R.id.ib_del);
                convertView.setTag(holder);
            }else {
                holder = ( ViewHolder)convertView.getTag();
            }
            EmergencyContact emergencyContact = list.get(position);
            holder.nameView.setText(emergencyContact.getName());
            holder.telView.setText(emergencyContact.getTel());
            emergencyContact.getTel().replaceAll(" ","");
            holder.ib_del.setVisibility(View.INVISIBLE);
            return convertView;
        }

        class ViewHolder{
            TextView nameView;
            TextView telView;
            ImageButton ib_del;
        }
    }


    public String sHA1(Context context){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        /**
         * 定位请求回调函数
         * @param location 定位结果
         */
        @Override
        public void onReceiveLocation(BDLocation location) {

            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                int tag = 1;
//                try {
//                    List<Address>  addresses = geocoder.getFromLocation(location.getLatitude() ,location.getLongitude() ,1);
//                    Log.e("19001","addresses:"+(addresses.size()));
//                    if (addresses!=null){
//                        for (Address a : addresses){
//                             Log.e("19001","address:"+a.getCountryName()+a.getFeatureName()+a.getLocality());
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度

                sb.append(location.getLatitude());
                sb.append("\nlongtitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nProvince : ");// 获取省份
                sb.append(location.getProvince());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nTown : ");// 获取镇信息
                sb.append(location.getTown());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nStreetNumber : ");// 获取街道号码
                sb.append(location.getStreetNumber());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = location.getPoiList().get(i);
                        sb.append("poiName:");
                        sb.append(poi.getName() + ", ");
                        sb.append("poiTag:");
                        sb.append(poi.getTags() + "\n");
                    }
                }
                if (location.getPoiRegion() != null) {
                    sb.append("PoiRegion: ");// 返回定位位置相对poi的位置关系，仅在开发者设置需要POI信息时才会返回，在网络不通或无法获取时有可能返回null
                    PoiRegion poiRegion = location.getPoiRegion();
                    sb.append("DerectionDesc:"); // 获取POIREGION的位置关系，ex:"内"
                    sb.append(poiRegion.getDerectionDesc() + "; ");
                    sb.append("Name:"); // 获取POIREGION的名字字符串
                    sb.append(poiRegion.getName() + "; ");
                    sb.append("Tags:"); // 获取POIREGION的类型
                    sb.append(poiRegion.getTags() + "; ");
                    sb.append("\nSDK版本: ");
                }
                sb.append(locationService.getSDKVersion()); // 获取SDK版本
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }

                Log.e("1900",sb.toString());
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            super.onConnectHotSpotMessage(s, i);
        }

        /**
         * 回调定位诊断信息，开发者可以根据相关信息解决定位遇到的一些问题
         * @param locType 当前定位类型
         * @param diagnosticType 诊断类型（1~9）
         * @param diagnosticMessage 具体的诊断信息释义
         */
        @Override
        public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
            super.onLocDiagnosticMessage(locType, diagnosticType, diagnosticMessage);

            StringBuffer sb = new StringBuffer(256);
            sb.append("诊断结果: ");
            if (locType == BDLocation.TypeNetWorkLocation) {
                if (diagnosticType == 1) {
                    sb.append("网络定位成功，没有开启GPS，建议打开GPS会更好");
                    sb.append("\n" + diagnosticMessage);
                } else if (diagnosticType == 2) {
                    sb.append("网络定位成功，没有开启Wi-Fi，建议打开Wi-Fi会更好");
                    sb.append("\n" + diagnosticMessage);
                }
            } else if (locType == BDLocation.TypeOffLineLocationFail) {
                if (diagnosticType == 3) {
                    sb.append("定位失败，请您检查您的网络状态");
                    sb.append("\n" + diagnosticMessage);
                }
            } else if (locType == BDLocation.TypeCriteriaException) {
                if (diagnosticType == 4) {
                    sb.append("定位失败，无法获取任何有效定位依据");
                    sb.append("\n" + diagnosticMessage);
                } else if (diagnosticType == 5) {
                    sb.append("定位失败，无法获取有效定位依据，请检查运营商网络或者Wi-Fi网络是否正常开启，尝试重新请求定位");
                    sb.append(diagnosticMessage);
                } else if (diagnosticType == 6) {
                    sb.append("定位失败，无法获取有效定位依据，请尝试插入一张sim卡或打开Wi-Fi重试");
                    sb.append("\n" + diagnosticMessage);
                } else if (diagnosticType == 7) {
                    sb.append("定位失败，飞行模式下无法获取有效定位依据，请关闭飞行模式重试");
                    sb.append("\n" + diagnosticMessage);
                } else if (diagnosticType == 9) {
                    sb.append("定位失败，无法获取任何有效定位依据");
                    sb.append("\n" + diagnosticMessage);
                }
            } else if (locType == BDLocation.TypeServerError) {
                if (diagnosticType == 8) {
                    sb.append("定位失败，请确认您定位的开关打开状态，是否赋予APP定位权限");
                    sb.append("\n" + diagnosticMessage);
                }
            }
            Log.e("1900",sb.toString());
        }
    };
}
