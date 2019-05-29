package com.panda.student_map_driver_side.data.Repository;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.panda.student_map_driver_side.LocationJop;
import com.panda.student_map_driver_side.Location_updater_utill;
import com.panda.student_map_driver_side.locationJopBroadCastReciver;
import io.reactivex.Observable;

import javax.security.auth.Destroyable;

import java.util.concurrent.TimeUnit;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class Driver_Repository implements PresnterToRepoContract.DriverRemoteDataSrcJop, PresnterToRepoContract.MapLocalDataSrcJop {
    private static Driver_Repository INSTANCE = null;
    private final DriverRemoteDataSource mDriverDataSource;
    private final MapLocalSource mMapSource;
    public static final int LocationJopUD = 1002;
    private static Context mContex;
    private boolean IsMock = false;

    /*
   Cache
     */
    private Driver_Repository(boolean Mock, @NonNull DriverRemoteDataSource mDriverDataSource, @NonNull MapLocalSource mMapSource, Context mCont) {
        this.mDriverDataSource = mDriverDataSource;
        this.mMapSource = mMapSource;
        mContex = mCont;
        IsMock = Mock;

    }

    public static Driver_Repository getInstance(boolean Mock, @NonNull DriverRemoteDataSource mDriverDataSource, @NonNull MapLocalSource mMapSource, Context mcont) {
        if (INSTANCE == null) {
            INSTANCE = new Driver_Repository(Mock, mDriverDataSource, mMapSource, mcont);

        }
        return INSTANCE;
    }

    @Override
    public void add_Marker(LatLng l, String id) {
        mMapSource.add_Marker(l, id);
    }

    public static void destroyInstance() {
        INSTANCE = null;
        DriverRemoteDataSource.destroyInstance();
        MapLocalSource.destroyInstance();
    }

    @Override
    public void getRoute(@NonNull Point mOrgin, @NonNull Point mDestination, @NonNull final PresnterToRepoContract.DriverRemoteDataSrcJop.GetRouteResponse mCallBack) {
        mDriverDataSource.getRoute(mOrgin, mDestination, new GetRouteResponse() {
                    @Override
                    public void sucess(DirectionsResponse body) {
                        mMapSource.Rout_Sucess(body, mCallBack);
                    }

                    @Override
                    public void faild() {
                        mCallBack.faild();// send call back to presenter
                    }
                }
        );
    }

    @Override
    public void hokUpListners(@NonNull final HockUpListnersResponse mCallBack) {
        mDriverDataSource.initListners(mCallBack);
    }

    @SuppressLint("NewApi")
    @Override
    public void SendLocationUpdates() {
        // Add broadCast Receiver To Pull Location Update From Jop Location
        try {
            Location_updater_utill.INSTANCE.mHockUpListner(mMapSource::animate_Vichle_To_Point);
            // Launch Jop To  Automate Data Sending  ...
            JobScheduler jobScheduler = (JobScheduler) mContex
                    .getSystemService(JOB_SCHEDULER_SERVICE);
            ComponentName componentName = new ComponentName(mContex,
                    LocationJop.class);
            PersistableBundle pb = new PersistableBundle();
            if (IsMock) {
                Log.v("QNQ", "OnStartStop 0 0 0 0");
                pb.putString("mRoute", mMapSource.getRoutePoints());
            }
            @SuppressLint({"NewApi", "LocalSuppress"}) JobInfo jobInfoObj = new JobInfo.Builder(LocationJopUD, componentName)
                    .setRequiresBatteryNotLow(true).setExtras(pb).build();
            jobScheduler.schedule(jobInfoObj);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
