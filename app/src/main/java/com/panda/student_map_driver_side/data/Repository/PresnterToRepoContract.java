package com.panda.student_map_driver_side.data.Repository;

import android.support.annotation.NonNull;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

public interface PresnterToRepoContract
{
    interface MapLocalDataSrcJop {
        void add_Marker(LatLng mMarkerLocation,String id);

    }

    interface DriverRemoteDataSrcJop {
        interface GetRouteResponse {
            void sucess(DirectionsResponse body);

            void faild();
        }

        interface HockUpListnersResponse {
            void IncomingRequest(LatLng pickUpLocation,String ID);

            void Detached();
        }

        void hokUpListners(@NonNull HockUpListnersResponse mCallBack);

        void SendLocationUpdates();

        void getRoute(@NonNull Point mOrgin, @NonNull Point mDestination, @NonNull GetRouteResponse mCallBack);
    }
}
