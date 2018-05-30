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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

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
    private List<ApiData.routeInfo> RouteInfoList = new ArrayList<ApiData.routeInfo>();
    private List<ApiData.routeInfo> CopyRouteInfoList;

    ArrayList<String> favouriteList;
    Handler mHandler = new Handler();

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
        recyclerView = (RecyclerView) findViewById(R.id.rv_favourite_bus_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                        getBaseContext(), LinearLayoutManager.VERTICAL, false
                )
        );
        recyclerView.setAdapter(favourite_bus_list_adapter);


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

        ImageView bus_stop_image = (ImageView) findViewById(R.id.bus_stop_image);
        bus_stop_image.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("sb", "search for bus stop");


                        Intent i = new Intent(ActivityFavourite.this, ActivitySearchFav.class);
                        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
        );


    }

    public void CallMyBusList(){

        final TinyDB tinydb = new TinyDB(ActivityFavourite.this);
        favouriteList= tinydb.getListString("Favourite");
        Log.d("sb", "favouriteList: " + favouriteList);
        Iterator<String> itr = favouriteList.iterator();
        Log.d("sb", "favouriteSize:  " + favouriteList.size());

        while(itr.hasNext()){
            String id = itr.next();
            CallData("routeInfo", id);
        }
        favourite_bus_list_adapter.notifyDataSetChanged();
    }
    //검색 불러오는 API
    public synchronized void CallData(final String api, final String routeID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Map<String, String> args = new HashMap<String, String>();
                args.put("routeID",  routeID);

                try {
                    final String response = NetworkService.INSTANCE.postQuery(api, args);
                    Log.d("sb","333333"+response);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jObject = new JSONObject(response).getJSONObject("body");

                                Log.d("sb","jobject"+ jObject);

                                ApiData.routeInfo arr= new Gson().fromJson(jObject.toString(), ApiData.routeInfo.class);
                                Log.d("sb","4444444"+arr);

                                RouteInfoList = Arrays.asList(arr);
                                CopyRouteInfoList = new ArrayList<ApiData.routeInfo>();
                                CopyRouteInfoList.addAll(RouteInfoList);


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

//            holder.delete_btn.setVisibility(View.GONE);
//            holder.routeNumber.setText(RouteInfoList.get(position).routeNumber);
//            holder.regionName.setText(RouteInfoList.get(position).regionName);
//            holder.routeTypeName.setText(RouteInfoList.get(position).routeTypeName);
//            holder.upFirstTime.setText(RouteInfoList.get(position).upFirstTime);
//            holder.upLastTime.setText(RouteInfoList.get(position).upLastTime);
//            holder.downFirstTime.setText(RouteInfoList.get(position).downFirstTime);
//            holder.downLastTime.setText(RouteInfoList.get(position).downLastTime);

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

            }
        }

    }


}
