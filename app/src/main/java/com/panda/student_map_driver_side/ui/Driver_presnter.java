package com.panda.student_map_driver_side.ui;

import android.util.Log;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.panda.student_map_driver_side.data.Repository.Driver_Repository;
import com.panda.student_map_driver_side.data.Repository.PresnterToRepoContract;
import io.reactivex.Observable;
import timber.log.Timber;

import java.util.concurrent.TimeUnit;

public class Driver_presnter implements Driver_Contract.Presenter
{
    private final Driver_Contract.View mView;
    private final Driver_Repository mRepository;
    private static Driver_presnter INSTANCE;
    private static final String TAG = "Driver_presnter";

    private Driver_presnter(final Driver_Repository mRepository, final Driver_Contract.View v) {
        this.mRepository = mRepository;
        this.mView = v;
    }

    public static Driver_presnter getInstance(Driver_Repository driver_repository, Driver_Contract.View view) {
        if (INSTANCE == null) {
            INSTANCE = new Driver_presnter(driver_repository, view);
        }
        return INSTANCE;
    }

    @Override
    public void getRoute(com.mapbox.geojson.Point mOrgin, com.mapbox.geojson.Point mDestinaion) {
        mRepository.getRoute(mOrgin, mDestinaion, new PresnterToRepoContract.DriverRemoteDataSrcJop.GetRouteResponse() {
            @Override
            public void sucess(DirectionsResponse body) {
                Timber.tag(TAG).v("Success");
                mRepository.SendLocationUpdates();
            }

            @Override
            public void faild() {
                Timber.tag(TAG).v("Failed");
            }
        });
        mRepository.hokUpListners(new PresnterToRepoContract.DriverRemoteDataSrcJop.HockUpListnersResponse() {
            @Override
            public void IncomingRequest(LatLng pickUpLocation, String id) {
                DealwithResposne(pickUpLocation, id);
            }

            @Override
            public void Detached() {

            }
        });
    }

    @Override
    public void DealwithResposne(final LatLng Location, final String id) {
        // let student know that His / her Request is Accepted Or Rejected
        mView.showIncommingRequist(new DealWithRequestResponse() {
            @Override
            public void accept() {
                mRepository.add_Marker(Location, id);
                // let student know that His / her Request is Accepted Or Rejected
            }

            @Override
            public void refuse() {

            }
        });
        // on Success  add Marker With Data
    }

    @Override
    public void Start() {
        mView.setPresenter(this);
    }

    public void destroy() {
        INSTANCE = null;
        Driver_Repository.destroyInstance();
    }
}
