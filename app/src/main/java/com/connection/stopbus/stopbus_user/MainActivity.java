package com.connection.stopbus.stopbus_user;
/**
 * Created by Danbk on 2018-03-29.
 */
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends Activity {


    private long backPressedTime = 0;
    private final int REQUEST_NEED = 100;
    private final long FINISH_INTERVAL_TIME = 2000;
    private int STATUS = 0;
    //SharedPref
    SharedPreferences pref;


    private static String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Shared_Pref.init(getApplicationContext());
        Log.d("sb", "start app!!!");
        //[S] 퍼미션 체크 ----------------------------------------------------------------------------------------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionDeniedCount = 0;
            for (String permission : PERMISSIONS) {
                int result = ContextCompat.checkSelfPermission(this, permission);
                if (result == PackageManager.PERMISSION_DENIED) {
                    permissionDeniedCount++;
                }
            }

            if (permissionDeniedCount == 0) {
                StartApp();
            } else {
                doRequestPermission();
            }
        } else {
            //시작!!!!!
            StartApp();
        }

    }


    private void doRequestPermission() {
        ArrayList<String> notGrantedPermissions = new ArrayList<>();
        for (String perm : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_DENIED) {
                notGrantedPermissions.add(perm);
            }
        }
        if (notGrantedPermissions.size() == 0) {
            StartApp();
        } else {
            ActivityCompat.requestPermissions(this, notGrantedPermissions.toArray(new String[]{}), REQUEST_NEED);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionDeniedCount = 0;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                permissionDeniedCount++;
            }
        }

        if (0 == permissionDeniedCount) {
            StartApp();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.exit_title))
                    .setMessage(getResources().getString(R.string.exit_content))
                    .setPositiveButton(getResources().getString(R.string.yes_text), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no_text), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            doRequestPermission();
                        }
                    })
                    .show();
        }
    }
    //[E] 퍼미션 체크 ----------------------------------------------------------------------------------------------------------------

    //[S] 뒤로가기 버튼 클릭 ----------------------------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 누르시면 앱이 종료 됩니다", Toast.LENGTH_SHORT).show();
        }
    }
    //[E] 뒤로가기 버튼 클릭 ----------------------------------------------------------------------------------------------------------------

    //[S] 앱시작 ( 기기 정보 로드 및 UI ) ----------------------------------------------------------------------------------------------------------------
    public void StartApp() {



        try {
            TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

            String tmDevice = "" + tm.getDeviceId();
            String tmSerial = "" + tm.getSimSerialNumber();
            String androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            // UUID
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            // FCM 토큰
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();

            pref = getSharedPreferences("a", MODE_PRIVATE);

            Log.d("sb", "Shared : " + pref.getString("Token", "") );

            if(pref.getString("Token","").equals(refreshedToken)){
                Log.d("sb", "same token!" );
            }else{
                Log.d("sb","12415543aslkdrlnaksnrllksadrnlkasdrndlkr");
                CallData("register");
            }

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("DeviceId", deviceUuid.toString());
            editor.putString("Token", refreshedToken);
            editor.commit();

            Log.d("sb", "MyFirebaseInstanceIDService> Refreshed token: " + refreshedToken);
            Log.d("sb","DeviceId: "+deviceUuid.toString());



        } catch (SecurityException e) {

        }



        if(STATUS==0){
            Intent i = new Intent(MainActivity.this, ActivityFavourite.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }


    }
    //[E] 앱시작 ( 기기 정보 로드 및 UI ) ----------------------------------------------------------------------------------------------------------------v
    public synchronized void CallData(final String api) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> args = new HashMap<String, String>();
                Log.d("sb", "token:" + Shared_Pref.Token +"UUID: " + Shared_Pref.DeviceId);
                args.put("token",  pref.getString("Token","")); //POST
                args.put("UUID",  pref.getString("DeviceId","")); //POST

                try {

                    final String response = NetworkService.INSTANCE.postQuery(api, args);
                    Log.d("sb","333333"+response);


                } catch (Exception e) {
                }
            }
        }).start();

    }
}


