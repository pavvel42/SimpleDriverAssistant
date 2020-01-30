package com.example.simpledriverassistant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.simpledriverassistant.Beans.User;
import com.example.simpledriverassistant.Support.CurrentTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static com.example.simpledriverassistant.MainActivity.locationUser;
import static com.example.simpledriverassistant.MainActivity.user;

public class ProfileFragment extends Fragment {

    private final String TAG = ProfileFragment.class.getSimpleName();
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("users");
    private DocumentReference documentReference = db.document("users/" + user_google_information.getEmail());
    protected View card_view_profile;
    private TextView user_rating, user_like, user_dislike, user_longitude, user_latitude;
    private CurrentTime currentTime = new CurrentTime();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_profile, container, false);

        initVariables(mainView);
        //actionSetOnClickListener();
        setValueInCardview();

        return mainView;
    }

    /*Inicjowanie zmiennych*/
    private void initVariables(View mainView) {
        card_view_profile = mainView.findViewById(R.id.btn_profile);
        user_rating = mainView.findViewById(R.id.user_rating);
        user_like = mainView.findViewById(R.id.user_like);
        user_dislike = mainView.findViewById(R.id.user_unlike);
        user_longitude = mainView.findViewById(R.id.user_longitude);
        user_latitude = mainView.findViewById(R.id.user_latitude);
    }

    public void setValueInCardview() {
        user_rating.setText("Rating: " + String.format("%.2g%n", user.getRating()));
        user_like.setText("Likes: " + user.getLike());
        user_dislike.setText("Dislikes: " + user.getDislike());
        user_longitude.setText("Longitude: " + locationUser.getLongitude());
        user_latitude.setText("Lalitude: " + locationUser.getLatitude());
    }

    private void actionSetOnClickListener() {
        card_view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_longitude.getVisibility() == View.VISIBLE) {
                    user_longitude.setVisibility(View.INVISIBLE);
                    user_latitude.setVisibility(View.INVISIBLE);
                } else {
                    user_longitude.setVisibility(View.VISIBLE);
                    user_latitude.setVisibility(View.VISIBLE);
                    setValueInCardview();
                    Toast.makeText(getContext(), "Dev Mode", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        userDownloadListener();
    }

    private void userDownloadListener() {
        Log.d(TAG, getString(R.string.firebase_download));
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()) {
                    User userDocument = documentSnapshot.toObject(User.class);
                    rating4user(userDocument);
                    user.userToString();
                    user.toString();
                    setValueInCardview();
                } else {
                    user.setEmail(user_google_information.getEmail());
                    user.setName(user_google_information.getDisplayName());
                    user.setUid(user_google_information.getUid());
                    user.setOnline(false);
                    user.userUpdate();
                    locationUser.userUpdate();
                    Log.d(TAG, getString(R.string.firebase_upload));
                }
            }
        });
    }

    private void rating4user(User userDocument) {
        user.setLike(userDocument.getLike());
        user.setDislike(userDocument.getDislike());
        if (userDocument.getLike() == 0 && userDocument.getDislike() == 0) {
            user.setRating(0.0);
        } else if (userDocument.getLike() >= 1 && userDocument.getDislike() == 0) {
            user.setRating(1.0);
        } else if (userDocument.getLike() == 0 && userDocument.getDislike() >= 1) {
            user.setRating(0.0);
        } else if (userDocument.getLike() >= 1 && userDocument.getDislike() == 1) {
            user.setRating(Double.valueOf(userDocument.getLike()) / Double.valueOf(userDocument.getDislike()));
        } else if (userDocument.getLike() >= 1 && userDocument.getDislike() >= 1) {
            user.setRating(Double.valueOf(userDocument.getLike()) / Double.valueOf(userDocument.getDislike()));
        }
    }
}
