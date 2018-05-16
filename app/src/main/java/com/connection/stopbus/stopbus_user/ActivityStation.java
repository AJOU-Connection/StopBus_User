package com.connection.stopbus.stopbus_user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Danbk on 2018-05-04.
 */

public class ActivityStation extends Activity{

    private RecyclerView recyclerView;
    private RecycleAdapter station_bus_list_adapter = new RecycleAdapter(this);
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);


        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_layout0);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeContainer.setRefreshing(false);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.rv_search_bus_list);
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
                        Intent i = new Intent(ActivityStation.this, ActivitySearchFav.class);
                        startActivity(i);

                    }
                }
        );

        findViewById(R.id.home).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Log.d("sb", "home button pressed");
                        Intent i = new Intent(ActivityStation.this, ActivityFavourite.class);
                        startActivity(i);

                    }
                }
        );



    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

        Context mContext;

        public RecycleAdapter(Context context) {
            this.mContext = context;

        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_station_bus, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {



        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView bus_num;
            public TextView bus_time_1;
            public TextView bus_time_2;
            public TextView bus_location1;
            public TextView bus_location2;
            public TextView bus_type;

            public ViewHolder(final View itemView) {
                super(itemView);
                bus_num = (TextView) itemView.findViewById(R.id.bus_num);
                bus_time_1 = (TextView) itemView.findViewById(R.id.bus_time_1);
                bus_time_2 = (TextView) itemView.findViewById(R.id.bus_time_2);
                bus_location1 = (TextView) itemView.findViewById(R.id.bus_location1);
                bus_location2 = (TextView) itemView.findViewById(R.id.bus_location2);
                bus_type = (TextView) itemView.findViewById(R.id.bus_type);

            }
        }

    }


}
