package com.example.kiiru.liquorglass;

import android.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiiru.liquorglass.Model.User;
import com.example.kiiru.liquorglass.common.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

import static com.example.kiiru.liquorglass.Home.MY_PERMISSIONS_REQUEST_LOCATION;

public class MainActivity extends AppCompatActivity {

    private Button btn_register, btn_login;
    private TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

        }
        txtSlogan = (TextView) findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Nabila.ttf");
        txtSlogan.setTypeface(face);
        btn_register = (Button) findViewById(R.id.btn_signUp);
        btn_login = (Button) findViewById(R.id.btn_signIn);
        // Init Paper
        Paper.init(this);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register_intent = new Intent(MainActivity.this, CustomerRegisterActivity.class);
                startActivity(register_intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
                startActivity(login_intent);

            }
        });

        //Check Remember me

        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);

        if (user !=null && pwd!=null){

            if (!user.isEmpty() && !pwd.isEmpty()){
                login(user,pwd);

            }

        }
    }


    private void login( final String phone, final String pwd) {

        //Initialize the Firebase Database
        final FirebaseDatabase cDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference table_users = cDatabase.getReference().child("Users").child("Customers");

        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();
            table_users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Check if user exists in database
                    if (dataSnapshot.child(phone).exists()) {
                        //Get User Information
                        mProgressDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone); //set Phone

                        if (user.getPassword().equals(pwd)) {
                            Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                            Intent alcoholTypes_intent = new Intent(MainActivity.this, AlcoholTypes.class);
                            Common.currentUser = user;
                            startActivity(alcoholTypes_intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();

        }
    }
}



