package com.example.simpledriverassistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ReportFragment extends Fragment {

    private static final String TAG = ReportFragment.class.getSimpleName();
    protected View card_view_report;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_report, container, false);

        initVariables(mainView);
        actionSetOnClickListener();

        return mainView;
    }

    /*Inicjowanie zmiennych*/
    private void initVariables(View mainView) {
        card_view_report = mainView.findViewById(R.id.btn_report);
    }

    private void actionSetOnClickListener() {
        card_view_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Report 1", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
