package com.simplestudio.simplechat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.simplestudio.simplechat.R;

import java.util.concurrent.TimeUnit;

public class OtpAuthentication extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button verifyOTP;
    private String verficationID;
    private String phoneNumber;
    private TextView label;
    private EditText otpBox;
    public static Dialog dialog;
    public static Activity currentActivity = null;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_authentication);

        auth = FirebaseAuth.getInstance();

        otpBox = findViewById(R.id.editOTP);
        verifyOTP = findViewById(R.id.otpVerify);
        label = findViewById(R.id.otpStringLabel);
        phoneNumber = getIntent().getStringExtra("mobile");

        context = getApplicationContext();

        label.setText("enter the otp sent to "+phoneNumber);
        sendVerificationCode(phoneNumber);

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otpBox.getText().toString().isEmpty())
                {
                    Toast.makeText(OtpAuthentication.this, "please enter otp", Toast.LENGTH_SHORT).show();
                }
                else {
                    verifyCode(otpBox.getText().toString().trim());
                }
            }
        });
    }



    private void sendVerificationCode(String number) {

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack =

            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    final String code = phoneAuthCredential.getSmsCode();

                    if (code != null)
                    {
                        otpBox.setText(code);

                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {

                }

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);

                    verficationID = s;
                }
            };

    private void verifyCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verficationID, code);

        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(OtpAuthentication.this, "verfication otp successfully", Toast.LENGTH_SHORT).show();

                            //update ui
                            FirebaseUser user1 = task.getResult().getUser();
                            // user register time
                            long creationTime = user1.getMetadata().getCreationTimestamp();
                            // user last sign in time
                            long lastSignInTime = user1.getMetadata().getLastSignInTimestamp();
                            // timestamp string
                            String timeStamp;

                            if (creationTime == lastSignInTime)
                            {
                                timeStamp = "new";
                            }
                            else {
                                timeStamp = "exists";
                            }

                            Intent intent = new Intent(OtpAuthentication.this, ProfileInfo.class);
                                    intent.putExtra("time",timeStamp);
                                    startActivity(intent);
                                    finish();




                        }
                        else {
                            Toast.makeText(OtpAuthentication.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}