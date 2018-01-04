package com.example.kiiru.liquorglass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.kiiru.liquorglass.Database.Database;
import com.example.kiiru.liquorglass.Model.MyResponse;
import com.example.kiiru.liquorglass.Model.Notification;
import com.example.kiiru.liquorglass.Model.Order;
import com.example.kiiru.liquorglass.Model.Request;
import com.example.kiiru.liquorglass.Model.Sender;
import com.example.kiiru.liquorglass.Model.Token;
import com.example.kiiru.liquorglass.Remote.APIService;
import com.example.kiiru.liquorglass.common.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class Cart extends AppCompatActivity {

    public static String order_number = String.valueOf(System.currentTimeMillis());
    FirebaseDatabase requestDatabase;
    DatabaseReference requestsRef;
    RecyclerView cartRecycler;
    RecyclerView.LayoutManager cartLayoutManager;
    TextView totalTxtView;
    Button placeOrder;
    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    FirebaseAuth auth;
    APIService mService;

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
        setContentView(R.layout.activity_cart);

        //Init service
        mService = Common.getFCMService();

        //Initialize Firebase
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        requestDatabase = FirebaseDatabase.getInstance();
        requestsRef = requestDatabase.getReference("customerRequest").child(userId).child("orderDetails");

        cartRecycler = findViewById(R.id.listCart);
        cartRecycler.setHasFixedSize(true);
        cartLayoutManager = new LinearLayoutManager(this);
        cartRecycler.setLayoutManager(cartLayoutManager);

        totalTxtView = findViewById(R.id.totalTxtView);
        placeOrder = findViewById(R.id.btnPlaceOrder);
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() > 0) {
                    Snackbar.make(v, "Make sure you verify that your order is correct before placing it", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //Create new request
                    showAlertDialog();
                } else{
                    Toast.makeText(Cart.this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        loadListDrinks();

        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void updateToken(String token) {
        String userId = auth.getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token,false);
        tokens.child(userId).setValue(data);
    }

    private void showAlertDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One More Step...");
        alertDialog.setMessage("Pick your location: ");

        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getfName(),
                        Common.currentUser.getlName(),
                        totalTxtView.getText().toString(),
                        cart);



                // Submitting request to Firebase
                // Using System.currentTimeMillis to key
                requestsRef.child(order_number)
                        .setValue(request);

                new Database(getBaseContext()).cleanCart();
                Intent home_intent = new Intent(Cart.this, MapActivity.class);
                startActivity(home_intent);
                finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alertDialog.show();



    }

    /*private void sendNotificationOfOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("merchantToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Token merchantToken = postSnapshot.getValue(Token.class);


                    //Creating raw payload to send

                    Notification notification = new Notification("LIQUOR GLASS", "You have a new order "+order_number);
                    Sender content = new Sender(merchantToken.getToken(), notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        if (response.body().success == 1) {
                                            Toast.makeText(Home.this, "Order placed", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Home.this, "Failed to send notification", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR", t.getMessage());

                                }
                            });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/



    private void loadListDrinks() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        cartRecycler.setAdapter(adapter);

        int total = 0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("en", "KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        totalTxtView.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position) {
        //We remove item from List<Order> by position
        cart.remove(position);
        //After that we delete old data from SQLLite Database
        new Database(this).cleanCart();
        //Finally we update new data to database from List<Order>
        for(Order item:cart)
            new Database(this).addToCart(item);
        // Refresh cart
        loadListDrinks();
    }
}
