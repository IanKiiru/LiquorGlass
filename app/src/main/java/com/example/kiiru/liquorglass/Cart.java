package com.example.kiiru.liquorglass;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.kiiru.liquorglass.Database.Database;
import com.example.kiiru.liquorglass.Model.Order;
import com.example.kiiru.liquorglass.Model.Request;
import com.example.kiiru.liquorglass.common.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    FirebaseDatabase requestDatabase;
    DatabaseReference requestsRef;
    RecyclerView cartRecycler;
    RecyclerView.LayoutManager cartLayoutManager;

    TextView totalTxtView;
    Button placeOrder;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Initialize Firebase

        requestDatabase = FirebaseDatabase.getInstance();
        requestsRef = requestDatabase.getReference("Requests");

        cartRecycler = (RecyclerView) findViewById(R.id.listCart);
        cartRecycler.setHasFixedSize(true);
        cartLayoutManager = new LinearLayoutManager(this);
        cartRecycler.setLayoutManager(cartLayoutManager);

        totalTxtView = (TextView) findViewById(R.id.totalTxtView);
        placeOrder = (Button) findViewById(R.id.btnPlaceOrder);
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create new request
                showAlertDialog();

            }
        });

        loadListDrinks();
    }

    private void showAlertDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One More Step...");
        alertDialog.setMessage("Enter your address: ");

        final EditText edtAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress); // Adding an Edit Text Field to AlertDialog
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getfName(),
                        edtAddress.getText().toString(),
                        totalTxtView.getText().toString(),
                        cart);

                // Submitting request to Firebase
                // Using System.currentTimeMillis to key
                requestsRef.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);

                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "Thank you, Order Placed", Toast.LENGTH_SHORT).show();
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

    private void loadListDrinks() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        cartRecycler.setAdapter(adapter);

        int total = 0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("en", "KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        totalTxtView.setText(fmt.format(total));
    }
}
