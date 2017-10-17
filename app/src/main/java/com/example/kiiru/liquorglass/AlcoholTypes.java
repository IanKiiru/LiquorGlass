package com.example.kiiru.liquorglass;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.kiiru.liquorglass.Interface.ItemClickListener;
import com.example.kiiru.liquorglass.Model.AlcoholTypesModel;
import com.example.kiiru.liquorglass.ViewHolder.AlcoholTypesMenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AlcoholTypes extends AppCompatActivity {
    private FirebaseDatabase cDatabase;
    DatabaseReference alcoholTypes;
    RecyclerView alcoholRecycler_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<AlcoholTypesModel, AlcoholTypesMenuViewHolder> adapter;
    FloatingActionButton viewCartFab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol_types);

        // Initialize Firebase
        cDatabase = FirebaseDatabase.getInstance();
        alcoholTypes =cDatabase.getReference("AlcoholTypes");

        //Load the menu
        alcoholRecycler_menu = (RecyclerView) findViewById(R.id.alcohol_recyclerMenu);
        alcoholRecycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        alcoholRecycler_menu.setLayoutManager(layoutManager);

        viewCartFab = (FloatingActionButton) findViewById(R.id.viewCartFab);
        viewCartFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cart_intent = new Intent(AlcoholTypes.this, Cart.class);
                startActivity(cart_intent);
            }
        });
        
        loadMenu();
    }

    private void loadMenu() {

        adapter = new FirebaseRecyclerAdapter<AlcoholTypesModel, AlcoholTypesMenuViewHolder>(AlcoholTypesModel.class, R.layout.alcohol_menu_item, AlcoholTypesMenuViewHolder.class, alcoholTypes) {
            @Override
            protected void populateViewHolder(AlcoholTypesMenuViewHolder viewHolder, AlcoholTypesModel model, int position) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final AlcoholTypesModel clickItem = model;

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Get AlcoholTypesId and send it to DrinksListActivity
                        Intent drinksList_intent = new Intent(AlcoholTypes.this, DrinksList.class);
                        //Because AlcoholTypeId is key we just key of this item
                        drinksList_intent.putExtra("AlcoholTypesId", adapter.getRef(position).getKey());
                        startActivity(drinksList_intent);
                    }
                });
            }
        };

        alcoholRecycler_menu.setAdapter(adapter);
    }
}
