package com.connection.stopbus.stopbus_user;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Danbk on 2018-04-04.
 */

public class ActivityFavourite extends Activity{

    private RecyclerView recyclerView;
    private RecycleAdapter favourite_bus_list_adapter = new RecycleAdapter(this);
    private SwipeRefreshLayout swipeContainer;
    private List<ApiData.favrouteInfo> favRouteInfoList = new ArrayList<ApiData.favrouteInfo>();

    ArrayList<String> favouriteList;
    Handler mHandler = new Handler();
    int flag=0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        CallMyBusList();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CallMyBusList();
                swipeContainer.setRefreshing(false);
            }
        });

        TextView search = (TextView) findViewById(R.id.search);
        search.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("sb", "search");
                        Intent i = new Intent(ActivityFavourite.this, ActivitySearchFav.class);
                        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
        );

        TextView delete_fav = (TextView) findViewById(R.id.delete_fav);
        delete_fav.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("sb", "delte_fav");
                        if(flag==0){
                            flag =1;
                        }else if(flag==1){
                            flag=0;
                        }
                        CallMyBusList();
                    }
                }
        );

        findViewById(R.id.fab).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("sb", "search for bus stop");


                        if(Shared_Pref.btenable==0){

                            Log.d("sb", "Bluetooth Enable Request");
                            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(i, 1);



                        }else if(Shared_Pref.btenable==1){
                            Intent intent = new Intent(
                                    getApplicationContext(),//현재제어권자
                                    ServiceBeacon.class); // 이동할 컴포넌트
                            startService(intent); // 서비스 시작



                            if(Shared_Pref.beacon_stationID.equals("")){
                                Toast.makeText(getApplicationContext(), "감지된 버스정류장이 없습니다.", Toast.LENGTH_LONG).show();

                            }else{
                                Log.d("sb", "search for bus stop 222222");
                                Intent i = new Intent(ActivityFavourite.this, ActivityStation.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }


                        }

                    }
                }
        );


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // 확인 눌렀을 때 //Next Step
                    Log.d("sb", "Bluetooth is  enabled");
                    Shared_Pref.btenable= 1;
                    Intent intent = new Intent(
                            getApplicationContext(),//현재제어권자
                            ServiceBeacon.class); // 이동할 컴포넌트
                    startService(intent); // 서비스 시작

                    Log.d("sb", "search for bus stop 222222");
                    Intent i = new Intent(ActivityFavourite.this, ActivityStation.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    // 취소 눌렀을 때
                    Log.d("sb", "Bluetooth is not enabled");
                    Shared_Pref.btenable= 0;
                }
                break;
        }
    }


    public void CallMyBusList() {

        favRouteInfoList = new ArrayList<ApiData.favrouteInfo>();
        final TinyDB tinydb = new TinyDB(ActivityFavourite.this);
        favouriteList = tinydb.getListString("Favourite");
        Log.d("sb", "favouriteList: " + favouriteList);
        Log.d("sb", "favouriteSize:  " + favouriteList.size());

        CallData("starInfo");
        recyclerView = (RecyclerView) findViewById(R.id.rv_favourite_bus_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                        getBaseContext(), LinearLayoutManager.VERTICAL, false
                )
        );
        recyclerView.setAdapter(favourite_bus_list_adapter);

    }



    //검색 불러오는 API
    public synchronized void CallData(final String api) {
        new Thread(new Runnable() {
            @Override
                public void run() {
                Log.d("sb", "json: "+ favouriteList);

                try {
                    final String response = NetworkService.INSTANCE.postQuery2(api, favouriteList, "routeIDList");
                    Log.d("sb","333333"+response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONArray jarray = new JSONObject(response).getJSONArray("body");   // JSONArray 생성
                                Log.d("sb","jarray"+jarray);
                                ApiData.favrouteInfo[] arr= new Gson().fromJson(jarray.toString(), ApiData.favrouteInfo[].class);
                                favRouteInfoList = Arrays.asList(arr);

                                Log.d("sb","44444"+favRouteInfoList);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            favourite_bus_list_adapter.notifyDataSetChanged();

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

        Context mContext;

        public RecycleAdapter(Context context) {
            this.mContext = context;

        }

        @Override
        public RecycleAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_fav_bus, parent, false);
            return new RecycleAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecycleAdapter.ViewHolder holder, final int position) {

            Log.d("sb","666666666666"+favRouteInfoList);

            final TinyDB tinydb = new TinyDB(ActivityFavourite.this);

            try {
                if(flag==1){
                    holder.delete_btn.setVisibility(View.VISIBLE);
                    holder.reserve_btn.setVisibility(View.GONE);
                }else if(flag==0){
                    holder.delete_btn.setVisibility(View.GONE);
                    holder.reserve_btn.setVisibility(View.VISIBLE);

                }
                holder.delete_btn.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {

                                favouriteList= tinydb.getListString("Favourite");
                                Log.d("sb", "favouriteList: " + favouriteList);
                                Iterator<String> itr = favouriteList.iterator();
                                while(itr.hasNext()){
                                    String id = itr.next();
                                    Log.d("sb", "favRouteInfoList.get(position).routeID :"+ favRouteInfoList.get(position).routeID);
                                    if(id.equals(favRouteInfoList.get(position).routeID)){

                                        Log.d("sb", "delete");
                                        favouriteList.remove(favRouteInfoList.get(position).routeID);
                                        tinydb.putListString("Favourite",favouriteList );

                                        Log.d("sb","delete"+favRouteInfoList);
                                        CallMyBusList();
                                        break;
                                    }
                                }



                            }
                        }
                );

                Log.d("sb", "favRouteInfoList.get(position).routeID222222222222222222: "+favRouteInfoList.get(position).routeID);
                Log.d("sb", "favRouteInfoList.get(position).routeNumber33333333333333: "+favRouteInfoList.get(position).routeNumber);
                holder.routeNumber.setText(favRouteInfoList.get(position).routeNumber);
                holder.regionName.setText(favRouteInfoList.get(position).regionName);
                holder.routeTypeName.setText(favRouteInfoList.get(position).routeTypeName);
                holder.upFirstTime.setText("상행 : " + favRouteInfoList.get(position).upFirstTime);
                holder.upLastTime.setText(" ~ "+ favRouteInfoList.get(position).upLastTime);
                holder.downFirstTime.setText("하행 : " + favRouteInfoList.get(position).downFirstTime);
                holder.downLastTime.setText(" ~ "+ favRouteInfoList.get(position).downLastTime);
            }catch (Exception e){
                e.printStackTrace();
            }



        }

        @Override
        public int getItemCount() {
            return favouriteList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            public ImageView delete_btn;

            public TextView routeNumber;
            public TextView regionName;
            public TextView routeTypeName;
            public TextView upFirstTime;
            public TextView downFirstTime;
            public TextView upLastTime;
            public TextView downLastTime;
            public com.zcw.togglebutton.ToggleButton reserve_btn;

            public ViewHolder(final View itemView) {
                super(itemView);
                routeNumber= (TextView) itemView.findViewById(R.id.routeNumber);
                regionName = (TextView) itemView.findViewById(R.id.regionName);
                routeTypeName = (TextView) itemView.findViewById(R.id.routeTypeName);
                upFirstTime = (TextView) itemView.findViewById(R.id.upFirstTime);
                downFirstTime = (TextView) itemView.findViewById(R.id.downFirstTime);
                upLastTime = (TextView) itemView.findViewById(R.id.upLastTime);
                downLastTime= (TextView) itemView.findViewById(R.id.downLastTime);

                delete_btn = (ImageView) itemView.findViewById(R.id.delete_btn);
                reserve_btn = (com.zcw.togglebutton.ToggleButton) itemView.findViewById(R.id.reserve_btn);

            }
        }

    }


}
