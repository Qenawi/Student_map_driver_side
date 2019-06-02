package com.panda.student_map_driver_side.data.Repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.firebase.database.*;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.panda.student_map_driver_side.data.PickupRequst;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/*
Get Route From MapBox Sdk
and any Other WepService
Fire base and Location Update
*/
public class DriverRemoteDataSource {
    private ChildEventListener Confermation_Value_Event_Listner;
    private DatabaseReference databaseReference;
    private static final String NAVIGATION = "Navigation";
    private static final String PICKUPREQUEST = "PickupRequst";
    private String ROUTEID ;
    private static final String TAG = "DIRVERREPO";
    @SuppressLint("StaticFieldLeak")
    private static DriverRemoteDataSource INiSTANCE = null;
    private Context context;

    private DriverRemoteDataSource(Context context, String LineUD) {

        this.context = context;
        this.ROUTEID = LineUD;

    }

    public static DriverRemoteDataSource getInistance(Context context, String LineID) {
        if (INiSTANCE == null) {
            INiSTANCE = new DriverRemoteDataSource(context, LineID);
        }
        return INiSTANCE;
    }

    private void getRouteHelper(Point mOrigin, Point mDestination, PresnterToRepoContract.DriverRemoteDataSrcJop.GetRouteResponse mCallBack) {
        NavigationRoute.builder(context).accessToken(Mapbox.getAccessToken()).origin(mOrigin).destination(mDestination).
                build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                mCallBack.sucess(response.body());
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                mCallBack.faild();
            }
        });
    }

    void getRoute(final Point mOrgin, final Point mDestination, final PresnterToRepoContract.DriverRemoteDataSrcJop.GetRouteResponse mCallBack) {
        getRouteHelper(mOrgin, mDestination, mCallBack);
    }

    void initListners(@NonNull final PresnterToRepoContract.DriverRemoteDataSrcJop.HockUpListnersResponse mRequests_callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        HockupRequestListner(mRequests_callback);
    }

    private void HockupRequestListner(@NonNull final PresnterToRepoContract.DriverRemoteDataSrcJop.HockUpListnersResponse mRequests_callback) {
        Query query = databaseReference.child(NAVIGATION).child(ROUTEID).child(PICKUPREQUEST);
        Confermation_Value_Event_Listner = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                PickupRequst pickupRequst = dataSnapshot.getValue(PickupRequst.class);
                if (pickupRequst != null && pickupRequst.getState().equals("pending"))
                {
                    LatLng latLng = null;
                    try {
                        String arr[] = pickupRequst.getLocation().split(",");
                        latLng = new LatLng(Float.valueOf(arr[0]), Float.valueOf(arr[1]));

                    } catch (Exception e) {
                        Timber.tag(TAG).v(e);
                        latLng = new LatLng(0.0, 0.0);
                    } finally {
                        mRequests_callback.IncomingRequest(latLng, dataSnapshot.getKey());
                        Timber.tag(TAG).v(dataSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                PickupRequst pickupRequst = dataSnapshot.getValue(PickupRequst.class);
                LatLng latLng = null;
                try {
                    String arr[] = pickupRequst.getLocation().split(",");
                    latLng = new LatLng(Float.valueOf(arr[0]), Float.valueOf(arr[1]));

                }
                catch (Exception e)
                {
                    Timber.tag(TAG).v(e);
                    latLng = new LatLng(0.0, 0.0);
                } finally {
                    mRequests_callback.IncomingRequest(latLng, dataSnapshot.getKey());
                    Timber.tag(TAG).v(dataSnapshot.getKey());
                }
                }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void notifyStudent(String Response,String id)
    {
         databaseReference.child(NAVIGATION).child(ROUTEID).child(PICKUPREQUEST).child(id).
                 child("state").setValue(Response);
    }

    public static void destroyInstance() {
        INiSTANCE = null;

    }
}

