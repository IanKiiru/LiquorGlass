package com.example.kiiru.liquorglass.Service;

import android.widget.Toast;

import com.example.kiiru.liquorglass.Model.Token;
import com.example.kiiru.liquorglass.common.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;



public class MyFirebaseIdService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token token = new Token(tokenRefreshed,true);
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
        } else {
            Toast.makeText(this, "Cannot update token to database", Toast.LENGTH_SHORT).show();
        }

    }
}
