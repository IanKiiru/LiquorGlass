package com.example.kiiru.liquorglass;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    }
}
