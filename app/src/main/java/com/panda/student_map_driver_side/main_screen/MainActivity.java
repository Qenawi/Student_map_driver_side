package com.panda.student_map_driver_side.main_screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.widget.CheckBox;
import carbon.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;
import com.panda.student_map_driver_side.data.Route_Module;
import com.panda.student_map_driver_side.ui.Driver_View;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import com.panda.student_map_driver_side.R;
import okhttp3.Route;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.Routese)
    RecyclerView recyclerView;
    @BindView(R.id.mCheckBox)
    CheckBox checkBox;
    CompositeDisposable disposable;
    Ordersadapter adapter;
    private static final String mTAg = "MainActivity";
    private static String SelectedRouteID = "101";
    ArrayList<Route_Module> Routes = new ArrayList<>();
    int SelectedPos = 0;
    private static final String Navigation = "Navigation";
    private static final String Lines = "Lines";
    private static final String State = "State";
    private static final String Active = "active";
    private static final String DisActive = "notctive";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ecoo);
        ButterKnife.bind(this);
        disposable = new CompositeDisposable();
        initRv();
        getData();
    }

    private void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Lines);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    Routes.add((Route_Module) post.getValue(Route_Module.class));
                }
                adapter.replaceData(Routes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRv() {
        adapter = new Ordersadapter(new ArrayList<>(), pos -> {
            SelectedPos = pos;
            recyclerView.setEnabled(false);
            disposable.add(io.reactivex.Observable.timer(150, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s ->
                    {
                        SelectedRouteID = String.valueOf((Routes.get(SelectedPos)).getLineId());
                        Intent intent = new Intent(MainActivity.this, Driver_View.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("Mock", checkBox.isChecked());
                        bundle.putParcelable("RouteModule", Routes.get(SelectedPos));
                        intent.putExtras(bundle);
                        activeRoute(intent);

                    }, e ->
                    {
                        e.printStackTrace();
                    })
            );

        });

           recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        recyclerView.getItemAnimator().setAddDuration(800);
        recyclerView.getItemAnimator().setRemoveDuration(500);
        recyclerView.getItemAnimator().setMoveDuration(500);
        recyclerView.getItemAnimator().setChangeDuration(500);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposable.clear();
    }

    private void activeRoute(@NotNull final Intent intent) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Navigation);
        myRef.child(SelectedRouteID).child(State).setValue(Active).addOnSuccessListener(aVoid ->
        {
            Timber.tag("FireBaseInterACtion").v("Route is Active");
            startActivity(intent);
        });
    }

}
