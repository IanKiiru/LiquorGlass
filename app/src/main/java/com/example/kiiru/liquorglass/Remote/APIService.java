package com.example.kiiru.liquorglass.Remote;

import com.example.kiiru.liquorglass.Model.MyResponse;
import com.example.kiiru.liquorglass.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Kiiru on 11/13/2017.
 */

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAHMo6iEg:APA91bFXqeL3lvvWAdwN2Uv5eWL7O0deLhUrfLgJijn2iVBW24Q7dQHo3tmm1CRRGXqFFBw8739J3J6UiqeVaSBERfJ_ZrJWV1pZAavVwLPNeJehc82rSQMBbyd6Mfdtn8VT5MBQ4x0A"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
