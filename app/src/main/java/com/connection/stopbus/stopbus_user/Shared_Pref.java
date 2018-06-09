package com.connection.stopbus.stopbus_user;

/**
 * Created by Danbk on 2018-04-02.
 */


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Shared_Pref {
    private static SharedPreferences mSharedPref;

    public static int STATUS=0;
    public static int IN_BUS=0;
    public static String UUID = null;
    public static int btenable =0;


    //정류장별 버스 리스트를 받아오기 위함
    public static String stationNumber =null;
    public static String stationName =null;
    public static String stationDirect =null;
    public static String stationID = null;

    //버스별 노선 정류장을 받아오기 위함
    public static String routeID = null;
    public static String plateNo =null;

    private Shared_Pref() {

    }

    public static void init(Context context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).commit();
    }
}