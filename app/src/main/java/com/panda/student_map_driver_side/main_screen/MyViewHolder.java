package com.panda.student_map_driver_side.main_screen;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import carbon.widget.RelativeLayout;
import com.panda.student_map_driver_side.R;

public class MyViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.Whole_layout)
    RelativeLayout txtv3;
    @BindView(R.id.RouteName)
    carbon.widget.TextView cardView;



    public MyViewHolder(View itemView)
    {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
