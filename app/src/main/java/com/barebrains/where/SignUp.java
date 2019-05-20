package com.barebrains.where;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.barebrains.where.R;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference mDatabase;


    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private EditText mPassword;
    private EditText mName;
    private EditText mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mPassword = findViewById(R.id.password);
        mName = findViewById(R.id.avatar_name);
        mEmail = findViewById(R.id.Email);
        findViewById(R.id.register).setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        //Intent intent = new Intent(this, Profile.class);
        //startActivity(intent);
        if (!validateForm()) {
            Log.d(TAG,"failure");
            return;
        }


        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            String key = user.getUid();
                            mDatabase.child(key).child("Email").setValue(mEmail.getText().toString());
                            mDatabase.child(key).child("Name").setValue(mName.getText().toString());
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
        // [END create_user_with_email]
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            Intent intent = new Intent(this, Profile.class);
            intent.putExtra("user", user);

            startActivity(intent);

        }
    }
    @Override
    public void onClick(View v) {
        Log.e(TAG,"click working");
        int i = v.getId();
        if (i == R.id.register) {
            createAccount(mEmail.getText().toString(), mPassword.getText().toString());
        }
    }
}
