package com.example.simpledriverassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bsk.floatingbubblelib.FloatingBubblePermissions;
import com.example.simpledriverassistant.Beans.LocationUser;
import com.example.simpledriverassistant.Beans.User;
import com.example.simpledriverassistant.Support.InfoDialog;
import com.example.simpledriverassistant.Support.NetworkStateReceiver;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    private final String TAG = MainActivity.class.getSimpleName();
    private final String SERVICE_NAME = "com.example.simpledriverassistant.FloatingService";
    private GoogleSignInClient mGoogleSignInClient;
    protected static LocationUser locationUser = new LocationUser();
    public static User user = new User();
    private DrawerLayout drawer;
    private View headerView;
    protected static View floatingActionButton; //FloatingActionButton
    private ImageView avatar;
    private TextView name, email;
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("users");
    private DocumentReference documentReference;
    private LocationManager locationManager;
    private NetworkStateReceiver networkStateReceiver = new NetworkStateReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (this.getIntent().getExtras() != null) {
            if (this.getIntent().getExtras().getInt("kill") == 1) {
                finishAffinity();
                System.exit(0);
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildLayout(savedInstanceState);
        initVariables();
        actionSetOnClickListener();
    }

    /*Inicjowanie zmiennych*/
    private void initVariables() {
        floatingActionButton = findViewById(R.id.floating_button);
        if (isServiceRunning() == true) {
            //Toast.makeText(getApplicationContext(), "RUNNING", Toast.LENGTH_LONG).show();
            Log.d(TAG, "RUNNING");
            floatingActionButton.setVisibility(View.INVISIBLE);
        } else {
            Log.d(TAG, "NOT RUNNING");
        }
    }

    /*Budowanie Layoutu*/
    private void buildLayout(Bundle savedInstanceState) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        avatar = headerView.findViewById(R.id.avatar);
        name = headerView.findViewById(R.id.name);
        email = headerView.findViewById(R.id.email);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_profile);
        }

        if (user_google_information != null) {
            Log.d(TAG, user_google_information.getPhotoUrl().toString());
            Picasso.get().load(user_google_information.getPhotoUrl().toString()).into(avatar);
            name.setText(user_google_information.getDisplayName());
            email.setText(user_google_information.getEmail());
        }
    }

    /*odświeżenie fragmentu*/
    protected void refreshFragment() {
        Log.d(TAG, getString(R.string.refresh_fragment));
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(fragment);
        fragmentTransaction.attach(fragment);
        fragmentTransaction.commit();
    }

    /*Akcje Buttons*/
    private void actionSetOnClickListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//              Toast.makeText(getApplicationContext(), "SNACK BAR", Toast.LENGTH_LONG).show();
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (FloatingBubblePermissions.requiresPermission(MainActivity.this) == false) /*jesli przyznano permission*/ {
                    if (checkPerm() == true /* && locationManager.isLocationEnabled()*/) {
                        if (/*locationManager.isLocationEnabled() == false*/locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
                            Intent intent_action_location_source_settings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            InfoDialog dialog = new InfoDialog(getString(R.string.pls_turn_on_gps), intent_action_location_source_settings);
                            dialog.show(getSupportFragmentManager(), "intent_action_location_source_settings dialog");
                        } else if (networkStateReceiver.haveNetworkConnection(MainActivity.this) == false) {
                            Intent intent_action_network_operator_settings = new Intent(Settings.ACTION_SETTINGS);
                            InfoDialog dialog = new InfoDialog(getString(R.string.pls_turn_on_network_connection), intent_action_network_operator_settings);
                            dialog.show(getSupportFragmentManager(), "intent_action_network_operator_settings dialog");
                        } else {
                            startService();
                        }
                    }
                } else {
                    Intent intent_action_manage_overlay_permission = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + MainActivity.this.getPackageName()));
                    InfoDialog dialog = new InfoDialog(getString(R.string.app_need_permission), intent_action_manage_overlay_permission);
                    dialog.show(getSupportFragmentManager(), "intent_action_manage_overlay_permission dialog");
                    //FloatingBubblePermissions.startPermissionRequest(MainActivity.this);
                }
            }
        });
    }

    /*Ststus usera Online/Offline*/
    protected void userOnline(Boolean state) {
        user.setEmail(user_google_information.getEmail());
        user.setName(user_google_information.getDisplayName());
        user.setUid(user_google_information.getUid());
        user.setOnline(state);
        user.userUpdate();
        Log.d(TAG, getString(R.string.firebase_upload));
    }

    @Override
    public void onStart() {
        super.onStart();
        /*potrzebuje executora!? do działania w klasie user*/
//        documentReference = db.document("users/"+user_google_information.getEmail());
//        documentReference.addSnapshotListener((Executor) User.this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    return;
//                }
//                User userExample = documentSnapshot.toObject(User.class);
//                Double raitingEx = userExample.getRaiting();
//                Log.d(TAG,"Raiting: "+raitingEx);
//            }
//        });

        /*Nasłuchiwanie kolekcji*/
//        collectionReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
//                if (e != null) {
//                    return;
//                }
//
//                String data = "";
//
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                    User userExample = documentSnapshot.toObject(User.class);
//                    //userExample.setDocumentId(documentSnapshot.getId());
//
//                    Double raitingEx = userExample.getRaiting();
//                    Log.d(TAG,"Raiting: "+raitingEx);
//                }
//
//                //textViewData.setText(data);
//            }
//        });
    }

    /* Wylogowanie z Firebase*/
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        stopService();
                        finish();
                        startActivity(new Intent(MainActivity.this, SignIn.class));
                    }
                });
    }

    /*Nawigacja NavDrawer*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ReportFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_info:
                Toast.makeText(this, "Information", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                logout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*Start usługi*/
    public void startService() {
        Intent serviceIntent = new Intent(this, FloatingService.class);
        serviceIntent.putExtra("inputExtra", getString(R.string.click_to_return));
        ContextCompat.startForegroundService(this, serviceIntent);
        userOnline(true);
        floatingActionButton.setVisibility(View.INVISIBLE);
    }

    /*Stop usługi*/
    public void stopService() {
        //onDestroy(); //trzeba spawdzić czy usługa istnieje
        Intent serviceIntent = new Intent(this, FloatingService.class); //główna usługa
        stopService(serviceIntent);
    }

    private boolean isServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (SERVICE_NAME.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(123)
    private boolean checkPerm() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            return true;
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.app_need_permission), 123, perms);
            return false;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
//        InfoDialog exampleDialog = new InfoDialog(getString(R.string.thx_for_persmission));
//        exampleDialog.show(getSupportFragmentManager(), "example dialog");
        Toast.makeText(this, getString(R.string.thx_for_persmission), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}