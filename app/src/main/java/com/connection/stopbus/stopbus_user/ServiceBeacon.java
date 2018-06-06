package com.connection.stopbus.stopbus_user;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 서비스 클래스를 구현하려면, Service 를 상속받는다
public class ServiceBeacon extends Service implements BeaconConsumer{

    private final MyHandler mHandler = new MyHandler(this);

    private BeaconManager beaconManager;
    private BluetoothAdapter btAdapter;


    //감지된 비콘들을 임시로 담을 리스트
    private List<Beacon> beaconList = new ArrayList<>();


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        //비콘매니저 객체 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this);

        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:23-24"));
        //위 숫자가 detect 숫자. 바꾸면 인식안됨
        beaconManager.bind(this);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //인식 시작
        mHandler.sendEmptyMessage(0);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    //[S] 비콘 ----------------------------------------------------------------------------------------------------------------

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for(Beacon beacon : beacons){
                        beaconList.add(beacon);

                        Log.d("beacon","List : " + beaconList);

                    }
                    Log.d("beacon", "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private static class MyHandler extends Handler {
        private final WeakReference<ServiceBeacon> mActivity;

        public MyHandler(ServiceBeacon activity) {
            mActivity = new WeakReference<ServiceBeacon>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ServiceBeacon activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }

    }

    public void handleMessage(Message msg){
        Log.d("beacon", "beaconlist: "+ beaconList);

        Log.d("beacon", "msg: "+ msg);

        if(btAdapter.isEnabled()){
            Shared_Pref.btenable = 1;
        }else{
            Shared_Pref.btenable = 0;
            beaconList.clear();

        }
        //비콘의 아이디와 거리를 측정하여 textvIEW에 띄움
        for(Beacon beacon : beaconList){


            if(beaconList.toString().equals("[]")){
                Log.d("beacon","no");

            }else{
                Shared_Pref.STATUS =1;
                Log.d("beacon","yes");
                //여기 이제 위치별 districtCd, stationNumber 받아와야함
                if(beacon.getBluetoothName().substring(0,3).equals("bus")){
                    Shared_Pref.routeID = beacon.getBluetoothName().substring(3,12);
                    Shared_Pref.plateNo = beacon.getBluetoothName().substring(13,17);
                    Log.d("beacon","beacon route id: "+ beacon.getBluetoothName().substring(3,12));
                    Log.d("beacon","beacon bus name: "+ beacon.getBluetoothName().substring(13,17));

                }else if(beacon.getBluetoothName().substring(0,1).equals("s")){
                    Shared_Pref.stationID= beacon.getBluetoothName().substring(1,10);
                    Shared_Pref.stationNumber =beacon.getBluetoothName().substring(10,15);
                    Log.d("beacon","beacon station id: "+ beacon.getBluetoothName().substring(1,10));
                    Log.d("beacon","beacon station number:  "+ beacon.getBluetoothName().substring(10,15));

                    CallName("stationName");
                }
            }

            Log.d("beacon","name : " + beacon.getBluetoothName() + " / " + "ID2 : " + beacon.getId2() + " / " + String.valueOf(beacon.getDistance()));

        }

        mHandler.sendEmptyMessageDelayed(0,1000);

    }


    //[E] 비콘 ----------------------------------------------------------------------------------------------------------------
    public synchronized void CallName(final String api) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> args = new HashMap<String, String>();
                args.put("stationID",  Shared_Pref.stationID); //POST
                args.put("stationNumber",  Shared_Pref.stationNumber); //POST

                try {

                    final String response = NetworkService.INSTANCE.postQuery(api, args);
                    Log.d("sb","333333"+response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                JSONObject obj = new JSONObject(response).getJSONObject("body");   // JSONArray 생성
                                Log.d("sb","obj: "+obj);


                                Shared_Pref.stationName = obj.optString("stationName");

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

    public boolean getDeviceState() {

        Log.d("beacon", "Check the Bluetooth support");
        if(btAdapter == null) {

            Log.d("beacon", "Bluetooth is not available");

        return false;

        } else {
            Log.d("beacon", "Bluetooth is available");

        return true;
        }
    }

    public void enableBluetooth() {
        Log.d("beacon", "Check the enabled Bluetooth");
        if(btAdapter.isEnabled()) {
            // 기기의 블루투스 상태가 On인 경우
            Log.d("beacon", "Bluetooth Enable Now");
            //Next Step
        } else {
            // 기기의 블루투스 상태가 Off인 경우
            Log.d("beacon", "Bluetooth Enable Request");

        }
    }



}