package com.panda.student_map_driver_side.data.Repository;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.panda.student_map_driver_side.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;
import com.mapbox.mapboxsdk.annotations.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
Map Editing and Manipulation
 */
public class MapLocalSource implements Animationutill.updateLocationMainThread {
    private static MapLocalSource INSTANCE = null;
    private final MapboxMap mapboxMap;
    private final MapView mapView;
    private Marker car;
    private DirectionsRoute currentRoute;
    private CompositeDisposable Cdisposable;
    private List<LatLng> Route_Points = new ArrayList<>(); // PolyLine
    private NavigationMapRoute navigationMapRoute = null;
    private HashMap<String, Marker> studentToMarker;
    private static Animationutill animationutill;

    /*
    Constructor
     */
    private MapLocalSource(@NonNull MapboxMap mapboxMap, @NonNull MapView mapView) {
        this.mapboxMap = mapboxMap;
        this.mapView = mapView;
        Route_Points = new ArrayList<>();
        Cdisposable = new CompositeDisposable();
        studentToMarker = new HashMap<>();
        animationutill = new Animationutill(this);

    }

    /*
   Get Instance
    */
    public static MapLocalSource getInstance(MapboxMap mapboxMap, MapView mapView) {
        if (INSTANCE == null) {
            INSTANCE = new MapLocalSource(mapboxMap, mapView);
        }
        return INSTANCE;
    }

    void add_Marker(LatLng marker, String studentID) {
        try {
            if (studentToMarker.containsKey(studentID) && studentToMarker.get(studentID) != null) {
                studentToMarker.get(studentID).setPosition(marker);
            } else
                add_MarkerHelper(marker, studentID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void animate_Vichle_To_Point(LatLng mnew) {
        animate_Vichle_To_PointHelper(mnew);
    }

    private void add_MarkerHelper(LatLng marker, String studentID) {
        Marker marker1 = mapboxMap.addMarker(new MarkerOptions().position(marker).title("M Qenawi"));
        studentToMarker.put(studentID, marker1);
    }

    @SuppressLint("LogNotTimber")
    private void animate_Vichle_To_PointHelper(LatLng point) {
        Log.v("animate To Point", "Qnqn");
        animationutill.addPoint(point);
        /*
        - x Top c current
        - on starting animation {animate from point c to point x}
        - increase c by 1
         */
    }

    void Rout_Sucess(DirectionsResponse directionsResponse, @NonNull final PresnterToRepoContract.DriverRemoteDataSrcJop.GetRouteResponse mCallBack) {
        currentRoute = directionsResponse.routes().get(0);
        // back Thread
        Cdisposable.add
                (
                        io.reactivex.Observable.fromCallable(() -> {
                            ArrayList<LatLng> Ret = new ArrayList<LatLng>();
                            LineString lineString = LineString.fromPolyline(currentRoute.geometry(), Constants.PRECISION_6);
                            List<Point> coordinates = lineString.coordinates();
                            Timber.tag("Current Thread").v(Thread.currentThread().getName());
                            for (int i = 0; i < coordinates.size(); i++) {
                                Ret.add(new LatLng(
                                        coordinates.get(i).latitude(),
                                        coordinates.get(i).longitude()));
                            }
                            return Ret;
                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(s ->
                        {
                            Route_Points = s;
                            mCallBack.sucess(null);
                            DrawRoute();
                        }, e -> {
                            mCallBack.faild();

                        })
                );
    }

    private void DrawRoute() {
        if (navigationMapRoute != null) {
            navigationMapRoute.removeRoute();
        } else {
            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
        }

        car = mapboxMap.addMarker(new MarkerOptions().position(Route_Points.get(0)));

        navigationMapRoute.addRoute(currentRoute);
        //     Add_End_Route_Point();

        //Messege.setTextColor(getResources().getColor(R.color.carbon_green_200));
        // Messege.setText("Waiting For Location Picking ...");
        //   Animateee();
    }

    public String getRoutePoints() {
        return Data_Converter.ListToString((ArrayList<LatLng>) Route_Points);
    }

    public static void destroyInstance() {
        animationutill.destroyInstance();
        INSTANCE = null;

    }

    @Override
    public void animiUtil_Update(@NotNull LatLng on)
    {
        car.setPosition(on);

    }
}
