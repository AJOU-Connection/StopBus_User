package com.connection.stopbus.stopbus_user;
/**
 * Created by Danbk on 2018-03-29.
 */
import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {


    private long backPressedTime = 0;
    private final int REQUEST_NEED = 100;
    private final long FINISH_INTERVAL_TIME = 2000;
    private int STATUS = 0;

    private static String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Shared_Pref.init(getApplicationContext());

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

        if(STATUS==0){
            Intent i = new Intent(MainActivity.this, ActivityFavourite.class);
            startActivity(i);
        }


    }
    //[E] 앱시작 ( 기기 정보 로드 및 UI ) ----------------------------------------------------------------------------------------------------------------v

}
