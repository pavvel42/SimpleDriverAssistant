package com.example.simpledriverassistant;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bsk.floatingbubblelib.FloatingBubbleConfig;
import com.bsk.floatingbubblelib.FloatingBubbleService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;


import java.util.ArrayList;

import static com.example.simpledriverassistant.MainActivity.floatingActionButton;
import static com.example.simpledriverassistant.MainActivity.locationUser;
import static com.example.simpledriverassistant.MainActivity.user;
import static com.example.simpledriverassistant.NotificationService.CHANNEL_ID;
import static com.example.simpledriverassistant.R.layout.notification_view;

public class FloatingService extends FloatingBubbleService implements LocationListener {

    private static final String TAG = FloatingService.class.getSimpleName();
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("users");
    private DocumentReference documentReference;
    private FirebaseFunctions mFunctions;
    private Location location;
    private LocationManager locationManager;
    private LocationListener locationListener;
    protected static Actions actions = new Actions();
    private NetworkStateReceiver networkStateReceiver = new NetworkStateReceiver();
    private Report report = new Report();
    private CurrentTime currentTime = new CurrentTime();
    private Report4User report4User;
    private User userBroadcaster;
    private ArrayList<String> reportsID = new ArrayList<String>();
    View buttonTrafficCone, buttonCarCrash, buttonInspection, buttonSpeedCamera, like, dislike, hide,
            radiusCV, iconCV, user_raitingCV, emailCV, skip;
    TextView radius, user_raiting, email;
    ImageView iconAction4Report;


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Dane z GPS: " + location.getLatitude() + " " + location.getLongitude());
        report.setLatitude(location.getLatitude());
        report.setLongitude(location.getLongitude());
        //Toast.makeText(getApplicationContext(), "Dane z GPS: " + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        if (locationUser != null) {
            locationUser.setLatitude(location.getLatitude());
            locationUser.setLongitude(location.getLongitude());
            locationUser.userUpdate();
        } else {
            locationUserUpdate(location.getLatitude(), location.getLongitude());
        }
        if (report4User != null) {
            currentDistance(location.getLatitude(), location.getLongitude(), report4User.getLatitudeReport(), report4User.getLongitudeReport());
        }
        Log.d(TAG, getString(R.string.firebase_upload));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent serviceIntent = new Intent(this, FloatingService.class);
        stopService(serviceIntent);
    }

    @SuppressLint("MissingPermission")
    private void tracking() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 20, this /*locationListener*/);
    }

    @Override
    protected void setTouchListener() {
        super.setTouchListener();
        initVariables();
        onViewDisplay();
        //setState(true);
        report4UserListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        floatingActionButton.setVisibility(View.VISIBLE);
        user.setOnline(false);
        locationUser.setLatitude(0.0);
        locationUser.setLongitude(0.0);
        locationUser.userUpdate();
        user.userUpdate();
        deleteReport4User();
        locationManager.removeUpdates(this);
        Log.d(TAG, getString(R.string.firebase_upload));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name)).setContentText(input).setSmallIcon(R.drawable.ic_icon_car_forground).setContentIntent(pendingIntent).build();
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
                .bubbleIconDp(75)

                // Set the size of the remove bubble in dp
                .removeBubbleIconDp(75)

                // Set the padding of the view from the boundary
                .paddingDp(15)

                // Set the radius of the border of the expandable view
                .borderRadiusDp(8)

                // Does the bubble attract towards the walls
                .physicsEnabled(true)

                // The color of background of the layout
                .expandableColor(Color.WHITE)

                // The color of the triangular layout
                .triangleColor(getColor(R.color.primary))

                // Horizontal gravity of the bubble when expanded
                .gravity(Gravity.END)

                // The view which is visible in the expanded view
                .expandableView(getInflater().inflate(notification_view, null))

                // Set the alpha value for the remove bubble icon
                .removeBubbleAlpha(0.75f)

                // Building
                .build();
    }

    private void initVariables() {
        buttonTrafficCone = expandableView.findViewById(R.id.traffic_cone);
        buttonCarCrash = expandableView.findViewById(R.id.car_crash);
        buttonInspection = expandableView.findViewById(R.id.inspection);
        buttonSpeedCamera = expandableView.findViewById(R.id.speed_camera);
        like = expandableView.findViewById(R.id.like);
        like.setVisibility(View.GONE);
        dislike = expandableView.findViewById(R.id.dislike);
        dislike.setVisibility(View.GONE);
        radiusCV = expandableView.findViewById(R.id.radiusCV);
        radiusCV.setVisibility(View.GONE);
        iconCV = expandableView.findViewById(R.id.iconCV);
        iconCV.setVisibility(View.GONE);
        user_raitingCV = expandableView.findViewById(R.id.user_raitingCV);
        user_raitingCV.setVisibility(View.GONE);
        emailCV = expandableView.findViewById(R.id.emailCV);
        emailCV.setVisibility(View.GONE);
        hide = expandableView.findViewById(R.id.hide);
        skip = expandableView.findViewById(R.id.skip);
        skip.setVisibility(View.GONE);
        radius = expandableView.findViewById(R.id.radius);
        user_raiting = expandableView.findViewById(R.id.user_raiting);
        email = expandableView.findViewById(R.id.email);
        iconAction4Report = expandableView.findViewById(R.id.iconAction4Report);
        iconAction4Report.setImageResource(R.drawable.ic_like);
    }

    private void functionHttps() {
        mFunctions = FirebaseFunctions.getInstance();

        FirebaseFunctions.getInstance() // Optional region: .getInstance("europe-west1")
                .getHttpsCallable("onReportCreate")
                .call()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Błąd przy wywołaniu: " + e);
                        Toast.makeText(getApplicationContext(), "Błąd przy wywołaniu: " + e, Toast.LENGTH_LONG).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        Log.d(TAG, "Poprawnie wywołana funkcja");
                        Toast.makeText(getApplicationContext(), "Poprawnie wywołana funkcja", Toast.LENGTH_LONG).show();
                    }
                });

    }

    public void onViewDisplay() {
        buttonTrafficCone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendPromise();
                chooseActionSendReport(actions.getRoadworks());
                //hideUp();
            }
        });
        buttonCarCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseActionSendReport(actions.getCarAccident());
                //hideUp();
            }
        });
        buttonInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseActionSendReport(actions.getRoadsideInspection());
                //hideUp();
            }
        });
        buttonSpeedCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseActionSendReport(actions.getSpeedCamera());
                //hideUp();
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadRateUser(1);
                //hideDown();
            }
        });
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadRateUser(-1);
                //hideDown();
            }
        });
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(false);
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadRateUser(0);
                //setState(false);
            }
        });
    }

    private void chooseActionSendReport(String action) {
        if (networkStateReceiver.haveNetworkConnection(this) == false) {
            Toast.makeText(getApplicationContext(), getString(R.string.pls_turn_on_network_connection), Toast.LENGTH_LONG).show();
            return;
        }
        if (report.coordinatesNotNull() == true) {
            report.setAction(action);
            report.setTime(currentTime.milliseconds());
            report.reportUpdate();
            Log.d(TAG, getString(R.string.send_report));
            setState(false);
            Toast.makeText(getApplicationContext(), getString(R.string.send_report) + " " + action, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.lost_gps), Toast.LENGTH_LONG).show();
        }
    }

    private void locationUserUpdate(Double latitude, Double longitude) {
        documentReference = db.document("users/" + user_google_information.getEmail() + "/locationUser/" + user_google_information.getEmail());
        documentReference.update("latitude", latitude);
        documentReference.update("longitude", longitude);
    }

    /*Nasłuchiwanie kolekcji*/
    private void report4UserListener() {
        documentReference = db.document("users/" + user_google_information.getEmail() + "/report4user/currentReport");
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if ((e) != null) {
                    return;
                }
                if (documentSnapshot.exists()) {
                    if (reportsID.isEmpty()) {
                        reportsID.add(documentSnapshot.getString("reportid"));
                        createReport4User(documentSnapshot);
                    } else if (reportsID.contains(documentSnapshot.getString("reportid")) == false) {
                        reportsID.add(documentSnapshot.getString("reportid"));
                        createReport4User(documentSnapshot);
                    }
//                    else if (reportsID.contains(documentSnapshot.getString("reportid")) == true){
//                        createReport4User(documentSnapshot);
//                    }
                }
            }
        });
    }

    private void createReport4User(DocumentSnapshot documentSnapshot) {
        report4User = documentSnapshot.toObject(Report4User.class);
        radius.setText(Math.round(report4User.getDistance()) + "m");
        user_raiting.setText(String.valueOf(report4User.getRaiting()));
        email.setText(report4User.getBroadcaster());
        switch (report4User.getAction()) {
            case "carAccident": {
                iconAction4Report.setImageResource(R.drawable.ic_car_crash);
                break;
            }
            case "speedCamera": {
                iconAction4Report.setImageResource(R.drawable.ic_speed_camera);
                break;
            }
            case "roadworks": {
                iconAction4Report.setImageResource(R.drawable.ic_traffic_cone);
                break;
            }
            case "roadsideInspection": {
                iconAction4Report.setImageResource(R.drawable.ic_warning);
                break;
            }
            default: {
                iconAction4Report.setImageResource(R.drawable.ic_info);
            }
        }
        setState(true);
        hideUp();
        report4User.report4UserToString();
        userBroadcaster = new User(report4User.getBroadcaster());
        userBroadcaster.userDownloadOnes();
    }

    private void uploadRateUser(int state) {
        documentReference = db.collection("users").document(userBroadcaster.getEmail());
        switch (state) {
            case 1: {
                documentReference.update("like", userBroadcaster.getLike() + 1);
                Toast.makeText(getApplicationContext(), getString(R.string.thx_for_review), Toast.LENGTH_LONG).show();
                deleteReport4User();
                break;
            }
            case -1: {
                documentReference.update("dislike", userBroadcaster.getDislike() + 1);
                Toast.makeText(getApplicationContext(), getString(R.string.thx_for_review), Toast.LENGTH_LONG).show();
                deleteReport4User();
                break;
            }
            default: {
                deleteReport4User();
            }
        }
        userBroadcaster = null;
    }

    private void deleteReport4User() {
        documentReference = db.collection("users").document(user_google_information.getEmail())
                .collection("report4user").document("currentReport");
        documentReference.delete();
        report4User = null;
        setState(false);
        hideDown();
    }


    private void currentDistance(Double stLati, Double stLong, Double endLati, Double endLong) {
        float[] tablica = new float[2];
        location.distanceBetween(stLati, stLong, endLati, endLong, tablica);
        for (int i = 0; i < tablica.length; i++) {
            Log.d(TAG, "Element tablicy " + i + " to " + tablica[i]);
        }
        report4User.setDistance(Double.valueOf(tablica[0]));
        radius.setText(Math.round(report4User.getDistance()) + "m");
        tablica = null;
    }

    private void hideUp() {
        buttonTrafficCone.setVisibility(View.GONE);
        buttonCarCrash.setVisibility(View.GONE);
        buttonInspection.setVisibility(View.GONE);
        buttonSpeedCamera.setVisibility(View.GONE);
        radiusCV.setVisibility(View.VISIBLE);
        iconCV.setVisibility(View.VISIBLE);
        user_raitingCV.setVisibility(View.VISIBLE);
        emailCV.setVisibility(View.VISIBLE);
        like.setVisibility(View.VISIBLE);
        dislike.setVisibility(View.VISIBLE);
        skip.setVisibility(View.VISIBLE);
    }

    private void hideDown() {
        buttonTrafficCone.setVisibility(View.VISIBLE);
        buttonCarCrash.setVisibility(View.VISIBLE);
        buttonInspection.setVisibility(View.VISIBLE);
        buttonSpeedCamera.setVisibility(View.VISIBLE);
        radiusCV.setVisibility(View.GONE);
        iconCV.setVisibility(View.GONE);
        user_raitingCV.setVisibility(View.GONE);
        emailCV.setVisibility(View.GONE);
        like.setVisibility(View.GONE);
        dislike.setVisibility(View.GONE);
        skip.setVisibility(View.GONE);
    }
}