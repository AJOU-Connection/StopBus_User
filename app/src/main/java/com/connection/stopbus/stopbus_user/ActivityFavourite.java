package com.connection.stopbus.stopbus_user;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Danbk on 2018-04-04.
 */

public class ActivityFavourite extends Activity{

    private RecycleAdapter favourite_list = new RecycleAdapter(this);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);


    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

        Context mContext;

        public RecycleAdapter(Context context) {
            this.mContext = context;

        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.favourite_list_part, parent, false);
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


            public ViewHolder(final View itemView) {
                super(itemView);

            }
        }

    }


}
