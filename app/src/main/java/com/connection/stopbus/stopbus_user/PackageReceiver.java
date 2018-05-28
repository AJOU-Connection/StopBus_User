package com.connection.stopbus.stopbus_user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class PackageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(action.equals(Intent.ACTION_PACKAGE_ADDED)){


        } else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){

            // 앱이 삭제되었을 때

        } else if(action.equals(Intent.ACTION_PACKAGE_REPLACED)){



        }

    }


}

