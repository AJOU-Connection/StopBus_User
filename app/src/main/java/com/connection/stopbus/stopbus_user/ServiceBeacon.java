package com.connection.stopbus.stopbus_user;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 서비스 클래스를 구현하려면, Service 를 상속받는다
public class ServiceBeacon extends Service{

    protected static final String TAG = "beacon";
    MinewBeaconManager mMinewBeaconManager;
    HashMap<String, KalmanFilter> mRssiMap = new HashMap<>();

    Handler mHandler = new Handler();

    private BluetoothAdapter btAdapter;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        mMinewBeaconManager = MinewBeaconManager.getInstance(this);
        mMinewBeaconManager.setDeviceManagerDelegateListener(new MinewBeaconManagerListener() {
            public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {
                Log.d(TAG, "onDisappearBeacons() called");
                for (MinewBeacon minewBeacon : minewBeacons) {
                    if(minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue().equals("N/A"))
                        continue;
                    String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                   //Toast.makeText(getApplicationContext(), deviceName + "  out range", Toast.LENGTH_SHORT).show();
                }
            }

            public void onAppearBeacons(List<MinewBeacon> minewBeacons) {
                Log.d(TAG, "onAppearBeacons() called");
            }

            @Override
            public void onUpdateState(BluetoothState state) {
                Log.d(TAG, "onUpdateState() called");
                switch (state) {
                    case BluetoothStatePowerOn:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOn", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothStatePowerOff:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOff", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            public void onRangeBeacons(final List<MinewBeacon> minewBeacons) {
                Log.d(TAG, "onRangeBeacons() called: "+ minewBeacons.size());

                String name;
                double rssi;
                double bus_dis= 0.0;
                double station_dis = 0.0;

                Shared_Pref.beacon_routeID = "";
                Shared_Pref.beacon_plateNo = "";
                Shared_Pref.beacon_stationID = "";
                Shared_Pref.beacon_stationNumber ="";

                if(btAdapter.isEnabled()){
                    if(minewBeacons.size()==0){
                        mMinewBeaconManager.startScan();
                    }

                }else{
                    minewBeacons.clear();
                }

                for(int i = 0 ; i < minewBeacons.size() ; i++){

                    name = minewBeacons.get(i).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    if(name.equals("N/A"))
                        continue;
                    rssi = minewBeacons.get(i).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getFloatValue();
                    if(!mRssiMap.containsKey(name)) {
                        mRssiMap.put(name, new KalmanFilter(0.0f));
                    }
                    rssi = mRssiMap.get(name).update(rssi);

                    //여기 이제 위치별 districtCd, stationNumber 받아와야함
                    if (name.substring(0, 3).equals("bus")) {
                        Shared_Pref.beacon_routeID = name.substring(3, 12);
                        Shared_Pref.beacon_plateNo = name.substring(13, 17);
                        bus_dis = calculateDistance(rssi);
                        Log.d("beacon", "beacon route id: " + name.substring(3, 12));
                        Log.d("beacon", "beacon bus name: " + name.substring(13, 17));
                    }else if (name.substring(0, 1).equals("s")) {
                        Shared_Pref.beacon_stationID = name.substring(1, 10);
                        Shared_Pref.beacon_stationNumber = name.substring(10, 15);
                        station_dis = calculateDistance(rssi);

                        Log.d("beacon", "beacon station id: " + name.substring(1, 10));
                        Log.d("beacon", "beacon station number:  " + name.substring(10, 15));

                        CallName("stationName");
                    }


                }
            }
        });
        mMinewBeaconManager.startScan();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG,"1123333333333333333");
        mMinewBeaconManager.stopScan();
        mMinewBeaconManager.startScan();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMinewBeaconManager.stopScan();

    }
    public double calculateDistance(double rssi) {
        int txPower = -59;

        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }
        double ratio = rssi*1.0/txPower;

        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }


    //[E] 비콘 ----------------------------------------------------------------------------------------------------------------
    public synchronized void CallName(final String api) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> args = new HashMap<String, String>();
                args.put("stationID",  Shared_Pref.beacon_stationID); //POST
                args.put("stationNumber",  Shared_Pref.beacon_stationNumber); //POST

                try {

                    final String response = NetworkService.INSTANCE.postQuery(api, args);
                    Log.d("sb","333333"+response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                JSONObject obj = new JSONObject(response).getJSONObject("body");   // JSONArray 생성
                                Log.d("sb","obj: "+obj);


                                Shared_Pref.beacon_stationName = obj.optString("stationName");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                } catch (Exception e) {
                }
            }
        }).start();

    }


}