package com.example.simpledriverassistant;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.bsk.floatingbubblelib.FloatingBubbleConfig;
import com.bsk.floatingbubblelib.FloatingBubbleService;
import com.example.simpledriverassistant.Beans.Actions;
import com.example.simpledriverassistant.Beans.Report;
import com.example.simpledriverassistant.Beans.Report4User;
import com.example.simpledriverassistant.Beans.User;
import com.example.simpledriverassistant.Support.CurrentTime;
import com.example.simpledriverassistant.Support.NetworkStateReceiver;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.reflect.TypeToken;
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
import com.google.gson.Gson;


import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.simpledriverassistant.MainActivity.floatingActionButton;
import static com.example.simpledriverassistant.MainActivity.locationUser;
import static com.example.simpledriverassistant.MainActivity.user;
import static com.example.simpledriverassistant.Support.NotificationService.CHANNEL_ID;
import static com.example.simpledriverassistant.R.layout.notification_view;

public class FloatingService extends FloatingBubbleService implements LocationListener {

    private final String TAG = FloatingService.class.getSimpleName();
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
    private TextToSpeech mTTS;
    View buttonTrafficCone, buttonCarCrash, buttonInspection, buttonSpeedCamera, like, dislike, hide,
            radiusCV, iconCV, user_ratingCV, emailCV, skip;
    TextView radius, user_rating, email;
    ImageView iconAction4Report;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name)).setContentText(input).setSmallIcon(R.drawable.ic_icon_car_forground).setContentIntent(pendingIntent).build();
        startForeground(1, notification);
        tracking();
        textToSpeechListener();
        return super.onStartCommand(intent, flags, Service.START_NOT_STICKY);
    }

    @Override
    protected void setTouchListener() {
        super.setTouchListener();
        initVariables();
        onViewDisplay();
        //setState(true);
        loadData();
        report4UserListener();
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

    @Override
    public void onDestroy() {
        saveData();
        floatingActionButton.setVisibility(View.VISIBLE);
        user.setOnline(false);
        locationUser.setLatitude(0.0);
        locationUser.setLongitude(0.0);
        locationUser.userUpdate();
        user.userUpdate();
        deleteReport4User();
        locationManager.removeUpdates(this);
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        Log.d(TAG, getString(R.string.firebase_upload));
        super.onDestroy();
    }

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 20, this);
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
                    } else if (reportsID.contains(documentSnapshot.getString("reportid")) == true) {
                        return;
                    } else if (reportsID.contains(documentSnapshot.getString("reportid")) == false) {
                        reportsID.add(documentSnapshot.getString("reportid"));
                        createReport4User(documentSnapshot);
                    }
                }
            }
        });
    }

    private void createReport4User(DocumentSnapshot documentSnapshot) {
        report4User = documentSnapshot.toObject(Report4User.class);
        radius.setText(round(report4User.getDistance(), 1) + "m");
        user_rating.setText(round(report4User.getRating(), 1) + "");
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
        report4User.report4UserToString();
        userBroadcaster = new User(report4User.getBroadcaster());
        userBroadcaster.userDownloadOnes();
        speakToUser(report4User.getAction(), report4User.getDistance());
        hideUp();
        try {
            setState(true);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void speakToUser(String action, Double meters) {
        switch (action) {
            case "carAccident": {
                action = getString(R.string.tts_carAccident);
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
        }
        if (meters != null) {
            mTTS.speak(action + Math.round(meters) + getString(R.string.tts_meters), TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            mTTS.speak(getString(R.string.send_report) + action, TextToSpeech.QUEUE_FLUSH, null, null);
        }

    }

    private void textToSpeechListener() {
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");

                        //mTTS.setLanguage(Locale.getDefault());
                    }
                } else {
                    Log.e(TAG + " TTS", "Initialization failed");
                }
            }
        });

        float pitch = 1.1f;
        float speed = 1;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);
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
            //Toast.makeText(getApplicationContext(), getString(R.string.send_report) + " " + action, Toast.LENGTH_LONG).show();
            speakToUser(action, null);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.lost_gps), Toast.LENGTH_LONG).show();
        }
    }

    private void locationUserUpdate(Double latitude, Double longitude) {
        documentReference = db.document("users/" + user_google_information.getEmail() + "/locationUser/" + user_google_information.getEmail());
        documentReference.update("latitude", latitude);
        documentReference.update("longitude", longitude);
    }

    private void uploadRateUser(int state) {
        documentReference = db.collection("users").document(userBroadcaster.getEmail());
        switch (state) {
            case 1: {
                documentReference.update("like", userBroadcaster.getLike() + 1);
                deleteReport4User();
                Toast.makeText(getApplicationContext(), getString(R.string.thx_for_feedback), Toast.LENGTH_LONG).show();
                break;
            }
            case -1: {
                documentReference.update("dislike", userBroadcaster.getDislike() + 1);
                deleteReport4User();
                Toast.makeText(getApplicationContext(), getString(R.string.thx_for_feedback), Toast.LENGTH_LONG).show();
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
        hideDown();
        setState(false);
    }

    private void currentDistance(Double stLati, Double stLong, Double endLati, Double endLong) {
        float[] tablica = new float[2];
        location.distanceBetween(stLati, stLong, endLati, endLong, tablica);
        for (int i = 0; i < tablica.length; i++) {
            Log.d(TAG, "Element tablicy " + i + " to " + tablica[i]);
        }
        report4User.setDistance(Double.valueOf(tablica[0]));
        radius.setText(Math.round(report4User.getDistance()) + "m");
        if (tablica[0] > 999) {
            uploadRateUser(999);
            Log.d(TAG, "Dystans >999m " + tablica[0]);
        }
        tablica = null;
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        if (!reportsID.isEmpty()) {
            String lastReportID = reportsID.get(reportsID.size() - 1);
            reportsID.clear();
            reportsID.add(lastReportID);
        }
        String json = gson.toJson(reportsID);
        editor.putString("task list", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        reportsID = gson.fromJson(json, type);

        if (reportsID == null) {
            reportsID = new ArrayList<>();
        }
    }

    private void hideUp() {
        buttonTrafficCone.setVisibility(View.GONE);
        buttonCarCrash.setVisibility(View.GONE);
        buttonInspection.setVisibility(View.GONE);
        buttonSpeedCamera.setVisibility(View.GONE);
        radiusCV.setVisibility(View.VISIBLE);
        iconCV.setVisibility(View.VISIBLE);
        user_ratingCV.setVisibility(View.VISIBLE);
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
        user_ratingCV.setVisibility(View.GONE);
        emailCV.setVisibility(View.GONE);
        like.setVisibility(View.GONE);
        dislike.setVisibility(View.GONE);
        skip.setVisibility(View.GONE);
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
        user_ratingCV = expandableView.findViewById(R.id.user_ratingCV);
        user_ratingCV.setVisibility(View.GONE);
        emailCV = expandableView.findViewById(R.id.emailCV);
        emailCV.setVisibility(View.GONE);
        skip = expandableView.findViewById(R.id.skip);
        skip.setVisibility(View.GONE);
        hide = expandableView.findViewById(R.id.hide);
        radius = expandableView.findViewById(R.id.radius);
        user_rating = expandableView.findViewById(R.id.user_rating);
        email = expandableView.findViewById(R.id.email);
        iconAction4Report = expandableView.findViewById(R.id.iconAction4Report);
        iconAction4Report.setImageResource(R.drawable.ic_like);
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

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //test
    private Double tooLong(Double distance) {
        if (distance > 500) {
            return Double.valueOf(500);
        } else {
            return distance;
        }
    }

    private void killApp() {
        Intent myIntent = new Intent(FloatingService.this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle myKillerBundle = new Bundle();
        myKillerBundle.putInt("kill", 1);
        myIntent.putExtras(myKillerBundle);
        getApplication().startActivity(myIntent);
    }

    //test wywolania function HTTPS
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
}