package com.example.kiiru.liquorglass.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.kiiru.liquorglass.Model.Request;
import com.example.kiiru.liquorglass.Model.User;
import com.example.kiiru.liquorglass.Remote.APIService;
import com.example.kiiru.liquorglass.Remote.RetrofitClient;

/**
 * Created by Kiiru on 9/29/2017.
 */

public class Common {
    public static User currentUser;



    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static boolean isConnectedToInternet (Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager!=null){

            NetworkInfo [] info = connectivityManager.getAllNetworkInfo();
            if (info != null)
            {
                for (int i=0;i<info.length;i++)
                {
                 if (info [i].getState() ==NetworkInfo.State.CONNECTED)
                     return  true;
                }

            }

        }

        return  false;

    }
    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";

        else if (status.equals("1"))
            return "Coming your way";

        else
            return "Delivered";
    }
}
