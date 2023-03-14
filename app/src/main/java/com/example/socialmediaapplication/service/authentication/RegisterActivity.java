package com.example.socialmediaapplication.service.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialmediaapplication.service.board.DashboardActivity;
import com.example.socialmediaapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;


/**
 * Created by LittleDuck
 * Name of project: SocialMediaApplication
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText email, password, name;
    private Button mRegister;
    private TextView existAccount;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();

        Objects.requireNonNull(actionBar).setTitle("Create Account");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        email = findViewById(R.id.register_email);
        name = findViewById(R.id.register_name);
        password = findViewById(R.id.register_password);
        mRegister = findViewById(R.id.register_button);
        existAccount = findViewById(R.id.homepage);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Register");

        mRegister.setOnClickListener(v -> {

            getUserCredential();
        });
        existAccount.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    private void getUserCredential() {
        String mail = email.getText().toString().trim();
        String uname = name.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError("Invalid Email");
            email.setFocusable(true);
        } else if (pass.length() < 6) {
            password.setError("Length Must be greater than 6 character");
            password.setFocusable(true);
        } else {
            registerUser(mail, pass, uname);
        }
    }

    private void registerUser(String mail, final String password, final String userName) {

        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                progressDialog.dismiss();
                FirebaseUser user = mAuth.getCurrentUser();
                String email = Objects.requireNonNull(user).getEmail();
                String userID = user.getUid();

                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("email", email);
                hashMap.put("uid", userID);
                hashMap.put("name", userName);
                hashMap.put("onlineStatus", "online");
                hashMap.put("typingTo", "noOne");
                hashMap.put("image", "");

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("Users");

                reference.child(userID).setValue(hashMap);
                Toast.makeText(RegisterActivity.this, "Registered User " + user.getEmail(), Toast.LENGTH_LONG).show();

                Intent mainIntent = new Intent(RegisterActivity.this, DashboardActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();

            } else {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
