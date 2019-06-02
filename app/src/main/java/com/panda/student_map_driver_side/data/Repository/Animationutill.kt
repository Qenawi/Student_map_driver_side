package com.panda.student_map_driver_side.data.Repository

import android.animation.Animator
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.LinearInterpolator
import com.mapbox.mapboxsdk.geometry.LatLng
import com.panda.student_map_driver_side.data.Failure
import com.panda.student_map_driver_side.data.Result
import com.panda.student_map_driver_side.data.Success
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Animationutill(val mcallBack: updateLocationMainThread) {
    private var NextFloat_Bearing: Float = 90f
    private var ArrayLis: ArrayList<LatLng>? = ArrayList<LatLng>()
    private var mCount: AtomicInteger = AtomicInteger(0)
    private var isAnimating: AtomicBoolean = AtomicBoolean(false)
    private var valueAnimator: ValueAnimator? = null

    fun addPoint(mNewlocation: LatLng)
    {
        ArrayLis!!.add(mNewlocation)
      try
      {
      if(valueAnimator!=null&&valueAnimator!!.isRunning)
      {
          Log.v("run", "Cant Accept This Add Requst Sry")
      }
          else
      {
          Log.v("run", "Starting Animation ")
          startAnimation()

      }

      }catch (e:Exception)
      {

      }
    }

    /**
    startAnimation can only be Called if animation has stoped or at the first time
     **/
    private fun startAnimation() {

        Log.v("run", "Loop Head ${mCount.get()}")
        GlobalScope.launch {
            val e = mAnimateToNextPoint()
            when (e) {
                is Success -> {
                    Log.v("run", "Over All Sucess")

                }
                is Failure -> {
                    Log.v("run", "Over All Faild")

                }
            }
        }
    }

    private suspend fun mAnimateToNextPoint(): Result<Boolean> = runBlocking {
        animate()
    }


    private suspend fun animate(): Result<Boolean> = suspendCoroutine {
        if (mCount.get() >= ArrayLis!!.size || mCount.get() + 1 >= ArrayLis!!.size)
        {
            Log.v("run", "Out Of Bounds ${mCount.get()}")
            it.resume(Failure(ArrayIndexOutOfBoundsException("Faild")))
        }
        var startPosition: LatLng = ArrayLis!![mCount.get()]
        mCount.set(mCount.get() + 1)
        var endPosition: LatLng = ArrayLis!![mCount.get()]
        valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator?.duration = 700
        valueAnimator?.interpolator = LinearInterpolator()

        valueAnimator?.addUpdateListener {
            //animate Point To to point  Start -> end 0 -> 1
            var v = it.animatedFraction
            var lng = v * endPosition.getLongitude() + (1 - v) * startPosition.getLongitude()
            var lat = v * endPosition.getLatitude() + (1 - v) * startPosition.getLatitude()
            val newPos = LatLng(lat, lng)
            NextFloat_Bearing = getBearing(startPosition, newPos)
            animate_Helper_Update_Marker(newPos)
        }
        Log.v("run", "Start Update Listner")
        valueAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                Log.v("run", "onAnimationRepeat")

            }

            override fun onAnimationEnd(animation: Animator?) {

                try {
                    valueAnimator?.cancel()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                it.resume(Success(true))
            }

            override fun onAnimationCancel(animation: Animator?) {
                Log.v("run", "onAnimationRepeat")
            }

            override fun onAnimationStart(animation: Animator?) {
                Log.v("run", "onAnimationRepeat")
            }
        })
        valueAnimator?.start()

    }

    private fun animate_Helper_Update_Marker(newPos: LatLng) {

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

    fun destroyInstance()
    {
        if (valueAnimator != null)
            valueAnimator!!.removeAllListeners()
    }
}