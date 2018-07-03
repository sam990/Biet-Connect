package com.bietconnect.bietconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 990;
    private FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.FacebookBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build()))
                            .setTheme(R.style.LoginTheme)
                            .build(),
                    RC_SIGN_IN

            );

        } else {
            Toast.makeText(this,
                    "Welcome " + auth.getCurrentUser().getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();
            UserDetails.username = auth.getCurrentUser().getDisplayName();
            startActivity(new Intent(MainActivity.this,Users.class));
            finish();

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully Signed In",
                        Toast.LENGTH_LONG)
                        .show();

                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("users");
                userRef = userRef.child(user.getDisplayName());
                userRef.child("uid").setValue(user.getUid());
                UserDetails.username = user.getDisplayName();
                startActivity(new Intent(MainActivity.this,Users.class));
                finish();


            } else {
                if (response == null) {
                    Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_LONG).show();
                    finish();
                    System.exit(0);
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this,
                            "No Internet",
                            Toast.LENGTH_LONG)
                            .show();
                    finish();
                    System.exit(0);
                }
            }
        }
    }
}