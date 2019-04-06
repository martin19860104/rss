package com.exemple.eac2_2017s1;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Util {

    public static boolean hayConexion(Activity activity) {
        boolean hayConexion = false;
        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            hayConexion = activeNetwork.isConnected();
        }
        return hayConexion;
    }
}
