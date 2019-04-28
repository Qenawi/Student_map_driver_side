package com.panda.student_map_driver_side.ui;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.panda.student_map_driver_side.BasePresenter;
import com.panda.student_map_driver_side.BaseView;

public interface Driver_Contract
{
    interface View extends BaseView<Presenter>
    {

        /*GetRoute in Populate View With Result*/
        void showIncommingRequist(Presenter.DealWithRequestResponse mcall);
        /*InComing Request Handel it ith Acc or Dec */
    }
    /*
   presenter is a person or organization responsible
   for the running of a public event,
    or someone who conveys information
     or media via a broadcasting outlet.
     */
    interface Presenter extends BasePresenter
    {
        interface DealWithRequestResponse {
            void accept();

            void refuse();
        }
        void getRoute(Point mOrgin, Point mDestination);
        void DealwithResposne(LatLng Location,String  Id);
        /*
         * get Route points and deliver Results
         *
         * */
    }

    interface LocationJopToRepoSotory
    {
        void onLocationChange(LatLng mNewLocation);
    }
}
