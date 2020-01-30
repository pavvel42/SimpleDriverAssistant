package com.example.simpledriverassistant.Beans;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class User {

    private String email;
    private String uid;
    private String name;
    private Boolean online;
    private LocationUser locationUser;
    private int like;
    private int dislike;
    private Double rating;
    private final String TAG = User.class.getSimpleName();
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;

    public User() {
    }

    public User(String email) {
        this.email = email;
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

    public LocationUser getLocationUser() {
        return locationUser;
    }

    public void setLocationUser(LocationUser locationUser) {
        this.locationUser = locationUser;
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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void userToString() {
        Log.d(TAG, "getEmail " + getEmail() + " getName " + getName() + " getUid " + getUid() + " getLike " + getLike() + " getDislike " + getDislike() + " getRating " + getRating() + " getOnline " + getOnline() + getLocationUser());
    }

    public void userUpdate() {
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

    public void userDownloadOnes() {
        documentReference = db.collection("users").document(getEmail());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User userDocument = documentSnapshot.toObject(User.class);
                    setLike(userDocument.getLike());
                    setDislike(userDocument.getDislike());
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
