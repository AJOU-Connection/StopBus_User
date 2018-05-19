package com.connection.stopbus.stopbus_user;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Danbk on 2018-05-17.
 */

public class ActivityBus extends Activity{

    private RecyclerView recyclerView;
    private RecycleAdapter bus_station_list_adapter = new RecycleAdapter(this);
    private RecycleAdapter  bus_location_list_adapter= new RecycleAdapter(this);
    private RecycleAdapter  route_info_list_adapter= new RecycleAdapter(this);

    private SwipeRefreshLayout swipeContainer;

    private List<ApiData.BusStation> BusStationList = new ArrayList<ApiData.BusStation>();
    private List<ApiData.BusStation> CopyBusStationList;

    private List<ApiData.routeInfo> routeInfoList = new ArrayList<ApiData.routeInfo>();
    private List<ApiData.routeInfo> CopyrouteInfoList;

    private List<ApiData.busLocation> busLocationList = new ArrayList<ApiData.busLocation>();
    private List<ApiData.busLocation> CopybusLocationList;

    Handler mHandler = new Handler();

    TextView bus_type;
    TextView bus_num;
    TextView startStationName;
    TextView endStationName;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        CallData("busStationList");
        CallData("routeInfo");
        CallData("busLocationList");



        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_layout0);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                CallData("busStationList");
                CallData("busLocationList");
                swipeContainer.setRefreshing(false);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.rv_station_bus_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                        getBaseContext(), LinearLayoutManager.VERTICAL, false
                )
        );
        recyclerView.setAdapter(bus_station_list_adapter);

        findViewById(R.id.back).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("sb", "back button pressed");
                        Intent i = new Intent(ActivityBus.this, ActivityStation.class);
                        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    }
                }
        );

        findViewById(R.id.home).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("sb", "home button pressed");
                        Intent i = new Intent(ActivityBus.this, ActivityStation.class);
                        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

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
                args.put("routeID",  String.valueOf(Shared_Pref.routeId));

                try {

                    final String response = NetworkService.INSTANCE.postQuery(api, args);
                    Log.d("sb","333333"+response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (api.equals("busStationList")) {
                                try {

                                    JSONArray jarray = new JSONObject(response).getJSONArray("body");   // JSONArray 생성

                                    ApiData.BusStation[] arr = new Gson().fromJson(jarray.toString(), ApiData.BusStation[].class);
                                    BusStationList = Arrays.asList(arr);
                                    CopyBusStationList = new ArrayList<ApiData.BusStation>();
                                    CopyBusStationList.addAll(BusStationList);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                bus_station_list_adapter.notifyDataSetChanged();

                            } else if(api.equals("routeInfo")) {
                                try {

                                    JSONArray jarray = new JSONObject(response).getJSONArray("body");   // JSONArray 생성

                                    ApiData.routeInfo[] arr = new Gson().fromJson(jarray.toString(), ApiData.routeInfo[].class);
                                    routeInfoList = Arrays.asList(arr);
                                    CopyrouteInfoList = new ArrayList<ApiData.routeInfo>();
                                    CopyrouteInfoList.addAll(routeInfoList);

                                    bus_type = (TextView) findViewById(R.id.bus_type);
                                    bus_type.setText(routeInfoList.get(0).routeTypeName);

                                    bus_num = (TextView) findViewById(R.id.bus_num);
                                    bus_num.setText(routeInfoList.get(0).routeNumber);

                                    startStationName = (TextView) findViewById(R.id.startStationName);
                                    startStationName.setText(routeInfoList.get(0).startStationName);

                                    endStationName = (TextView) findViewById(R.id.endStationName);
                                    endStationName.setText(routeInfoList.get(0).endStationName);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                route_info_list_adapter.notifyDataSetChanged();

                            }else if(api.equals("busLocationList")) {
                                try {

                                    JSONArray jarray = new JSONObject(response).getJSONArray("body");   // JSONArray 생성

                                    ApiData.busLocation[] arr = new Gson().fromJson(jarray.toString(), ApiData.busLocation[].class);
                                    busLocationList = Arrays.asList(arr);
                                    CopybusLocationList = new ArrayList<ApiData.busLocation>();
                                    CopybusLocationList.addAll(busLocationList);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                bus_location_list_adapter.notifyDataSetChanged();
                            }
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해주세요 ", Toast.LENGTH_LONG).show();
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
            final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_route, parent, false);
            return new RecycleAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecycleAdapter.ViewHolder holder, final int position) {

            try {

                holder.stationName.setText(BusStationList.get(position).stationName);
                holder.stationNumber.setText(BusStationList.get(position).stationNumber);


            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return BusStationList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView stationName;
            public TextView stationNumber;

            public ViewHolder(final View itemView) {
                super(itemView);

                stationName = (TextView) itemView.findViewById(R.id.stationName);
                stationNumber = (TextView) itemView.findViewById(R.id.stationNumber);


            }
        }

    }


}
