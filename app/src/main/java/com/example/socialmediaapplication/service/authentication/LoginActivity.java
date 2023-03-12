package com.example.socialmediaapplication.service.authentication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;

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
        Objects.requireNonNull(actionBar).setTitle("Login into account");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        newAccount = findViewById(R.id.needs_new_account);
        recoverPassword = findViewById(R.id.forget_password);
        mAuth = FirebaseAuth.getInstance();
        mlogin = findViewById(R.id.login_button);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mlogin.setOnClickListener(v -> {
            getUserCredentials();
        });

        newAccount.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));

        recoverPassword.setOnClickListener(v -> showRecoverPasswordDialog());
    }

    private void getUserCredentials() {
        String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        // if format of email doesn't matches return null
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            email.setError("Invalid Email");
            email.setFocusable(true);

        } else {
            loginUser(mail, pass);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showRecoverPasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout = new LinearLayout(this);

        final EditText mailTextField = new EditText(this);//write your registered email
        mailTextField.setText("Email");
        mailTextField.setMinEms(16);
        mailTextField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        linearLayout.addView(mailTextField);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Recover Pass", (dialog, which) -> {
            String mail = mailTextField.getText().toString().trim();
            beginRecovery(mail);//send a mail message on the mail to recover the password
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void beginRecovery(String mail) {
        loadingBar.setMessage("Sending Email....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(task -> {
            loadingBar.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Done sent", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            loadingBar.dismiss();
            Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_LONG).show();
        });
    }

    private void loginUser(String mail, String password) {
        loadingBar.setMessage("Logging In....");
        loadingBar.show();

        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                loadingBar.dismiss();
                FirebaseUser user = mAuth.getCurrentUser();

                if (Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser()) {

                    String email = Objects.requireNonNull(user).getEmail();
                    String userId = user.getUid();

                    HashMap<Object, String> hashMapModel = new HashMap<>();
                    hashMapModel.put("email", email);
                    hashMapModel.put("uid", userId);
                    hashMapModel.put("name", "");
                    hashMapModel.put("onlineStatus", "online");
                    hashMapModel.put("typingTo", "noOne");
                    hashMapModel.put("phone", "");
                    hashMapModel.put("image", "");
                    hashMapModel.put("cover", "");

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Users");
                    reference.child(userId).setValue(hashMapModel);
                }

                Toast.makeText(LoginActivity.this, "Login User " + Objects.requireNonNull(user).getEmail(), Toast.LENGTH_LONG).show();
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
