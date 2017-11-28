package com.example.kiiru.liquorglass;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiiru.liquorglass.Model.User;
import com.example.kiiru.liquorglass.common.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.example.kiiru.liquorglass.Home.MY_PERMISSIONS_REQUEST_LOCATION;

public class MainActivity extends AppCompatActivity {

    private Button btn_register, btn_login;
    RelativeLayout rootLayout;
    FirebaseAuth auth;
    ImageView profileImageView;
    private Uri resultUri;
    SpotsDialog regProgressDialog;

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
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

        }
        auth = FirebaseAuth.getInstance();
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        btn_register = (Button) findViewById(R.id.btn_signUp);
        btn_login = (Button) findViewById(R.id.btn_signIn);

        regProgressDialog = new SpotsDialog(MainActivity.this);




        // Init Paper
        Paper.init(this);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();

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


    private void login( final String email, final String pwd) {

        //Initialize the Firebase Database
        final FirebaseDatabase cDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference table_users = cDatabase.getReference().child("Users").child("Customers");

        if (Common.isConnectedToInternet(getBaseContext())) {

            final SpotsDialog mProgressDialog = new SpotsDialog(MainActivity.this);
            mProgressDialog.show();

            auth.signInWithEmailAndPassword(email, pwd)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            final String currentUserId = auth.getCurrentUser().getUid();
                            table_users.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //Check if user exists in database
                                    if (dataSnapshot.child(currentUserId).exists()) {
                                        //Get User Information
                                        User user = dataSnapshot.child(currentUserId).getValue(User.class);
                                        user.setEmail(email);  //setEmail;
                                        Common.currentUser = user;
                                        mProgressDialog.dismiss();
                                        Snackbar.make(rootLayout, "Sign in successful", Snackbar.LENGTH_SHORT)
                                                .show();
                                        startActivity(new Intent(MainActivity.this, AlcoholTypes.class));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Snackbar.make(rootLayout, "DatabaseError: "+databaseError.getMessage(), Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Snackbar.make(rootLayout, "Sign in failed: " +e.getMessage(), Snackbar.LENGTH_LONG)
                            .show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();

        }
    }

    private void showLoginDialog() {
        //Initialize the Firebase Database
        final FirebaseDatabase cDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference table_users = cDatabase.getReference().child("Users").child("Customers");

        if (Common.isConnectedToInternet(getBaseContext())) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN ");
        dialog.setMessage("Please use your email to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login, null);
            dialog.setView(login_layout);

        final MaterialEditText edtEmail = (MaterialEditText) login_layout.findViewById(R.id.edtLoginEmail);
        final MaterialEditText edtPassword = (MaterialEditText) login_layout.findViewById(R.id.edtLoginPassword);
            final com.rey.material.widget.CheckBox checkBox = (com.rey.material.widget.CheckBox) login_layout.findViewById(R.id.chkBoxRememberMe);




        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
               final SpotsDialog loginProgressDialog = new SpotsDialog(MainActivity.this);
                loginProgressDialog.show();
                if (checkBox.isChecked()) {
                    Paper.book().write(Common.USER_KEY, edtEmail.getText().toString());
                    Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                }

                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    loginProgressDialog.dismiss();
                    Snackbar.make(rootLayout, "Some fields were left missing...", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    loginProgressDialog.dismiss();
                    Snackbar.make(rootLayout, "Some fields were left missing...", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (edtPassword.getText().toString().length() < 6) {
                    loginProgressDialog.dismiss();
                    Snackbar.make(rootLayout, "Password is too short", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                } else {
                    auth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    loginProgressDialog.dismiss();
                                    final String userId = auth.getCurrentUser().getUid();
                                    table_users.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            //Check if user exists in database
                                            if (dataSnapshot.child(userId).exists()) {
                                                //Get User Information
                                                User user = dataSnapshot.child(userId).getValue(User.class);
                                                user.setEmail(edtEmail.getText().toString()); //set Email
                                                Common.currentUser = user;

                                                Snackbar.make(rootLayout, "Sign in successful", Snackbar.LENGTH_SHORT)
                                                        .show();
                                                startActivity(new Intent(MainActivity.this, AlcoholTypes.class));

                                            }


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Snackbar.make(rootLayout, "DatabaseError: "+databaseError.getMessage(), Snackbar.LENGTH_LONG)
                                                    .show();



                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


                }
            }
        });
            dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialog.show();

    } else {

            Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
        }

    }
     FirebaseDatabase cDatabase = FirebaseDatabase.getInstance();
    DatabaseReference table_users = cDatabase.getReference().child("Users").child("Customers");
    private void showRegisterDialog() {
        if (Common.isConnectedToInternet(getBaseContext())) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enter details to register ");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register, null);
            dialog.setView(register_layout);

        final MaterialEditText edtEmail = (MaterialEditText) register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtFirstName = (MaterialEditText) register_layout.findViewById(R.id.edtFirstName);
        final MaterialEditText edtLastName = (MaterialEditText) register_layout.findViewById(R.id.edtLastName);
        final MaterialEditText edtPhone = (MaterialEditText) register_layout.findViewById(R.id.edtPhone);
        final MaterialEditText edtPassword = (MaterialEditText) register_layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtConfirmPassword = (MaterialEditText) register_layout.findViewById(R.id.edtConfirmPassword);
            profileImageView = (ImageView) register_layout.findViewById(R.id.profileImage);
            profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profileImageIntent = new Intent(Intent.ACTION_PICK);
                    profileImageIntent.setType("image/*");
                    startActivityForResult(profileImageIntent, 1);
                }
            });


        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                dialog.dismiss();
                regProgressDialog.show();


                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    regProgressDialog.dismiss();
                    Snackbar.make(rootLayout, "Some fields were left missing", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(edtPhone.getText().toString())) {
                    regProgressDialog.dismiss();
                    Snackbar.make(rootLayout, "Please enter phone number", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(edtFirstName.getText().toString())) {
                    regProgressDialog.dismiss();
                    Snackbar.make(rootLayout, "Please enter your first name", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(edtLastName.getText().toString())) {
                    regProgressDialog.dismiss();
                    Snackbar.make(rootLayout, "Please enter your last name", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    regProgressDialog.dismiss();
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (edtPassword.getText().toString().length() < 6) {
                    regProgressDialog.dismiss();
                    Snackbar.make(rootLayout, "Password is too short", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(edtConfirmPassword.getText().toString())) {
                    regProgressDialog.dismiss();
                    Snackbar.make(rootLayout, "Please re-enter your password", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (!(edtConfirmPassword.getText().toString().equals(edtPassword.getText().toString()))) {
                    regProgressDialog.dismiss();
                    Snackbar.make(rootLayout, "Your passwords do not match", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                } else {

                    auth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtConfirmPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    User user = new User();
                                    user.setEmail(edtEmail.getText().toString());
                                    user.setPhone(edtPhone.getText().toString());
                                    user.setPassword(edtConfirmPassword.getText().toString());
                                    user.setfName(edtFirstName.getText().toString());
                                    user.setlName(edtLastName.getText().toString());
                                    Common.currentUser = user;

                                    table_users.child(auth.getCurrentUser().getUid())
                                            .setValue(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    uploadImage();
                                                    regProgressDialog.dismiss();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            regProgressDialog.dismiss();
                                            Snackbar.make(rootLayout, "Registration failed: " +e.getMessage(), Snackbar.LENGTH_LONG)
                                                    .show();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            regProgressDialog.dismiss();
                            Snackbar.make(rootLayout, "Registration failed: " +e.getMessage(), Snackbar.LENGTH_LONG)
                                    .show();

                        }
                    });

                }

            }
        });
            dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialog.show();
    }  else {
            Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();

        }
    }
    private void uploadImage(){
        final String userId = auth.getCurrentUser().getUid();
        if(resultUri != null) {

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    regProgressDialog.dismiss();

                    Toast.makeText(MainActivity.this, "Registration failed " +e.getMessage(), Toast.LENGTH_LONG).show();
                    table_users.child(userId).removeValue();
                    auth.getCurrentUser().delete();

                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Map newImage = new HashMap();
                    newImage.put("profileImageUrl", downloadUrl.toString());
                    table_users.child(userId).updateChildren(newImage);

                    Snackbar.make(rootLayout, "Registration Successful", Snackbar.LENGTH_LONG)
                            .show();
                    showLoginDialog();
                }
            });
        }else{
            table_users.child(userId).removeValue();
            auth.getCurrentUser().delete();
            Toast.makeText(MainActivity.this, "Registration failed : You did not set a profile image ", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            profileImageView.setImageURI(resultUri);
        }
    }

}



