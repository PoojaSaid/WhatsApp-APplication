package com.example.mywhatsappapllication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

//    private FirebaseUser currentUser;
    private Button loginButton, phoneButton;
    private EditText userEmail, userPassword;
    private TextView needNewAccountLink, forgetPasswordLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InitializeFields();
        mAuth = FirebaseAuth.getInstance();

        //Get current user
//        currentUser = mAuth.getCurrentUser();
        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowUserToLogin();
            }
        });
    }

    private void allowUserToLogin() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email..........", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password..........", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait.........");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "Logged in successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                String errorMessage = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void InitializeFields() {
        loginButton = findViewById(R.id.login_button);
        phoneButton = findViewById(R.id.phone_login_button);
        userEmail = findViewById(R.id.login_email);
        userPassword = findViewById(R.id.login_password);
        needNewAccountLink = findViewById(R.id.need_new_account);
        forgetPasswordLink = findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);


    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (currentUser != null) {
//            sendUserToMainActivity();
//        }
//    }

    private void sendUserToMainActivity() {
        Intent registerToMainIntent = new Intent(LoginActivity.this, MainActivity.class);

        //not go back to login screen after successful login
        registerToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerToMainIntent);
        finish();
    }


    private void sendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}