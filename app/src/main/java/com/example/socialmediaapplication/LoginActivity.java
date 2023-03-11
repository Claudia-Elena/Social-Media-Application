package com.example.socialmediaapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password, name;
    private Button mlogin;
    private TextView newAccount, recoverPassword;
    FirebaseUser currentUser;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        newAccount = findViewById(R.id.needs_new_account);
        recoverPassword = findViewById(R.id.forgetp);
        mAuth = FirebaseAuth.getInstance();
        mlogin = findViewById(R.id.login_button);
        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        mlogin.setOnClickListener(v -> {
            String emaill = email.getText().toString().trim();
            String pass = password.getText().toString().trim();

            // if format of email doesn't matches return null
            if (!Patterns.EMAIL_ADDRESS.matcher(emaill).matches()) {
                email.setError("Invalid Email");
                email.setFocusable(true);

            } else {
                loginUser(emaill, pass);
            }
        });

        newAccount.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));

        recoverPassword.setOnClickListener(v -> showRecoverPasswordDialog());
    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailet = new EditText(this);//write your registered email
        emailet.setText("Email");
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailet);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);
        builder.setPositiveButton("Recover", (dialog, which) -> {
            String emaill = emailet.getText().toString().trim();
            beginRecovery(emaill);//send a mail on the mail to recover password
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginRecovery(String emaill) {
        loadingBar.setMessage("Sending Email....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        mAuth.sendPasswordResetEmail(emaill).addOnCompleteListener(task -> {
            loadingBar.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Done sent", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginUser(String emaill, String pass) {
        loadingBar.setMessage("Logging In....");
        loadingBar.show();

        mAuth.signInWithEmailAndPassword(emaill, pass).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                loadingBar.dismiss();
                FirebaseUser user = mAuth.getCurrentUser();

                if (Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser()) {
                    String email = Objects.requireNonNull(user).getEmail();
                    String uid = user.getUid();
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("uid", uid);
                    hashMap.put("name", "");
                    hashMap.put("onlineStatus", "online");
                    hashMap.put("typingTo", "noOne");
                    hashMap.put("phone", "");
                    hashMap.put("image", "");
                    hashMap.put("cover", "");
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference reference = database.getReference("Users");

                    reference.child(uid).setValue(hashMap);
                }
                Toast.makeText(LoginActivity.this, "Registered User " + Objects.requireNonNull(user).getEmail(), Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            } else {
                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            loadingBar.dismiss();
            Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
