package com.example.kiiru.liquorglass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiiru.liquorglass.Interface.ItemClickListener;
import com.example.kiiru.liquorglass.Model.AlcoholTypesModel;
import com.example.kiiru.liquorglass.Model.Token;
import com.example.kiiru.liquorglass.ViewHolder.AlcoholTypesMenuViewHolder;
import com.example.kiiru.liquorglass.common.Common;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DatabaseReference alcoholTypes;
    RecyclerView alcoholRecycler_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<AlcoholTypesModel, AlcoholTypesMenuViewHolder> adapter;
    FloatingActionButton viewCartFab;
    FirebaseAuth auth;
    String userId;
    private FirebaseDatabase cDatabase;
    private TextView txtFullName;




    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token,false);
        tokens.child(userId).setValue(data);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        // Initialize Firebase
        cDatabase = FirebaseDatabase.getInstance();
        alcoholTypes =cDatabase.getReference("AlcoholTypes");
        auth = FirebaseAuth.getInstance();

        userId = auth.getCurrentUser().getUid();

        //Load the menu
        alcoholRecycler_menu = findViewById(R.id.alcohol_recyclerMenu);
        alcoholRecycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        alcoholRecycler_menu.setLayoutManager(layoutManager);

        viewCartFab = findViewById(R.id.viewCartFab);
        viewCartFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cart_intent = new Intent(Home.this, Cart.class);
                startActivity(cart_intent);
            }
        });

        if(Common.isConnectedToInternet(this))
            loadMenu();

        else {
            Toast.makeText(Home.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        updateToken(FirebaseInstanceId.getInstance().getToken());





        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set Name for user on Navigation header

        View headerLayout = navigationView.getHeaderView(0);
        txtFullName = headerLayout.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getfName()+" "+Common.currentUser.getlName() );







    }

    private void loadMenu() {

        adapter = new FirebaseRecyclerAdapter<AlcoholTypesModel, AlcoholTypesMenuViewHolder>(AlcoholTypesModel.class, R.layout.alcohol_menu_item, AlcoholTypesMenuViewHolder.class, alcoholTypes) {
            @Override
            protected void populateViewHolder(AlcoholTypesMenuViewHolder viewHolder, AlcoholTypesModel model, int position) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Get AlcoholTypesId and send it to DrinksListActivity
                        Intent drinksList_intent = new Intent(Home.this, DrinksList.class);
                        //Because AlcoholTypeId is key we just key of this item
                        drinksList_intent.putExtra("AlcoholTypesId", adapter.getRef(position).getKey());
                        startActivity(drinksList_intent);
                    }
                });
            }
        };

        alcoholRecycler_menu.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        if (id == R.id.nav_cart) {
            Intent cart_intent = new Intent(Home.this, Cart.class);
            startActivity(cart_intent);

        } else if (id == R.id.nav_orders) {
            Intent order_intent = new Intent(Home.this, Orders.class);
            startActivity(order_intent);

        } else if (id == R.id.nav_logout) {
            Paper.book().destroy();
            FirebaseAuth.getInstance().signOut();
            Intent signOut_intent = new Intent(Home.this, MainActivity.class);
            signOut_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signOut_intent);
        } else if (id == R.id.nav_profile) {
            Intent profileIntent = new Intent(Home.this, UserProfile.class);
            startActivity(profileIntent);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}

