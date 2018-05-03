package com.connection.stopbus.stopbus_user;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

/**
 * Created by Danbk on 2018-04-05.
 */

public class ActivitySearchFav extends Activity{


    private SwipeRefreshLayout swipeContainer0;
    private SwipeRefreshLayout swipeContainer1;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;

    private RecycleAdapter SearchBusListAdapter = new RecycleAdapter(this);
    private RecycleAdapter2 SearchStationListAdapter = new RecycleAdapter2(this);


        @Override
        protected void onCreate(final Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search_fav);


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
                            getBaseContext()).inflate(R.layout.list_search_bus, null, false);
                    recyclerView = (RecyclerView) view.findViewById(R.id.rv_search_bus_list);
                    swipeContainer0 = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout0);
                    // Setup refresh listener which triggers new data loading
                    swipeContainer0.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            // Your code to refresh the list here.
                            // Make sure you call swipeContainer.setRefreshing(false)
                            // once the network request has completed successfully.

                            swipeContainer0.setRefreshing(false);

                        }
                    });
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(
                                    getBaseContext(), LinearLayoutManager.VERTICAL, false
                            )
                    );
                    recyclerView.setAdapter(SearchBusListAdapter);


                } else if (position == 1) {


                    view = LayoutInflater.from(
                            getBaseContext()).inflate(R.layout.list_search_station, null, false);
                    recyclerView2 = (RecyclerView) view.findViewById(R.id.rv_search_station_list);
                    swipeContainer1 = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout0);
                    // Setup refresh listener which triggers new data loading
                    swipeContainer1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            // Your code to refresh the list here.
                            // Make sure you call swipeContainer.setRefreshing(false)
                            // once the network request has completed successfully.

                            swipeContainer1.setRefreshing(false);

                        }
                    });
                    recyclerView2.setHasFixedSize(true);
                    recyclerView2.setLayoutManager(new LinearLayoutManager(
                                    getBaseContext(), LinearLayoutManager.VERTICAL, false
                            )
                    );
                    recyclerView2.setAdapter(SearchStationListAdapter);

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
                        Color.parseColor(colors[0]))
                        .title("버스")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_transfer_within_a_station_white_36pt),
                        Color.parseColor(colors[0]))
                        .title("정류장")
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

            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });


    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

        Context mContext;

        public RecycleAdapter(Context context) {
            this.mContext = context;

        }

        @Override
        public RecycleAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_search_bus, parent, false);
            return new RecycleAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecycleAdapter.ViewHolder holder, final int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {



            public ViewHolder(final View itemView) {
                super(itemView);


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
            final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_search_station, parent, false);
            return new RecycleAdapter2.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecycleAdapter2.ViewHolder holder, final int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {



            public ViewHolder(final View itemView) {
                super(itemView);

            }
        }

    }
}
