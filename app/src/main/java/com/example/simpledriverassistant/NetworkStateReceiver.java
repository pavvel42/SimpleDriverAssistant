package com.example.simpledriverassistant;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateReceiver {

    public NetworkStateReceiver() {
    }

    private static final String TAG = NetworkStateReceiver.class.getSimpleName();

    protected boolean haveNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d(TAG, "Network connectivity status true");
            return true;
        } else {
            Log.d(TAG, "Network connectivity status false");
            return false;
        }
    }
}
