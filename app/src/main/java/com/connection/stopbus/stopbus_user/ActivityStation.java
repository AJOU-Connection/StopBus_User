package com.connection.stopbus.stopbus_user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ActivityStation extends Activity{

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

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


}
