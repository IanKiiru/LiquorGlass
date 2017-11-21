package com.example.kiiru.liquorglass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.kiiru.liquorglass.Model.Request;
import com.example.kiiru.liquorglass.ViewHolder.OrderViewHolder;
import com.example.kiiru.liquorglass.common.Common;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import static com.example.kiiru.liquorglass.Cart.order_number;


public class OrderStatus extends AppCompatActivity {
    FirebaseDatabase orderDatabase;
    DatabaseReference orderDatabaseRef;
    RecyclerView orderRecycler;
    RecyclerView.LayoutManager orderLayoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> orderAdapter;
    FirebaseAuth auth;


    String userId;
    String destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);



                // Initialize database
        auth = FirebaseAuth.getInstance();
        userId  = auth.getCurrentUser().getUid();
        orderDatabase = FirebaseDatabase.getInstance();
        orderDatabaseRef =orderDatabase.getReference("customerRequest").child(userId).child("orderDetails");

        //Load the menu
        orderRecycler = (RecyclerView) findViewById(R.id.listOrders);
        orderRecycler.setHasFixedSize(true);
        orderLayoutManager = new LinearLayoutManager(this);
        orderRecycler.setLayoutManager(orderLayoutManager);

        loadOrders(Common.currentUser.getPhone());

    }
    DatabaseReference destinationRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(userId).child("orderDetails").child(order_number);
    ValueEventListener destinationRefListener = destinationRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if (map.get("address") != null) {
                    destination = map.get("address").toString();


                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });

    String firstName = Common.currentUser.getfName();
    String lastName = Common.currentUser.getlName();


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
                viewHolder.txtOrderStatus.setText("Order status: " +Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText("Address: " +destination);
                viewHolder.txtOrderPhone.setText("Phone number: " +model.getPhone());
                viewHolder.customerName.setText("Name: " +firstName +" " +lastName);



            }
        };

        orderRecycler.setAdapter(orderAdapter);
    }

}
