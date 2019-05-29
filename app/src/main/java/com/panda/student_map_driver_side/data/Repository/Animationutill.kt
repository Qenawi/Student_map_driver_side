package com.panda.student_map_driver_side.data.Repository

import android.animation.Animator
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.LinearInterpolator
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class Animationutill(val mcallBack: updateLocationMainThread) {
    private var NextFloat_Bearing: Float = 90f
    private var ArrayLis: ArrayList<LatLng>? = ArrayList<LatLng>()
    private var mCount: AtomicInteger = AtomicInteger(0)
    private var isAnimating: AtomicBoolean = AtomicBoolean(false)
    fun addPoint(mNewlocation: LatLng)
    {
        ArrayLis!!.add(mNewlocation)
        if (ArrayLis!!.size < 2) {
            return
        }
        if (isAnimating.get())
        {
            return
// Do No Thing
        } else {
            startAnimation()
        }
        //else start Animating
    }

    private fun startAnimation() {
        GlobalScope.launch(Dispatchers.IO)
        {
            while (mCount.get() < ArrayLis!!.size) {
                delay(700)
                withContext(Dispatchers.Main) { mAnimateToNextPoint() }

            }
        }
    }

    private fun mAnimateToNextPoint() {

        animate()
    }

    private var valueAnimator: ValueAnimator? = null

    private fun animate()
    {
        if (mCount.get()>=ArrayLis!!.size||mCount.get()+1>=ArrayLis!!.size){return}
        var startPosition: LatLng = ArrayLis!![mCount.get()]
        Log.v("mCount",mCount.get().toString())
        mCount.set(mCount.get() + 1)
        Log.v("mCount",mCount.get().toString())
        var endPosition: LatLng = ArrayLis!![mCount.get()]
        valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator!!.duration = 500
        valueAnimator!!.interpolator = LinearInterpolator()
        valueAnimator!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                isAnimating.set(true)

            }

            override fun onAnimationEnd(animation: Animator) {
                isAnimating.set(false)

            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        valueAnimator!!.addUpdateListener {
            Log.v("run", "listner")
            //animate Point To to point  Start -> end 0 -> 1
            var v = it.animatedFraction
            var lng = v * endPosition.getLongitude() + (1 - v) * startPosition.getLongitude()
            var lat = v * endPosition.getLatitude() + (1 - v) * startPosition.getLatitude()
            val newPos = LatLng(lat, lng)
            NextFloat_Bearing = getBearing(startPosition, newPos)
            animate_Helper_Update_Marker(newPos)

        }
        valueAnimator!!.start()
    }

    private fun animate_Helper_Update_Marker(newPos: LatLng) {
        /*
        if (mapboxMap != null && mapboxMap.getStyle() != null) {
            mapboxMap.getStyle()!!.getLayer("layer-id")!!.setProperties(
                PropertyFactory.iconRotate(NextFloat_Bearing),
                PropertyFactory.iconAnchor(com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_CENTER),
                PropertyFactory.visibility(com.mapbox.mapboxsdk.style.layers.Property.VISIBLE)
            )// Rotation And Anchor
        }
        geoJsonSource.setGeoJson(Point.fromLngLat(newPos.longitude, newPos.latitude))
        */
        mcallBack.animiUtil_Update(newPos)
    }

    private fun getBearing(begin: LatLng, end: LatLng): Float {
        val lat = Math.abs(begin.latitude - end.latitude)
        val lng = Math.abs(begin.longitude - end.longitude)

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return Math.toDegrees(Math.atan(lng / lat)).toFloat()
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (90 - Math.toDegrees(Math.atan(lng / lat)) + 90).toFloat()
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (Math.toDegrees(Math.atan(lng / lat)) + 180).toFloat()
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (90 - Math.toDegrees(Math.atan(lng / lat)) + 270).toFloat()
        return -1f
    }

    interface updateLocationMainThread {
        fun animiUtil_Update(on: LatLng) {}
    }

    fun destroyInstance() {
        if (valueAnimator != null)
            valueAnimator!!.removeAllListeners()
    }
}