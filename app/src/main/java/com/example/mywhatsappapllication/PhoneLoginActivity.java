package com.example.mywhatsappapllication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private Button Btn_sendVerificationCode, Btn_verify;
    private EditText inputPhoneNumber, inputVerificationCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        Btn_sendVerificationCode = findViewById(R.id.send_ver_code_button);
        Btn_verify = findViewById(R.id.verify_button);
        inputPhoneNumber = findViewById(R.id.phone_number_input);
        inputVerificationCode = findViewById(R.id.verification_code_inputs);
        mAuth = FirebaseAuth.getInstance();

        Btn_sendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNum = inputPhoneNumber.getText().toString();
                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(PhoneLoginActivity.this, "Phone number is required.", Toast.LENGTH_SHORT).show();
                } else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNum,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);
                }
            }
        });

        Btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Btn_sendVerificationCode.setVisibility(View.INVISIBLE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);
                Btn_verify.setVisibility(View.INVISIBLE);
                inputVerificationCode.setVisibility(View.INVISIBLE);
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneLoginActivity.this, "Invalis, Please enter correct phone number with your country code.", Toast.LENGTH_SHORT).show();

                Btn_sendVerificationCode.setVisibility(View.VISIBLE);
                inputPhoneNumber.setVisibility(View.VISIBLE);
                Btn_verify.setVisibility(View.INVISIBLE);
                inputVerificationCode.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                Toast.makeText(PhoneLoginActivity.this, "Code has been changed, Please check it.", Toast.LENGTH_SHORT).show();
                //Code visibility
                Btn_sendVerificationCode.setVisibility(View.INVISIBLE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);
                Btn_verify.setVisibility(View.VISIBLE);
                inputVerificationCode.setVisibility(View.VISIBLE);

            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                        } else {

                        }

                    }
                });
    }
}