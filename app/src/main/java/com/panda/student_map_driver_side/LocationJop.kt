package com.panda.student_map_driver_side

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.ComponentName
import android.content.Intent
import android.location.Location
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.util.TimeUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.patloew.rxlocation.RxLocation
import io.reactivex.disposables.CompositeDisposable
import com.google.android.gms.location.LocationRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.mapboxsdk.geometry.LatLng
import com.panda.student_map_driver_side.data.Repository.Data_Converter
import io.reactivex.Scheduler
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.operators.single.SingleInternalHelper.toObservable
import io.reactivex.internal.schedulers.IoScheduler
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.math.log

@SuppressLint("NewApi")
class LocationJop : JobService() {
    private val NAVIGATION = "Navigation"
    private val PICKUPREQUEST = "PickupRequst"
    private val ROUTEID = "301"
    private val Currnt_Pus_Location = "currnt_Pus_Location"
    private val TAG = "DIRVERREPO"
    var rxLocation: RxLocation? = null
    var cd: CompositeDisposable? = null
    var mPoints: ArrayList<LatLng>? = null
    var mCounter = 0
    private var databaseReference: DatabaseReference? = null
    override fun onStopJob(params: JobParameters?): Boolean {
        Log.v(TAG, "OnJopStop")
        cd!!.clear()
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.v(TAG, "OnStartStop")
        if (params != null && params.extras.containsKey("mRoute")) {
            mPoints = Data_Converter.ListFromString(params.extras.getString("mRoute"))
            Log.v("QNQ", "Mocking Location.... ${mPoints!!.size}")

        }
        mCounter = 0;
        cd = CompositeDisposable()
        rxLocation = RxLocation(this)
        val database = FirebaseDatabase.getInstance()
        databaseReference = database.reference
        if (mPoints == null || mPoints!!.isEmpty())
            sendLocationUpdate()
        else sendLocationUpdateMock()
        return true
    }

    @SuppressLint("MissingPermission")
    private fun sendLocationUpdate() {
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(TimeUnit.MINUTES.toMillis(1))
        cd!!.add(rxLocation!!.location().updates(locationRequest)
            .flatMap { location -> rxLocation!!.geocoding().fromLocation(location).toObservable() }.subscribeOn(
                Schedulers.io()
            ).observeOn(AndroidSchedulers.mainThread())
            .subscribe { address ->
                notfy_server(address.latitude.toString() + "," + address.longitude.toString())
            })
    }

    private fun sendLocationUpdateMock() {

        Log.v("QNQ", "Mocking Location.... $mCounter")

        cd?.add(mRequee().subscribe(
            { _ ->
                if (mCounter >= mPoints!!.size)
                {
                    Log.v(TAG, "Error")
                }
                else {
                    notfy_server("${mPoints!![mCounter].latitude},${mPoints!![mCounter].longitude}")
                    GlobalScope.launch(Dispatchers.Main){
                        Location_updater_utill.mPostUpdate(mPoints!![mCounter])

                    }
                    mCounter += 1
                    sendLocationUpdateMock()
                }
            },
            { e -> e.printStackTrace() }
        ))
    }


    private fun notfy_server(s: String)
    {

        // Send local broadcast
        val query = databaseReference!!.child(NAVIGATION).child(ROUTEID).child(Currnt_Pus_Location)
        query.setValue(s).addOnSuccessListener { Log.v(TAG, "Sucess { $s }") }
    }

    fun mRequee(): Observable<Long>

    {

        return Observable.timer(2, TimeUnit.SECONDS).observeOn(Schedulers.io()).observeOn(Schedulers.io())

    }


    }