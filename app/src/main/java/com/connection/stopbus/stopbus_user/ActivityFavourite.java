package com.connection.stopbus.stopbus_user;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.zcw.togglebutton.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import devlight.io.library.ntb.NavigationTabBar;

/**
 * Created by Danbk on 2018-04-04.
 */

public class ActivityFavourite extends Activity{

    ArrayList<String> favouriteList;
    Handler mHandler = new Handler();
    int flag=0;

    private RecyclerView recyclerView;
    private RecycleAdapter favourite_bus_list_adapter = new RecycleAdapter(this);
    private SwipeRefreshLayout swipeContainer0;

    private List<ApiData.favrouteInfo> favRouteInfoList = new ArrayList<ApiData.favrouteInfo>();

    private RecyclerView recyclerView2;
    private RecycleAdapter2 bus_station_list_adapter = new RecycleAdapter2(this);
    private RecycleAdapter2 bus_location_list_adapter= new RecycleAdapter2(this);
    private RecycleAdapter2 route_info_list_adapter= new RecycleAdapter2(this);
    private SwipeRefreshLayout swipeContainer1;

    private List<ApiData.BusStation> BusStationList = new ArrayList<ApiData.BusStation>();
    private List<ApiData.busLocation> busLocationList = new ArrayList<ApiData.busLocation>();
    private BluetoothAdapter btAdapter;

    TextView bus_type;
    TextView bus_num;
    TextView startStationName;
    TextView endStationName;

    int station_now =0;
    String target_station;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        initUI();
    }
    private void initUI() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(final View view, final Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(final View container, final int position, final Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public Object instantiateItem(final ViewGroup container, final int position) {
                View view = null;

                if (position == 0) {  //작업 이력 레이아웃
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.activity_favourite, null, false);
                    recyclerView = (RecyclerView) view.findViewById(R.id.rv_favourite_bus_list);
                    CallMyBusList();

                    // Lookup the swipe container view
                    swipeContainer0 = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
                    // Setup refresh listener which triggers new data loading
                    swipeContainer0.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            CallMyBusList();
                            swipeContainer0.setRefreshing(false);
                        }
                    });

                    TextView search = (TextView) view.findViewById(R.id.search);
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


                    final TextView delete_fav = (TextView) view.findViewById(R.id.delete_fav);
                    delete_fav.setOnClickListener(
                            new Button.OnClickListener() {
                                public void onClick(View v) {
                                    Log.d("sb", "delte_fav");
                                    if(flag==0){
                                        flag =1;
                                        delete_fav.setText("완료");
                                    }else if(flag==1){
                                        flag=0;
                                        delete_fav.setText("편집");
                                    }
                                    CallMyBusList();
                                }
                            }
                    );



                    view.findViewById(R.id.fab).setOnClickListener(
                            new Button.OnClickListener() {
                                public void onClick(View v) {
                                    Log.d("sb", "search for bus stop");
                                    Shared_Pref.stationID= Shared_Pref.beacon_stationID;

                                    if(btAdapter.isEnabled()){
                                        Intent intent = new Intent(
                                                getApplicationContext(),//현재제어권자
                                                ServiceBeacon.class); // 이동할 컴포넌트
                                        startService(intent); // 서비스 시작

                                        Shared_Pref.bt_station_flag=1;

                                        if(Shared_Pref.beacon_stationID.equals("")){
                                            Toast.makeText(getApplicationContext(), "감지된 버스정류장이 없습니다.", Toast.LENGTH_LONG).show();

                                        }else{
                                            Log.d("sb", "search for bus stop 222222");
                                            Intent i = new Intent(ActivityFavourite.this, ActivityStation.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                        }


                                    }else{

                                        Log.d("sb", "Bluetooth Enable Request");
                                        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                        startActivityForResult(i, 1);


                                    }

                                }
                            }
                    );






                } else if (position == 1) {
                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.activity_bus, null, false);

                    Shared_Pref.bt_bus_flag=1;

                    CallData2("busStationList");
                    CallData2("routeInfo");
                    CallData2("busLocationList");

                    if(Shared_Pref.beacon_routeID.equals("")){
                        view.findViewById(R.id.bus_layout).setVisibility(View.GONE);
                        view.findViewById(R.id.swipe_layout0).setVisibility(View.GONE);

                        view.findViewById(R.id. no_bus_layout).setVisibility(View.VISIBLE);

                    }else{
                        view.findViewById(R.id.bus_layout).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.swipe_layout0).setVisibility(View.VISIBLE);
                        view.findViewById(R.id. no_bus_layout).setVisibility(View.GONE);
                    }

                    // Lookup the swipe container view
                    swipeContainer1 = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout0);
                    // Setup refresh listener which triggers new data loading
                    swipeContainer1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            Shared_Pref.bt_bus_flag=1;
                            CallData2("routeInfo");
                            CallData2("busStationList");
                            CallData2("busLocationList");
                            swipeContainer1.setRefreshing(false);
                        }
                    });
                    recyclerView2 = (RecyclerView) view.findViewById(R.id.rv_station_list);
                    recyclerView2.setHasFixedSize(true);
                    recyclerView2.setLayoutManager(new LinearLayoutManager(
                                    getBaseContext(), LinearLayoutManager.VERTICAL, false
                            )
                    );
                    recyclerView2.setAdapter(bus_station_list_adapter);



                    view.findViewById(R.id.back).setVisibility(View.INVISIBLE);

                    view.findViewById(R.id.home).setVisibility(View.INVISIBLE);

                    view.findViewById(R.id.moreInfo).setOnClickListener(
                            new Button.OnClickListener() {
                                public void onClick(View v) {
                                    Log.d("sb", "more info button pressed");
                                    Intent i = new Intent(ActivityFavourite.this, ActivityMoreInfo.class);
                                    i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);

                                }
                            }
                    );

                }

                container.addView(view);
                return view;

            }
        });

        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_directions_bus_white_48pt),
                        Color.parseColor(colors[4]))
                        .title("즐겨찾기")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.bus_stop),
                        Color.parseColor(colors[4]))
                        .title("비콘버스")
                        .build()
        );


        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);

        //IMPORTANT: ENABLE SCROLL BEHAVIOUR IN COORDINATOR LAYOUT
        navigationTabBar.setBehaviorEnabled(false);

        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {
            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });

        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                Log.d("dd", "22222222222222222222");
                if(position==1){
                    Shared_Pref.bt_bus_flag=1;
                    if(Shared_Pref.beacon_routeID.equals("")){
                        findViewById(R.id.bus_layout).setVisibility(View.GONE);
                        findViewById(R.id.swipe_layout0).setVisibility(View.GONE);
                        findViewById(R.id. no_bus_layout).setVisibility(View.VISIBLE);

                    }else{
                        findViewById(R.id.bus_layout).setVisibility(View.VISIBLE);
                        findViewById(R.id.swipe_layout0).setVisibility(View.VISIBLE);
                        findViewById(R.id. no_bus_layout).setVisibility(View.GONE);

                        CallData2("busStationList");
                        CallData2("routeInfo");
                        CallData2("busLocationList");
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                Log.d("dd", "3333333333333333333");
            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // 확인 눌렀을 때 //Next Step
                    Log.d("sb", "Bluetooth is  enabled");

                    Intent intent = new Intent(
                            getApplicationContext(),//현재제어권자
                            ServiceBeacon.class); // 이동할 컴포넌트
                    startService(intent); // 서비스 시작

                    Shared_Pref.bt_station_flag=1;
                    Log.d("sb", "search for bus stop 222222");
                    Intent i = new Intent(ActivityFavourite.this, ActivityStation.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    // 취소 눌렀을 때
                    Log.d("sb", "Bluetooth is not enabled");
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                        getBaseContext(), LinearLayoutManager.VERTICAL, false
                )
        );
        recyclerView.setAdapter(favourite_bus_list_adapter);

    }

    //하차예약
    public synchronized void getOut(final String api) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> args = new HashMap<String, String>();
                args.put("UUID",  Shared_Pref.UUID);
                args.put("routeID",  Shared_Pref.beacon_routeID); //Shared_Pref.beacon_routeID
                args.put("stationID",  target_station); //target_station
                args.put("plateNo",  Shared_Pref.beacon_plateNo); //Shared_Pref.beacon_plateNo


                try {
                    final String response = NetworkService.INSTANCE.postQuery(api , args);
                    Log.d("sb","333333"+response);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해주세요 ", Toast.LENGTH_LONG).show();
                }
            }
        }).start();

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
    //검색 불러오는 API
    public synchronized void CallData2(final String api) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> args = new HashMap<String, String>();
                if(Shared_Pref.bt_bus_flag == 0){
                    args.put("routeID",  Shared_Pref.routeID);
                }else if(Shared_Pref.bt_bus_flag ==1){
                    args.put("routeID",  Shared_Pref.beacon_routeID);
                }


                try {

                    final String response = NetworkService.INSTANCE.postQuery(api, args);
                    Log.d("sb","333333"+response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (api.equals("busStationList")) {
                                try {

                                    JSONArray jarray = new JSONObject(response).getJSONArray("body");   // JSONArray 생성


                                    Log.d("sb","asdf2222");

                                    ApiData.BusStation[] arr = new Gson().fromJson(jarray.toString(), ApiData.BusStation[].class);
                                    BusStationList = Arrays.asList(arr);



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d("sb","station_now: "+ station_now);
                                bus_station_list_adapter.notifyDataSetChanged();

                            } else if(api.equals("routeInfo")) {
                                try {


                                    Log.d("sb","asdfasdfasdf");
                                    JSONObject jObject = new JSONObject(response).getJSONObject("body");

                                    Log.d("sb","jobject"+ jObject);

                                    ApiData.routeInfo routeInfo = new Gson().fromJson(jObject.toString(), ApiData.routeInfo.class);

                                    Log.d("sb","routeInfo: "+ routeInfo.toString());

                                    bus_type = (TextView) findViewById(R.id.bus_type);
                                    bus_type.setText(routeInfo.routeTypeName);

                                    bus_num = (TextView) findViewById(R.id.bus_num);
                                    bus_num.setText(routeInfo.routeNumber);

                                    startStationName = (TextView) findViewById(R.id.startStationName);
                                    startStationName.setText(routeInfo.startStationName);

                                    endStationName = (TextView) findViewById(R.id.endStationName);
                                    endStationName.setText(routeInfo.endStationName);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                route_info_list_adapter.notifyDataSetChanged();

                            }else if(api.equals("busLocationList")) {
                                try {

                                    JSONArray jarray = new JSONObject(response).getJSONArray("body");   // JSONArray 생성

                                    ApiData.busLocation[] arr = new Gson().fromJson(jarray.toString(), ApiData.busLocation[].class);
                                    busLocationList = Arrays.asList(arr);


                                    Log.d("sb","busLocationList: "+ busLocationList);

                                    Collections.sort(busLocationList, new Comparator<ApiData.busLocation>() {
                                        @Override
                                        public int compare(ApiData.busLocation first, ApiData.busLocation second) {
                                            if(first.getStationSeq()  > second.getStationSeq()){
                                                return 1;
                                            }else if(first.getStationSeq()  < second.getStationSeq()){
                                                return -1;
                                            }else {
                                                return 0;
                                            }
                                        }
                                    });

                                    Log.d("sb","busLocationList2: "+ busLocationList);

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

//                Log.d("sb","favRouteInfoList.get(position).isOn : " +favRouteInfoList.get(position).isOn);
//                if(favRouteInfoList.get(position).isOn==1){
//                    holder.reserve_btn.setToggleOn();
//                }else{
//                    holder.reserve_btn.setToggleOff();
//                }
//
//                holder.reserve_btn.setOnToggleChanged(new ToggleButton.OnToggleChanged(){
//                    @Override
//                    public void onToggle(boolean on) {
//                        if(on){
//                            favRouteInfoList.get(position).isOn =1;
//                        }else{
//                            favRouteInfoList.get(position).isOn =0;
//                        }
//                        Log.d("sb", "on: "+on);
//                    }
//                });



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
            public ToggleButton reserve_btn;

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
                reserve_btn = (ToggleButton) itemView.findViewById(R.id.reserve_btn);

            }
        }

    }

    public class RecycleAdapter2 extends RecyclerView.Adapter<RecycleAdapter2.ViewHolder> {

        Context mContext;

        public RecycleAdapter2(Context context) {
            this.mContext = context;

        }

        @Override
        public RecycleAdapter2.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_route, parent, false);
            return new RecycleAdapter2.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecycleAdapter2.ViewHolder holder, final int position) {

            try {

                holder.stationName.setText(BusStationList.get(position).stationName);
                holder.stationNumber.setText(BusStationList.get(position).stationNumber.trim());
                if(BusStationList.get(position).stationNumber.trim().equals(Shared_Pref.beacon_stationNumber)){

                    holder.station_layout.setBackgroundColor(Color.parseColor("#BDBDBD"));
                }else{
                    holder.station_layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }

                holder.station_layout.setOnClickListener(
                        new Button.OnClickListener() {
                            public void onClick(View v) {

                                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(ActivityFavourite.this);
                                alert_confirm.setMessage("하차 예약을 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // 'YES'
                                                target_station = BusStationList.get(position).stationID;
                                                getOut("reserv/getOut");
                                            }
                                        }).setNegativeButton("취소",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // 'No'
                                                return;
                                            }
                                        });
                                AlertDialog alert = alert_confirm.create();
                                alert.show();


                            }
                        }
                );


                for(int i =0; i < busLocationList.size(); i++){

                    if(busLocationList.get(i).stationSeq ==position+1){

                        holder.bus.setVisibility(View.VISIBLE);
                        holder.plateNo.setVisibility(View.VISIBLE);
                        holder.remainSeatCnt.setVisibility(View.VISIBLE);
                        holder.bus_info_layout.setVisibility(View.VISIBLE);
                        holder.pointer.setVisibility(View.INVISIBLE);

                        holder.plateNo.setText(busLocationList.get(i).plateNo.substring(5));

                        if(busLocationList.get(i).remainSeatCnt.equals("-1")){
                            holder.remainSeatCnt.setVisibility(View.INVISIBLE);
                        }
                        else{
                            holder.remainSeatCnt.setText(busLocationList.get(i).remainSeatCnt+"석");
                        }
                        break;
                    }else{
                        holder.bus.setVisibility(View.INVISIBLE);
                        holder.plateNo.setVisibility(View.INVISIBLE);
                        holder.remainSeatCnt.setVisibility(View.INVISIBLE);
                        holder.bus_info_layout.setVisibility(View.INVISIBLE);
                        holder.pointer.setVisibility(View.VISIBLE);
                    }


                }



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
            public ImageView bus;
            public ImageView pointer;
            public TextView plateNo;
            public TextView remainSeatCnt;
            public RelativeLayout bus_info_layout;
            public RelativeLayout station_layout;

            public ViewHolder(final View itemView) {
                super(itemView);

                stationName = (TextView) itemView.findViewById(R.id.stationName);
                stationNumber = (TextView) itemView.findViewById(R.id.stationNumber);
                bus = (ImageView) itemView.findViewById(R.id.bus);
                pointer = (ImageView) itemView.findViewById(R.id.pointer);
                plateNo = (TextView) itemView.findViewById(R.id.plateNo);
                remainSeatCnt = (TextView) itemView.findViewById(R.id.remainSeatCnt);
                bus_info_layout = (RelativeLayout) itemView.findViewById(R.id.bus_info_layout);
                station_layout = (RelativeLayout) itemView.findViewById(R.id.station_layout);


            }
        }

    }


}
