package com.panda.student_map_driver_side.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.job.JobScheduler;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.panda.student_map_driver_side.R;
import com.panda.student_map_driver_side.data.Repository.DriverRemoteDataSource;
import com.panda.student_map_driver_side.data.Repository.Driver_Repository;
import com.panda.student_map_driver_side.data.Repository.MapLocalSource;
import timber.log.Timber;
import com.panda.student_map_driver_side.data.Route_Module;

import java.util.List;

public class Driver_View extends AppCompatActivity implements PermissionsListener, Driver_Contract.View, OnMapReadyCallback {
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.picktxt)
    TextView picktxt;
    @BindView(R.id.Messge_box)
    TextView Messge_box;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;// To Acquire Permission
    private Driver_presnter mPresnter = null;
    public static final String mTAg = "mrQenawi..";
    public Route_Module Selected_route_module = null;
    LatLng SelectedRoute = null;
    private static final String Navigation = "Navigation";
    private static final String Lines = "Lines";
    private static final String State = "State";
    private static final String Active = "active";
    private static final String DisActive = "notctive";
    private boolean IsMockLocationUpdate = false;
    private int Total_Count=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.driver_view);
        ButterKnife.bind(this);


        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RouteModule")) {
            Selected_route_module = getIntent().getExtras().getParcelable("RouteModule");
            String[] points = Selected_route_module.getStartPoint().split(",");
            SelectedRoute = new LatLng(Double.valueOf(points[0]), Double.valueOf(points[1]));
            IsMockLocationUpdate = getIntent().getExtras().getBoolean("Mock");
        }


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    //-------------------PerMission Callback------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "R.string.user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(this::enableLocationComponent);
        } else {
            Toast.makeText(this, "R.string.user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
// Activate with options
            locationComponent.activateLocationComponent(this, loadedMapStyle);
// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);
// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    //-------------------------View Call back---------------------------

    @Override
    public void showIncommingRequist(Driver_Contract.Presenter.DealWithRequestResponse mcall) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Asp") //
                    .setMessage("Fe Requst Gy PlZ Accept ") //
                    .setPositiveButton("Accept", (dialog, id) -> {
                        // TODO
                        dialog.dismiss();
                        Total_Count+=1;
                        picktxt.setText("( "+Total_Count+" )"+"Student To Pick");
                        mcall.accept();
                    }) //
                    .setNegativeButton("Decline", (dialog, id) -> {
                        // TODO
                        dialog.dismiss();
                        mcall.refuse();
                    });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setPresenter(Driver_Contract.Presenter presenter) {
        Timber.tag(mTAg).v("setPresenter");
    }

    //----------------------------------------------------------------------
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        /* mPresnter init Data Cause It Depend On Map Object */
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @SuppressLint("MissingPermission")
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                enableLocationComponent(style);
                style.addImage(("car_icon_name"), BitmapFactory.decodeResource(getResources(), R.drawable.ic_car));
                mPresnter = Driver_presnter.getInstance(Driver_Repository.getInstance(IsMockLocationUpdate, DriverRemoteDataSource.getInistance(Driver_View.this, String.valueOf(Selected_route_module.getLineId())), MapLocalSource.getInstance(mapboxMap, mapView), Driver_View.this), Driver_View.this);
                initData();
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        JobScheduler jobScheduler = (JobScheduler) this
                .getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(Driver_Repository.LocationJopUD);
        mPresnter.destroy();
        deActivateRoute();
        super.onDestroy();
    }

    private void deActivateRoute() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Navigation);
        myRef.child(String.valueOf(Selected_route_module.getLineId())).child(State).setValue(DisActive).addOnSuccessListener(aVoid ->
        {
            Timber.tag("FireBaseInterACtion").v("Route is disActive");

        });
    }
    private void initData() {
        Point dest = Point.fromLngLat(31.2827528, 29.9828841);
        @SuppressLint("MissingPermission") Point origin = Point.fromLngLat(SelectedRoute.getLongitude(), SelectedRoute.getLatitude());
        mPresnter.getRoute(origin, dest);
    }
}
