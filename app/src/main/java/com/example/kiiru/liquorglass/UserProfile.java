package com.example.kiiru.liquorglass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.kiiru.liquorglass.common.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    private EditText firstNameField, lastNameField, emailField, passwordField, phoneField;

    private Button backButton, saveButton;

    private ImageView profileImageView;
    private DatabaseReference mCustomerDatabase;

    private String userID;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private String mProfileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firstNameField = (EditText) findViewById(R.id.profileFirstName);
        lastNameField = (EditText) findViewById(R.id.profileLastName);
        emailField = (EditText) findViewById(R.id.profileEmail);
        phoneField = (EditText) findViewById(R.id.profilePhone);
        passwordField = (EditText) findViewById(R.id.profilePassword);

        profileImageView = (ImageView) findViewById(R.id.profileImage);

        backButton = (Button) findViewById(R.id.profileBack);
        saveButton = (Button) findViewById(R.id.profileSave);

        userID = Common.currentUser.getPhone();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);

        getUserInfo();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
    }

    private void getUserInfo(){
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("email") != null) {
                        email = map.get("email").toString();
                        emailField.setText(email);
                    }
                    if (map.get("fName") != null) {
                        firstName = map.get("fName").toString();
                        firstNameField.setText(firstName);
                    }
                    if (map.get("lName") != null) {
                        lastName = map.get("lName").toString();
                        lastNameField.setText(lastName);
                    }
                    if (map.get("phone") != null) {
                        phone = map.get("phone").toString();
                        phoneField.setText(phone);
                    }
                    if (map.get("password") != null) {
                        password = map.get("password").toString();
                        passwordField.setText(password);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void saveUserInformation() {
        email = emailField.getText().toString();
        firstName = firstNameField.getText().toString();
        lastName = lastNameField.getText().toString();
        phone = phoneField.getText().toString();
        password = passwordField.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("email", email);
        userInfo.put("fName", firstName);
        userInfo.put("lName", lastName);
        userInfo.put("phone", phone);
        userInfo.put("password", password);
        mCustomerDatabase.updateChildren(userInfo);
        finish();
    }
}
