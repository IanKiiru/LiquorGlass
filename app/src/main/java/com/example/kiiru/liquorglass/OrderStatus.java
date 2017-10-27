package com.example.kiiru.liquorglass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.kiiru.liquorglass.Model.Request;
import com.example.kiiru.liquorglass.ViewHolder.OrderViewHolder;
import com.example.kiiru.liquorglass.common.Common;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {
    FirebaseDatabase orderDatabase;
    DatabaseReference orderDatabaseRef;
    RecyclerView orderRecycler;
    RecyclerView.LayoutManager orderLayoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        // Initialize database
        orderDatabase = FirebaseDatabase.getInstance();
        orderDatabaseRef =orderDatabase.getReference("customerRequest").child("itemsOrdered");

        //Load the menu
        orderRecycler = (RecyclerView) findViewById(R.id.listOrders);
        orderRecycler.setHasFixedSize(true);
        orderLayoutManager = new LinearLayoutManager(this);
        orderRecycler.setLayoutManager(orderLayoutManager);

        loadOrders(Common.currentUser.getPhone());
    }

    private void loadOrders(String phone) {

        orderAdapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                orderDatabaseRef.orderByChild("phone")
                .equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {

                viewHolder.txtOrderId.setText("Order Id: " +orderAdapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText("Order status: " +convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText("Address: " +model.getFname());
                viewHolder.txtOrderPhone.setText("Phone number: " +model.getPhone());
                viewHolder.customerName.setText("Name: " +Common.currentUser.getlName());



            }
        };

        orderRecycler.setAdapter(orderAdapter);
    }

    private String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";

        else if (status.equals("1"))
            return "Order is on its way";

        else
            return "Delivered";
    }
}
