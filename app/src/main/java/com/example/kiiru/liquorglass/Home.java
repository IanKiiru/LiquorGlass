package com.example.kiiru.liquorglass;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiiru.liquorglass.common.Common;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private FirebaseDatabase cDatabase;
    private TextView txtFullName;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mapFragment;
    private LocationRequest mLocationRequest;
    private DatabaseReference locationRef;
    Location lastLocation;
    Button confirmLoc;
    private LatLng customerLocation;
    private String destination;
    private Boolean requestBol = false;
    private Marker deliveryMarker;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    mapFragment.getMapAsync(this);
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Permission to access your location denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        locationRef = FirebaseDatabase.getInstance().getReference();
        mapFragment = SupportMapFragment.newInstance();
        android.support.v4.app.FragmentManager sFm = getSupportFragmentManager();
        sFm.beginTransaction().add(R.id.mapCustomer, mapFragment).commit();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            mapFragment.getMapAsync(this);
        }


        confirmLoc = (Button) findViewById(R.id.confirm_locationBtn);
        confirmLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestBol) {
                    requestBol = false;
                    geoQuery.removeAllListeners();
                    merchantLocationRef.removeEventListener(merchantLocationRefListener);

                    if (merchantFoundID != null) {
                        DatabaseReference merchantRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Merchants").child(merchantFoundID).child("customerRequest");
                        merchantRef.removeValue();
                        merchantFoundID = null;

                    }
                    merchantFound = false;
                    radius = 1;
                    String userId = Common.currentUser.getPhone();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.removeLocation(userId);

                    if (deliveryMarker != null) {
                        deliveryMarker.remove();
                    }
                    if (merchantMarker != null) {
                        merchantMarker.remove();
                    }
                    confirmLoc.setText("CONFIRM LOCATION");
                } else {
                    requestBol = true;

                    String userId = Common.currentUser.getPhone();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()));

                    customerLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    deliveryMarker = mMap.addMarker(new MarkerOptions().position(customerLocation).title("Delivery to be made here"));
                    confirmLoc.setText("Finding liquor store ...");
                    getClosestStore();
                }
            }
        });




        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set Name for user on Navigation header

        View headerLayout = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerLayout.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getfName());

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                destination = place.getName().toString();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("KE")
                .build();
        autocompleteFragment.setFilter(typeFilter);


    }



    private int radius = 1;
    private Boolean merchantFound = false;
    private String merchantFoundID;

    GeoQuery geoQuery;

    private void getClosestStore() {
        DatabaseReference merchantLocation = FirebaseDatabase.getInstance().getReference().child("merchantsAvailable");

        GeoFire geoFire = new GeoFire(merchantLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(customerLocation.latitude, customerLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!merchantFound) {
                    merchantFound = true;
                    merchantFoundID = key;

                    DatabaseReference merchantRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Merchants").child(merchantFoundID).child("CustomerRequest");
                    String customerId = Common.currentUser.getPhone();
                    HashMap map = new HashMap();
                    map.put("customerOrderId", customerId);
                    map.put("destination", destination);
                    merchantRef.updateChildren(map);

                    getMerchantLocation();
                    confirmLoc.setText("Looking for store's Location....");
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!merchantFound) {
                    radius++;
                    getClosestStore();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }



    private Marker merchantMarker;
    private ValueEventListener merchantLocationRefListener;
    private DatabaseReference merchantLocationRef;

    private void getMerchantLocation() {
        merchantLocationRef = FirebaseDatabase.getInstance().getReference().child("merchantsWorking").child(merchantFoundID).child("l");
        merchantLocationRefListener = merchantLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && requestBol) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    confirmLoc.setText("Merchant Found");
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng merchantLatLng = new LatLng(locationLat, locationLng);
                    if (merchantMarker != null) {
                        merchantMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(customerLocation.latitude);
                    loc1.setLongitude(customerLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(merchantLatLng.latitude);
                    loc2.setLongitude(merchantLatLng.longitude);

                    float distance = loc2.distanceTo(loc1);
                    if (distance < 100) {
                        confirmLoc.setText("Merchant is within you vicinity");
                    } else {
                        confirmLoc.setText("Liquor Store  Found: " + String.valueOf(distance));
                        merchantMarker = mMap.addMarker(new MarkerOptions().position(merchantLatLng).title("Nearest Liquor Store").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_liquorcoltour)));
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            Intent alcohol_menuIntent = new Intent(Home.this, AlcoholTypes.class);
            startActivity(alcohol_menuIntent);


            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            Intent cart_intent = new Intent(Home.this, Cart.class);
            startActivity(cart_intent);

        } else if (id == R.id.nav_orders) {
            Intent order_intent = new Intent(Home.this, OrderStatus.class);
            startActivity(order_intent);

        } else if (id == R.id.nav_logout) {
            Intent signOut_intent = new Intent(Home.this, CustomerLoginActivity.class);
            signOut_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signOut_intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Trying to find your location", Toast.LENGTH_LONG).show();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


        @Override
        public void onLocationChanged (Location location){
            lastLocation = location;
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, 15);
            mMap.animateCamera(update);

        }
    }

