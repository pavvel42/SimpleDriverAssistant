package com.example.simpledriverassistant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bsk.floatingbubblelib.FloatingBubblePermissions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleSignInClient mGoogleSignInClient;
    protected static User user = new User();
    private DrawerLayout drawer;
    private View headerView;
    protected static View floatingActionButton; //FloatingActionButton
    private ImageView avatar;
    private TextView name, email;
    private FirebaseUser user_google_information = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("users");
    private DocumentReference noteRef;
    private LocationManager locationManager;
    //private static final String KEY_TITLE = "raiting";
    //private static final String KEY_DESCRIPTION = "description";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVariables();
        buildLayout(savedInstanceState);
        actionSetOnClickListener();
    }

    /*Inicjowanie zmiennych*/
    private void initVariables() {
        floatingActionButton = findViewById(R.id.floating_button);
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

    /*Akcje Buttons*/
    private void actionSetOnClickListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//              Toast.makeText(getApplicationContext(), "SNACK BAR", Toast.LENGTH_LONG).show();
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (FloatingBubblePermissions.requiresPermission(MainActivity.this) == false) /*jesli przyznano permission*/ {
                    if (checkPerm() == true /*&& locationManager.isLocationEnabled()*/) {
                        if (/*locationManager.isLocationEnabled() == false*/locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
                            Intent intent_action_location_source_settings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            InfoDialog exampleDialog = new InfoDialog("Proszę włączyć GPS", intent_action_location_source_settings);
                            exampleDialog.show(getSupportFragmentManager(), "example dialog");
                        } else {
                            startService();
                        }
                    }
                } else {
                    FloatingBubblePermissions.startPermissionRequest(MainActivity.this);
                }
            }
        });
    }

    /*Ststus usera Online/Offline*/
    private void userOnline(Boolean state) {
        user.setEmail(user_google_information.getEmail());
        user.setName(user_google_information.getDisplayName());
        user.setUid(user_google_information.getUid());
        user.setOnline(state);
        user.userUpdate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*pierwsze logowanie do poprawy*/
//        noteRef = db.document("users/"+user_google_information.getEmail());
//        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
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

//        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
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

    /*Nawigacja NavDrawer*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ChatFragment()).commit();
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
        serviceIntent.putExtra("inputExtra", "Kliknij aby powrócić");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(123)
    private boolean checkPerm() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET};
        if (EasyPermissions.hasPermissions(this, perms)) {
            return true;
        } else {
            EasyPermissions.requestPermissions(this, "Potrzeba odpowiednich uprawień do poprawnego działania aplikacji", 123, perms);
            return false;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        InfoDialog exampleDialog = new InfoDialog("Dziękuję za przyzanie uprawnień, możesz korzystać z aplikacji.");
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}