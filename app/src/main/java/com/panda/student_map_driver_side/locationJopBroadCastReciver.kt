package com.panda.student_map_driver_side

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mapbox.mapboxsdk.geometry.LatLng
import com.panda.student_map_driver_side.ui.Driver_Contract
import java.io.IOException
import kotlin.math.log

class locationJopBroadCastReciver : BroadcastReceiver() {
    private var mCallBack: Driver_Contract.LocationJopToRepoSotory? = null
    @SuppressLint("LogNotTimber")
    override fun onReceive(context: Context?, intent: Intent?) {
        try {

            Log.v("eqnqn","Reciverd")
            val mes: String = intent!!.getStringExtra("mDataeee")
            val sp: List<String> = mes.split(",")
            mCallBack!!.onLocationChange(LatLng(sp[0].toDouble(), sp[1].toDouble()))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}