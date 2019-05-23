package com.barebrains.where;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.barebrains.where.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "EmailPassword";

    private FirebaseAuth mAuth;
    private TextView w_text;
    private FusedLocationProviderClient fusedLocationClient;
    private double la=0,lo=0;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private ChildEventListener mChildEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        BottomNavigationView nav =(BottomNavigationView) findViewById(R.id.nav);
        nav.setOnNavigationItemSelectedListener(this);
        loadFragment(new HomeFragment());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

            Log.e("p","permission not granted");
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.e("f","Successs");
                if (location != null) {
                    la = location.getLatitude();
                    lo = location.getLongitude();
                    FirebaseDatabase.getInstance().getReference("location").child(user.getUid()).setValue(String.valueOf(la)+','+String.valueOf(lo));
                    FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("Location").setValue(String.valueOf(la)+','+String.valueOf(lo));
                }
                else
                {
                    Log.e("f","location not fetched");
                }

            }
        });





    }
    private boolean loadFragment(Fragment fragment)

    {

        if(fragment!=null)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.Fragment_container,fragment)
                     .commit();
            return  true;
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId())
        {
            case R.id.action_home:
                fragment = new HomeFragment();
                break;
            case R.id.action_profile:
                fragment = new ProfileFragment();
                break;


        }
        return loadFragment(fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.fr_req) {

            // Do something
            Intent cfr= new Intent(this,Friend_requests.class);
            startActivity(cfr);

        }

        return super.onOptionsItemSelected(item);
    }

}
