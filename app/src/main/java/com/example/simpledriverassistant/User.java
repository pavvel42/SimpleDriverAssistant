package com.example.simpledriverassistant;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import static com.example.simpledriverassistant.MainActivity.user;

public class User {

    private String email;
    private String uid;
    private String name;
    private Double latitude;
    private Double longitude;
    private Boolean online;
    private int like;
    private int dislike;
    private Double raiting;
    private static final String TAG = User.class.getSimpleName();
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("users");
    private DocumentReference documentReference;

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int unlike) {
        this.dislike = unlike;
    }

    public Double getRaiting() {
        return raiting;
    }

    public void setRaiting(Double raiting) {
        this.raiting = raiting;
    }

    protected void userToString() {
        Log.d(TAG, "getEmail " + getEmail() + " getName " + getName() + " getUid " + getUid() + " getLatitude " + getLatitude() + " getLongitude " + getLongitude() + " getOnline " + getOnline() + " getLike " + getLike() + " getDislike " + getDislike() + " getRaiting " + getRaiting());
    }

    protected void userUpdate() {
        db.collection("users").document(getEmail()).set(this, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Dane zostały zapisane");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Błąd w zapisnie danych: " + e.toString());
                    }
                });
        userToString();
    }

    private void userDownloadOnes() {
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User userDocument = documentSnapshot.toObject(User.class);
                    user.setRaiting(userDocument.getRaiting());
                    user.setLike(userDocument.getLike());
                    user.setDislike(userDocument.getDislike());
                    user.setLongitude(userDocument.getLongitude());
                    user.setLatitude(userDocument.getLatitude());
                    user.userToString();
                    user.toString();
                    //refreshFragment();
                } else {
                    user.setEmail(user_google_information.getEmail());
                    user.setName(user_google_information.getDisplayName());
                    user.setUid(user_google_information.getUid());
                    user.setOnline(false);
                    user.userUpdate();
                    //Log.d(TAG, getString(R.string.firebase_upload));
                }
                Log.d(TAG, "Dane zostały zapisane");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Błąd w zapisnie danych: " + e.toString());
            }
        });
    }
}
