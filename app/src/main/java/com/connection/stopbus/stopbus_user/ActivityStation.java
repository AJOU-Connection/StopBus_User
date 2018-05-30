package com.connection.stopbus.stopbus_user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
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
 * Created by Danbk on 2018-05-04.
 */

public class ActivityStation extends Activity{

    private RecyclerView recyclerView;
    private RecycleAdapter station_bus_list_adapter = new RecycleAdapter(this);
    private SwipeRefreshLayout swipeContainer;

    private List<ApiData.StationBus> StationBusList = new ArrayList<ApiData.StationBus>();
    private List<ApiData.StationBus> CopyStationBusList;

    Handler mHandler = new Handler();

    TextView station_num;
    TextView station_name;
    TextView station_way;

    int routeID;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        CallData("busArrival");

        station_num = (TextView) findViewById(R.id.station_num);
        station_num.setText(Shared_Pref.stationNumber);

        station_name = (TextView) findViewById(R.id.station_name);
        station_name.setText(Shared_Pref.stationName);

        station_way = (TextView) findViewById(R.id.station_way);
        station_way.setText(Shared_Pref.stationDirect);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_layout0);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CallData("busArrival");
                swipeContainer.setRefreshing(false);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.rv_station_bus_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                        getBaseContext(), LinearLayoutManager.VERTICAL, false
                )
        );
        recyclerView.setAdapter(station_bus_list_adapter);


        findViewById(R.id.back).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("sb", "back button pressed");
                        finish();

                    }
                }
        );

        findViewById(R.id.home).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("sb", "home button pressed");
                        Intent i = new Intent(ActivityStation.this, ActivityFavourite.class);
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
                args.put("districtCd",  String.valueOf(Shared_Pref.districtCd)); //POST
                args.put("stationNumber",  Shared_Pref.stationNumber);

                try {

                    final String response = NetworkService.INSTANCE.postQuery(api, args);
                    Log.d("sb","333333"+response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                                try {
                                    JSONArray jarray = new JSONObject(response).getJSONArray("body");   // JSONArray 생성

                                    ApiData.StationBus[] arr = new Gson().fromJson(jarray.toString(), ApiData.StationBus[].class);
                                    StationBusList = Arrays.asList(arr);
                                    CopyStationBusList = new ArrayList<ApiData.StationBus>();
                                    CopyStationBusList.addAll(StationBusList);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                station_bus_list_adapter.notifyDataSetChanged();

                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해주세요 ", Toast.LENGTH_LONG).show();
                }
            }
        }).start();

    }

    //승차벨
    public synchronized void getIn(final String api) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> args = new HashMap<String, String>();
                args.put("UUID",  Shared_Pref.UUID); //POST
                args.put("routeID",  Integer.toString(routeID));
                args.put("stationID",  Shared_Pref.stationID);

                try {

                    final String response = NetworkService.INSTANCE.postQuery(api, args);
                    Log.d("sb","333333"+response);

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
            final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_station_bus, parent, false);
            return new RecycleAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecycleAdapter.ViewHolder holder, final int position) {

            try {
                if(Shared_Pref.STATUS==0){
                    holder.favourite_btn.setVisibility(View.VISIBLE);
                    holder.bell.setVisibility(View.INVISIBLE);
                }else{
                    holder.favourite_btn.setVisibility(View.INVISIBLE);
                    holder.bell.setVisibility(View.VISIBLE);
                }

                holder.bell.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {

                                routeID = StationBusList.get(position).routeId;
                                holder.bell.setImageResource(R.drawable.bell_red);
                                getIn("reserv/getIn");
                            }
                        }
                );

                holder.locationNo1.setText(StationBusList.get(position).locationNo1 + "번째 전");
                holder.locationNo2.setText(StationBusList.get(position).locationNo2 + "번째 전");
                //holder.lowPlate1.setText(StationBusList.get(position).lowPlate1);
                //holder.lowPlate2.setText(StationBusList.get(position).lowPlate2);
                holder.predictTime1.setText(StationBusList.get(position).predictTime1 + "분 후 도착");
                holder.predictTime2.setText(StationBusList.get(position).predictTime2 + "분 후 도착");
                //holder.remainSeatCnt1.setText(StationBusList.get(position).remainSeatCnt1);
                //holder.remainSeatCnt2.setText(StationBusList.get(position).remainSeatCnt2);
                //holder.routeId.setText(StationBusList.get(position).routeId);
                holder.routeNumber.setText(StationBusList.get(position).routeNumber);
                holder.routeTypeName.setText(StationBusList.get(position).routeTypeName);


                if(StationBusList.get(position).routeTypeName.equals("일반형시내버스")){
                    holder.routeNumber.setTextColor(Color.parseColor("#03896E"));
                }else if(StationBusList.get(position).routeTypeName.equals("직행좌석형시내버스")) {
                    holder.routeNumber.setTextColor(Color.parseColor("#F33D46"));
                }

                if(StationBusList.get(position).predictTime1.equals("-1")||StationBusList.get(position).predictTime1.equals("0")){
                    holder.predictTime1.setText("도착정보없음");
                    holder.locationNo1.setVisibility(View.GONE);
                }else{
                    holder.locationNo1.setVisibility(View.VISIBLE);
                }

                if(StationBusList.get(position).predictTime2.equals("-1")||StationBusList.get(position).predictTime2.equals("0")){
                    holder.predictTime2.setText("도착정보없음");
                    holder.locationNo2.setVisibility(View.GONE);
                }else{
                    holder.locationNo2.setVisibility(View.VISIBLE);
                }

                holder.bus_list_layout.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {

                                Shared_Pref.routeId = StationBusList.get(position).routeId;

                                Log.d("sb", "bus route list gogo");
                                Intent i = new Intent(ActivityStation.this, ActivityBus.class);
                                startActivity(i);

                            }
                        }
                );

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return StationBusList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView locationNo1;
            public TextView locationNo2;
            public TextView lowPlate1;
            public TextView lowPlate2;
            public TextView predictTime1;
            public TextView predictTime2;
            public TextView remainSeatCnt1;
            public TextView remainSeatCnt2;
            public TextView routeId;
            public TextView routeNumber;
            public TextView routeTypeName;
            public RelativeLayout bus_list_layout;
            public ImageView bell;
            public ImageView favourite_btn;

            public ViewHolder(final View itemView) {
                super(itemView);

                locationNo1 = (TextView) itemView.findViewById(R.id.locationNo1);
                locationNo2 = (TextView) itemView.findViewById(R.id.locationNo2);
                //lowPlate1 = (TextView) itemView.findViewById(R.id.lowPlate1);
                //lowPlate2 = (TextView) itemView.findViewById(R.id.lowPlate2);
                predictTime1 = (TextView) itemView.findViewById(R.id.predictTime1);
                predictTime2 = (TextView) itemView.findViewById(R.id.predictTime2);
                //remainSeatCnt1 = (TextView) itemView.findViewById(R.id.remainSeatCnt1);
                //remainSeatCnt2 = (TextView) itemView.findViewById(R.id.remainSeatCnt2);
                //routeId = (TextView) itemView.findViewById(R.id.routeId);
                routeNumber = (TextView) itemView.findViewById(R.id.routeNumber);
                routeTypeName= (TextView) itemView.findViewById(R.id.routeTypeName);

                bus_list_layout = (RelativeLayout)itemView.findViewById(R.id.bus_list_layout);

                bell = (ImageView)itemView.findViewById(R.id.bell);
                favourite_btn = (ImageView)itemView.findViewById(R.id.favourite_btn);


            }
        }

    }


}
