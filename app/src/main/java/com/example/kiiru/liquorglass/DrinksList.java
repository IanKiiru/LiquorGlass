package com.example.kiiru.liquorglass;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.kiiru.liquorglass.Interface.ItemClickListener;
import com.example.kiiru.liquorglass.Model.DrinksModel;
import com.example.kiiru.liquorglass.ViewHolder.DrinksListViewHolder;
import com.example.kiiru.liquorglass.common.Common;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DrinksList extends AppCompatActivity {

    FirebaseDatabase drinksDatabase;
    DatabaseReference drinksRef;
    FloatingActionButton viewCartFab;
    RecyclerView drinksRecycler_view;
    RecyclerView.LayoutManager drinksLayoutManager;

    String alcoholTypeID ="";
    FirebaseRecyclerAdapter<DrinksModel, DrinksListViewHolder> drinksAdapter;

    //search drinks functionality
    FirebaseRecyclerAdapter<DrinksModel, DrinksListViewHolder> searchAdapter;
    List<String> suggestionList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinks_list);

        // Initialize Firebase
        drinksDatabase = FirebaseDatabase.getInstance();
        drinksRef =drinksDatabase.getReference("Drinks");


        //Cart Floating Action Button
        viewCartFab = (FloatingActionButton) findViewById(R.id.viewCart_drinksList);
        viewCartFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cart_intent = new Intent(DrinksList.this, Cart.class);
                startActivity(cart_intent);
            }
        });

        //Load the menu
        drinksRecycler_view = (RecyclerView) findViewById(R.id.drinksList_recycler);
        drinksRecycler_view.setHasFixedSize(true);
        drinksLayoutManager = new LinearLayoutManager(this);
        drinksRecycler_view.setLayoutManager(drinksLayoutManager);



        if(getIntent() !=null)
            alcoholTypeID = getIntent().getStringExtra("AlcoholTypesId");

        if (!alcoholTypeID.isEmpty() && alcoholTypeID !=null){

            if (Common.isConnectedToInternet(getBaseContext()))
                    loadListDrinks(alcoholTypeID);
            else {
                Toast.makeText(DrinksList.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();

            }
        }

        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint("Search drinks");

        loadSuggestions();
        materialSearchBar.setLastSuggestions(suggestionList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggestion = new ArrayList<>();
                for (String search:suggestionList)
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggestion.add(search);
                }
                materialSearchBar.setLastSuggestions(suggestion);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled)
                    drinksRecycler_view.setAdapter(drinksAdapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<DrinksModel, DrinksListViewHolder>(
                DrinksModel.class,
                R.layout.drinks_menu_item,
                DrinksListViewHolder.class,
                drinksRef.orderByChild("Name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(DrinksListViewHolder viewHolder, DrinksModel model, int position) {
                viewHolder.txtDrinkName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.drinksImageView);
                final DrinksModel local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent drinkDetails = new Intent(DrinksList.this, DrinkDetails.class);
                        drinkDetails.putExtra("DrinkId", searchAdapter.getRef(position).getKey()); // Get DrinkId and send it to DrinkDetailsActivity
                        startActivity(drinkDetails);

                    }
                });

            }
        };
        drinksRecycler_view.setAdapter(searchAdapter);
    }

    private void loadSuggestions() {
        drinksRef.orderByChild("TypeId").equalTo(alcoholTypeID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            DrinksModel item = postSnapshot.getValue(DrinksModel.class);
                            suggestionList.add(item.getName());

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadListDrinks(String alcoholTypeID) {
        drinksAdapter = new FirebaseRecyclerAdapter<DrinksModel, DrinksListViewHolder>(DrinksModel.class,
                R.layout.drinks_menu_item,
                DrinksListViewHolder.class,
                drinksRef.orderByChild("TypeId").equalTo(alcoholTypeID)
                ) {
            @Override
            protected void populateViewHolder(DrinksListViewHolder viewHolder, DrinksModel model, int position) {

                viewHolder.txtDrinkName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.drinksImageView);
                final DrinksModel local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent drinkDetails = new Intent(DrinksList.this, DrinkDetails.class);
                        drinkDetails.putExtra("DrinkId", drinksAdapter.getRef(position).getKey()); // Get DrinkId and send it to DrinkDetailsActivity
                        startActivity(drinkDetails);

                    }
                });

            }
        };
        drinksRecycler_view.setAdapter(drinksAdapter);
    }
}
