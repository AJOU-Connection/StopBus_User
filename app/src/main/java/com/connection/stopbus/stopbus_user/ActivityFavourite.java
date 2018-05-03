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
 * Created by Danbk on 2018-04-04.
 */

public class ActivityFavourite extends Activity{

    private RecyclerView recyclerView;
    private RecycleAdapter favourite_bus_list_adapter = new RecycleAdapter(this);
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

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
                        startActivity(i);
                    }
                }
        );



    }

    public void CallMyBusList(){


    }


    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

        Context mContext;

        public RecycleAdapter(Context context) {
            this.mContext = context;

        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_fav_bus, parent, false);
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
            public TextView bus_schedule1;
            public TextView bus_schedule2;
            public TextView bus_type;

            public ViewHolder(final View itemView) {
                super(itemView);
                bus_num = (TextView) itemView.findViewById(R.id.bus_num);
                bus_schedule1 = (TextView) itemView.findViewById(R.id.bus_schedule_1);
                bus_schedule2 = (TextView) itemView.findViewById(R.id.bus_schedule_2);
                bus_type = (TextView) itemView.findViewById(R.id.bus_type);

            }
        }

    }


}
