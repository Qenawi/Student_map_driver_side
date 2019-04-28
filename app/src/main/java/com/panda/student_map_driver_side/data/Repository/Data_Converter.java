package com.panda.student_map_driver_side.data.Repository;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Data_Converter {

    public static ArrayList<LatLng> ListFromString(String myList)
    {
        Log.v("QnQ", "ListToString xX : : " + myList);

        if (myList == null || TextUtils.isEmpty(myList)) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LatLng>>() {
        }.getType();
        return gson.fromJson(myList, type);
    }

    public static String ListToString(ArrayList<LatLng> myList) {
        Log.v("QnQ", "ListToString : : " + myList.size());
        if (myList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LatLng>>() {
        }.getType();
        return gson.toJson(myList, type);
    }
}
