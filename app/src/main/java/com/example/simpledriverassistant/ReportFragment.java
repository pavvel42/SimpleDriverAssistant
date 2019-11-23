package com.example.simpledriverassistant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ReportFragment extends Fragment {

    private static final String TAG = ReportFragment.class.getSimpleName();
    private ArrayList<Report> exampleList;
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("report");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_report, container, false);
        exampleList = new ArrayList<>();
        loadReports();
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

    private void loadReports() {
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, "Collection" + document.getId() + " => " + document.getData());
                        Report report = document.toObject(Report.class);
                        if(report.getEmail().equals(user_google_information.getEmail())){
                            exampleList.add(new Report(setImageInReportFragment(report.getAction()), report.getTime(), report.getLatitude(), report.getLongitude()));
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: " + task.getException());
                }
            }
        });
    }

    private int setImageInReportFragment(String action) {
        switch (action) {
            case "carAccident": {
                return R.drawable.ic_car_crash;
            }
            case "speedCamera": {
                return R.drawable.ic_speed_camera;
            }
            case "roadworks": {
                return R.drawable.ic_traffic_cone;
            }
            case "roadsideInspection": {
                return R.drawable.ic_warning;
            }
            default: {
                return R.drawable.ic_info;
            }
        }
    }
}
