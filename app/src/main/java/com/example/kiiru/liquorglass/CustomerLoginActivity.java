package com.example.kiiru.liquorglass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.kiiru.liquorglass.Model.User;
import com.example.kiiru.liquorglass.common.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class CustomerLoginActivity extends AppCompatActivity {

    private MaterialEditText phone_editText, password_editText;
    private Button loginBtn;
    private RelativeLayout loginRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        phone_editText = (MaterialEditText) findViewById(R.id.edt_Phone);
        password_editText = (MaterialEditText) findViewById(R.id.edt_password);
        loginRelativeLayout = (RelativeLayout) findViewById(R.id.loginRelative_layout);

        loginRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.loginRelative_layout){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });

        password_editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    signIn(v);
                }
                return false;
            }
        });

        loginBtn = (Button) findViewById(R.id.loginBtn_signIn);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(v);
            }
        });

    }

    //Initialize the Firebase Database
    final FirebaseDatabase cDatabase = FirebaseDatabase.getInstance();
    final DatabaseReference table_users = cDatabase.getReference().child("Users").child("Customers");


    public void signIn(View view){
        final ProgressDialog mProgressDialog = new ProgressDialog(CustomerLoginActivity.this);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        final String phone = phone_editText.getText().toString();
        final String password = password_editText.getText().toString();

        if (phone.equals("") || password.equals("")) {
            AlertDialog.Builder cLoginBuilder = new AlertDialog.Builder(CustomerLoginActivity.this);
            cLoginBuilder.setTitle("Something went wrong...");
            cLoginBuilder.setMessage("Some fields were left missing");
            AlertDialog alertDialog = cLoginBuilder.create();
            alertDialog.show();
            mProgressDialog.dismiss();
        }else {
            table_users.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Check if user exists in database
                    if (dataSnapshot.child(phone_editText.getText().toString()).exists()) {
                        //Get User Information
                        mProgressDialog.dismiss();
                        User user = dataSnapshot.child(phone_editText.getText().toString()).getValue(User.class);
                        user.setPhone(phone_editText.getText().toString()); //set Phone

                        if (user.getPassword().equals(password_editText.getText().toString())) {
                            Toast.makeText(CustomerLoginActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                            Intent home_intent = new Intent(CustomerLoginActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(home_intent);
                        } else {
                            Toast.makeText(CustomerLoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(CustomerLoginActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();

                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
