package com.panda.student_map_driver_side

import com.mapbox.mapboxsdk.geometry.LatLng
import com.panda.student_map_driver_side.ui.Driver_Contract

object  Location_updater_utill
{


    var locationUpdateCallBack: Driver_Contract.LocationJopToRepoSotory? = null

     fun mHockUpListner(mC: Driver_Contract.LocationJopToRepoSotory?)
     {
        locationUpdateCallBack = mC
    }

    fun mPostUpdate(mloc: LatLng)
    {
        locationUpdateCallBack?.onLocationChange(mloc)
    }

}