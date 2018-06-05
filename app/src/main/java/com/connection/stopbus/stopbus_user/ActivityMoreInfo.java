package com.connection.stopbus.stopbus_user;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ActivityMoreInfo extends Activity{


    Handler mHandler = new Handler();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        CallData("routeInfo");

        findViewById(R.id.back).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("sb", "back button pressed");
                        finish();
                    }
                }
        );


    }


    //검색 불러오는 API
    public synchronized void CallData(final String api) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> args = new HashMap<String, String>();
                args.put("routeID",  Shared_Pref.routeID);

                try {
                    final String response = NetworkService.INSTANCE.postQuery(api, args);
                    Log.d("sb","333333"+response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jObject = new JSONObject(response).getJSONObject("body");

                                Log.d("sb","jobject"+ jObject);

                                ApiData.routeInfo routeInfo = new Gson().fromJson(jObject.toString(), ApiData.routeInfo.class);

                                Log.d("sb","routeInfo: "+ routeInfo.toString());

                                TextView routeNumber = (TextView) findViewById(R.id.routeNumber);
                                routeNumber.setText(routeInfo.routeNumber);

                                TextView startStationName = (TextView) findViewById(R.id.startStationName);
                                startStationName.setText(routeInfo.startStationName);

                                TextView endStationName = (TextView) findViewById(R.id.endStationName);
                                endStationName.setText(routeInfo.endStationName);

                                TextView regionName = (TextView) findViewById(R.id.regionName);
                                regionName.setText(routeInfo.regionName);

                                TextView routeTypeName = (TextView) findViewById(R.id.routeTypeName);
                                routeTypeName.setText(routeInfo.routeTypeName);

                                TextView upFirstTime = (TextView) findViewById(R.id.upFirstTime);
                                upFirstTime.setText(routeInfo.upFirstTime);

                                TextView downFirstTime = (TextView) findViewById(R.id.downFirstTime);
                                downFirstTime.setText(routeInfo.downFirstTime);

                                TextView upLastTime = (TextView) findViewById(R.id.upLastTime);
                                upLastTime.setText(" ~ "+ routeInfo.upLastTime);

                                TextView downLastTime = (TextView) findViewById(R.id.downLastTime);
                                downLastTime.setText(" ~ "+ routeInfo.downLastTime);

                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


}
