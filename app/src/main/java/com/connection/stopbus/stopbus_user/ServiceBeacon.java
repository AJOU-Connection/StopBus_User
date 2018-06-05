package com.connection.stopbus.stopbus_user;

import android.app.Service;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// 서비스 클래스를 구현하려면, Service 를 상속받는다
public class ServiceBeacon extends Service implements BeaconConsumer{

    private final MyHandler mHandler = new MyHandler(this);

    private BeaconManager beaconManager;

    //감지된 비콘들을 임시로 담을 리스트
    private List<Beacon> beaconList = new ArrayList<>();


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

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

                        Log.d("sb","List : " + beaconList);

                    }
                    Log.d("sb", "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
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
        Log.d("sb", "beaconlist: "+ beaconList);

        Log.d("sb", "msg: "+ msg);

        //비콘의 아이디와 거리를 측정하여 textvIEW에 띄움
        for(Beacon beacon : beaconList){

            if(beaconList.toString().equals("[]")){
                Log.d("sb","no");


            }else{
                Shared_Pref.STATUS =1;
                Log.d("sb","yes");
                //여기 이제 위치별 districtCd, stationNumber 받아와야함
                if(beacon.getBluetoothName().substring(0,3).equals("bus")){
                    Shared_Pref.routeID = beacon.getBluetoothName().substring(3,12);

                }else if(beacon.getBluetoothName().substring(0,1).equals("s")){
                    Shared_Pref.stationID= beacon.getBluetoothName().substring(1,10);
                    Shared_Pref.stationNumber =beacon.getBluetoothName().substring(10,15);
                    Log.d("sb","beacon.getBluetoothName().substring(1,10): "+ beacon.getBluetoothName().substring(1,10));
                    Log.d("sb","beacon.getBluetoothName().substring(10,15): "+ beacon.getBluetoothName().substring(10,15));

                }
            }

            Log.d("sb","name : " + beacon.getBluetoothName() + " / " + "ID2 : " + beacon.getId2() + " / " + String.valueOf(beacon.getDistance()));

        }

        mHandler.sendEmptyMessageDelayed(0,1000);

    }


    //[E] 비콘 ----------------------------------------------------------------------------------------------------------------

}