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

public class CustomerRegisterActivity extends AppCompatActivity {
    private MaterialEditText fName_editText, lName_editText, retypePass_editText,  regPhone_editText, regEmail_editText, regPassword_editText;
    private Button registerBtn;
    private RelativeLayout registerRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        fName_editText = (MaterialEditText) findViewById(R.id.regEdt_fName);
        lName_editText = (MaterialEditText) findViewById(R.id.regEdt_lName);
        retypePass_editText = (MaterialEditText) findViewById(R.id.regEdt_retypePassword);
        regPhone_editText = (MaterialEditText) findViewById(R.id.regEdt_Phone);
        regEmail_editText = (MaterialEditText) findViewById(R.id.regEdt_Email);
        regPassword_editText = (MaterialEditText) findViewById(R.id.regEdt_password);
        registerRelativeLayout = (RelativeLayout) findViewById(R.id.registerRelative_layout);

        registerRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.registerRelative_layout){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });

        retypePass_editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    signUp(v);

                }
                return false;
            }
        });

        registerBtn = (Button) findViewById(R.id.regBtn_signUp);




        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            signUp(v);
            }

        });



    }
    //Initialize the Firebase Database
    final FirebaseDatabase cDatabase = FirebaseDatabase.getInstance();
    final DatabaseReference table_users = cDatabase.getReference().child("Users").child("Customers");

    public void signUp(View view) {
        if (Common.isConnectedToInternet(getBaseContext())) {
            final ProgressDialog mProgressDialog = new ProgressDialog(CustomerRegisterActivity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

            final String fname = fName_editText.getText().toString();
            final String lname = lName_editText.getText().toString();
            final String phone = regPhone_editText.getText().toString();
            final String email = regEmail_editText.getText().toString();
            final String password = regPassword_editText.getText().toString();
            final String confirmPassword = retypePass_editText.getText().toString();

            if (email.equals("") || password.equals("") || confirmPassword.equals("") || fname.equals("") || lname.equals("") || phone.equals("")) {
                AlertDialog.Builder cLoginBuilder = new AlertDialog.Builder(CustomerRegisterActivity.this);
                cLoginBuilder.setTitle("Something went wrong...");
                cLoginBuilder.setMessage("Some fields were left missing");
                AlertDialog alertDialog = cLoginBuilder.create();
                alertDialog.show();
                mProgressDialog.dismiss();

            } else if (!(password.equals(confirmPassword))) {
                AlertDialog.Builder cLoginBuilder = new AlertDialog.Builder(CustomerRegisterActivity.this);
                cLoginBuilder.setTitle("Something went wrong...");
                cLoginBuilder.setMessage("Your Passwords are not matching.");
                AlertDialog alertDialog = cLoginBuilder.create();
                alertDialog.show();
                mProgressDialog.dismiss();
            } else if (password.length() < 6) {
                Toast.makeText(CustomerRegisterActivity.this, "Password is too short, enter a minimum of 6 characters!", Toast.LENGTH_SHORT).show();

            } else {
                table_users.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(regPhone_editText.getText().toString()).exists()) {

                            mProgressDialog.dismiss();
                            Toast.makeText(CustomerRegisterActivity.this, "User already exists", Toast.LENGTH_SHORT).show();

                        } else {

                            mProgressDialog.dismiss();
                            User user = new User(phone, fname, lname, email, confirmPassword);
                            table_users.child(phone).setValue(user);
                            Toast.makeText(CustomerRegisterActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                            Intent login_intent = new Intent(CustomerRegisterActivity.this, CustomerLoginActivity.class);
                            startActivity(login_intent);
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }


        } else {
            Toast.makeText(CustomerRegisterActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();

        }
    }
}
