package com.example.shareme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class InternetBroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        String action = intent.getAction();
        if(action.equals("android.net.conn.CONNECTIVITY_CHANGE")){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected())
                Toast.makeText(context.getApplicationContext(), "מחובר לאינטרנט", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context.getApplicationContext(), "אין חיבור לאינטרנט", Toast.LENGTH_SHORT).show();

        }

    }
}