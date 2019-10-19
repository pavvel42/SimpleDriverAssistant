package com.example.simpledriverassistant;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bsk.floatingbubblelib.FloatingBubbleConfig;
import com.bsk.floatingbubblelib.FloatingBubbleService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static com.example.simpledriverassistant.MainActivity.floatingActionButton;
import static com.example.simpledriverassistant.MainActivity.user;
import static com.example.simpledriverassistant.NotificationService.CHANNEL_ID;
import static com.example.simpledriverassistant.R.layout.notification_view;

public class FloatingService extends FloatingBubbleService implements LocationListener {

    private static final String TAG = FloatingService.class.getSimpleName();
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("users");
    private DocumentReference documentReference;
    private LocationManager locationManager;
    private LocationListener locationListener;
    View buttonTrafficCone, buttonCarCrash, buttonInspection, buttonSpeedCamera, like, dislike, hide, radius, icon;

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Dane z GPS: " + location.getLatitude() + " " + location.getLongitude());
        user.setLatitude(location.getLatitude());
        user.setLongitude(location.getLongitude());
        user.userUpdate();
        Log.d(TAG, getString(R.string.firebase_upload));
        //userDownload();
    }

    protected void userDownload() {
        Log.d(TAG, getString(R.string.firebase_download));
        documentReference = db.document("users/" + user_google_information.getEmail());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()) {
                    User userDocument = documentSnapshot.toObject(User.class);
                    Toast.makeText(getApplicationContext(), "Dane pobrane", Toast.LENGTH_LONG).show();
                    Log.d(TAG, getString(R.string.firebase_download));
                }
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        onDestroy();
    }

    @SuppressLint("MissingPermission")
    private void tracking() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, this /*locationListener*/);
    }

    @Override
    protected void setTouchListener() {
        super.setTouchListener();
        initVariables();
        onViewDisplay();
        setState(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        floatingActionButton.setVisibility(View.VISIBLE);
        user.setOnline(false);
        user.userUpdate();
        Log.d(TAG, getString(R.string.firebase_upload));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name)).setContentText(input).setSmallIcon(R.drawable.bubble_default_icon).setContentIntent(pendingIntent).build();
        startForeground(1, notification);
        tracking();
        return super.onStartCommand(intent, flags, Service.START_NOT_STICKY);
    }

    @Override
    protected FloatingBubbleConfig getConfig() {
        Context context = getApplicationContext();
        return new FloatingBubbleConfig.Builder()
                // Set the drawable for the bubble
                .bubbleIcon(ContextCompat.getDrawable(context, R.mipmap.ic_icon_car_round))

                // Set the drawable for the remove bubble
                .removeBubbleIcon(ContextCompat.getDrawable(context, R.drawable.close_default_icon))

                // Set the size of the bubble in dp
                .bubbleIconDp(64)

                // Set the size of the remove bubble in dp
                .removeBubbleIconDp(64)

                // Set the padding of the view from the boundary
                .paddingDp(4)

                // Set the radius of the border of the expandable view
                .borderRadiusDp(4)

                // Does the bubble attract towards the walls
                .physicsEnabled(true)

                // The color of background of the layout
                .expandableColor(Color.WHITE)

                // The color of the triangular layout
                .triangleColor(Color.WHITE)

                // Horizontal gravity of the bubble when expanded
                .gravity(Gravity.END)

                // The view which is visible in the expanded view
                .expandableView(getInflater().inflate(notification_view, null))

                // Set the alpha value for the remove bubble icon
                .removeBubbleAlpha(0.75f)

                // Building
                .build();
    }

    public void initVariables() {
        buttonTrafficCone = expandableView.findViewById(R.id.traffic_cone);
        buttonCarCrash = expandableView.findViewById(R.id.car_crash);
        buttonInspection = expandableView.findViewById(R.id.inspection);
        buttonSpeedCamera = expandableView.findViewById(R.id.speed_camera);
        like = expandableView.findViewById(R.id.like);
        dislike = expandableView.findViewById(R.id.dislike);
        radius = expandableView.findViewById(R.id.radius);
        icon = expandableView.findViewById(R.id.icon);
        hide = expandableView.findViewById(R.id.hide);
    }

    public void onViewDisplay() {
        buttonTrafficCone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideUp();
            }
        });
        buttonCarCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideUp();
            }
        });
        buttonInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideUp();
            }
        });
        buttonSpeedCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideUp();
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDown();
            }
        });
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDown();
            }
        });
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(false);
            }
        });
    }

    public void hideUp() {
        buttonTrafficCone.setVisibility(View.GONE);
        buttonCarCrash.setVisibility(View.GONE);
        buttonInspection.setVisibility(View.GONE);
        buttonSpeedCamera.setVisibility(View.GONE);
        radius.setVisibility(View.VISIBLE);
        icon.setVisibility(View.VISIBLE);
        like.setVisibility(View.VISIBLE);
        dislike.setVisibility(View.VISIBLE);
    }

    public void hideDown() {
        buttonTrafficCone.setVisibility(View.VISIBLE);
        buttonCarCrash.setVisibility(View.VISIBLE);
        buttonInspection.setVisibility(View.VISIBLE);
        buttonSpeedCamera.setVisibility(View.VISIBLE);
        radius.setVisibility(View.GONE);
        icon.setVisibility(View.GONE);
        like.setVisibility(View.GONE);
        dislike.setVisibility(View.GONE);
    }
}
