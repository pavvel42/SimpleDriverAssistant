package com.example.simpledriverassistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReportFragment extends Fragment {

    private static final String TAG = ReportFragment.class.getSimpleName();
    private ArrayList<Report> exampleList;
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_report, container, false);

        initVariables(mainView);
        actionSetOnClickListener();

        exampleList = new ArrayList<>();
        exampleList.add(new Report(R.drawable.ic_car_crash, Long.valueOf(543534),546546.534,5345345.543));
        exampleList.add(new Report(R.drawable.ic_speed_radar, Long.valueOf(543534),546546.534,5345345.543));
        exampleList.add(new Report(R.drawable.ic_car_crash, Long.valueOf(543534),546546.534,5345345.543));
        exampleList.add(new Report(R.drawable.ic_speed_radar, Long.valueOf(543534),546546.534,5345345.543));
        exampleList.add(new Report(R.drawable.ic_car_crash, Long.valueOf(543534),546546.534,5345345.543));
        exampleList.add(new Report(R.drawable.ic_speed_radar, Long.valueOf(543534),546546.534,5345345.543));

        bulidRecycleView(mainView);

        return mainView;
    }

    public void bulidRecycleView(View mainView) {
        mRecyclerView = mainView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new Adapter(exampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(), "Position: " + exampleList.get(position).getAction(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }

    public void removeItem(int position) {
        exampleList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }


    /*Inicjowanie zmiennych*/
    private void initVariables(View mainView) {

    }

    private void actionSetOnClickListener() {

    }
}
