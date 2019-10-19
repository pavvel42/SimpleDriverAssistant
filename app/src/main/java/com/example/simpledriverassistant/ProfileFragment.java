package com.example.simpledriverassistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.simpledriverassistant.MainActivity.user;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    protected View card_view_profile;
    private TextView user_raiting, user_like, user_dislike, user_longitude, user_latitude;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_profile, container, false);

        initVariables(mainView);
        actionSetOnClickListener();
        setValueInCardview();

        return mainView;
    }

    /*Inicjowanie zmiennych*/
    private void initVariables(View mainView) {
        card_view_profile = mainView.findViewById(R.id.btn_profile);
        user_raiting = mainView.findViewById(R.id.user_raiting);
        user_like = mainView.findViewById(R.id.user_like);
        user_dislike = mainView.findViewById(R.id.user_unlike);
        user_longitude = mainView.findViewById(R.id.user_longitude);
        user_latitude = mainView.findViewById(R.id.user_latitude);
    }

    public void setValueInCardview() {
        user_raiting.setText(getString(R.string.raiting) + user.getRaiting());
        user_like.setText(getString(R.string.like) + user.getLike());
        user_dislike.setText(getString(R.string.dislike) + user.getDislike());
        user_longitude.setText(getString(R.string.longitude) + user.getLongitude());
        user_latitude.setText(getString(R.string.latitude) + user.getLatitude());
    }

    private void actionSetOnClickListener() {
        card_view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Tryb Developera ;)", Toast.LENGTH_SHORT).show();
                if (user_longitude.getVisibility() == View.VISIBLE) {
                    user_longitude.setVisibility(View.GONE);
                    user_latitude.setVisibility(View.GONE);
                } else {
                    user_longitude.setVisibility(View.VISIBLE);
                    user_latitude.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
