package com.example.kiiru.liquorglass;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.kiiru.liquorglass.Database.Database;
import com.example.kiiru.liquorglass.Model.DrinksModel;
import com.example.kiiru.liquorglass.Model.Order;
import com.example.kiiru.liquorglass.common.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DrinkDetails extends AppCompatActivity {

    TextView drink_name, drink_price, drink_description;
    ImageView drink_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btn_cart;
    ElegantNumberButton numberButton;

    FirebaseDatabase detailsDatabase;
    DatabaseReference detailsRef;
    DrinksModel currentDrinkSModel;

    String drinkId="";

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
        setContentView(R.layout.activity_drink_details);

        //Initialize Database

        detailsDatabase = FirebaseDatabase.getInstance();
        detailsRef = detailsDatabase.getReference("Drinks");


        numberButton = findViewById(R.id.number_button);
        btn_cart = findViewById(R.id.details_btn_cart);

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        drinkId,
                        currentDrinkSModel.getName(),
                        numberButton.getNumber(),
                        currentDrinkSModel.getPrice()

                ));

                Toast.makeText(DrinkDetails.this, "Added to cart", Toast.LENGTH_SHORT).show();
            }
        });

        drink_description = findViewById(R.id.drink_description);
        drink_price = findViewById(R.id.drink_price);
        drink_name = findViewById(R.id.drink_name);
        drink_image = findViewById(R.id.details_drink_image);

        collapsingToolbarLayout = findViewById(R.id.details_collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        if(getIntent() !=null)
            drinkId = getIntent().getStringExtra("DrinkId");

        if (!drinkId.isEmpty()){

            if (Common.isConnectedToInternet(getBaseContext()))
                    getDrinkDetails(drinkId);
            else {
                Toast.makeText(DrinkDetails.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();

            }
        }

    }

    private void getDrinkDetails(String drinkId) {
        detailsRef.child(drinkId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentDrinkSModel = dataSnapshot.getValue(DrinksModel.class);

                Picasso.with(getBaseContext()).load(currentDrinkSModel.getImage())
                        .into(drink_image);

                collapsingToolbarLayout.setTitle(currentDrinkSModel.getName());
                drink_price.setText("Ksh "+currentDrinkSModel.getPrice());
                drink_name.setText(currentDrinkSModel.getName());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
