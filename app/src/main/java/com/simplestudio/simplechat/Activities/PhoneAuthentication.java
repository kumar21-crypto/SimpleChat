package com.simplestudio.simplechat.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;
import com.simplestudio.simplechat.R;

public class PhoneAuthentication extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText editPhone,editOTP;
    private Button getOTP,verifyOTP;
    private String verficationID;
    private CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);

        auth = FirebaseAuth.getInstance();

        editPhone = findViewById(R.id.phoneBox);
        getOTP = findViewById(R.id.getOtp);
        countryCodePicker = findViewById(R.id.ccp);
        countryCodePicker.registerCarrierNumberEditText(editPhone);


        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editPhone.getText().toString().isEmpty())
                {
                    Toast.makeText(PhoneAuthentication.this, "please fill mobile number", Toast.LENGTH_SHORT).show();
                }
                else {
                    String phone = countryCodePicker.getFullNumberWithPlus().trim();
                    Intent intent = new Intent(PhoneAuthentication.this, OtpAuthentication.class);
                    intent.putExtra("mobile",phone);
                    startActivity(intent);
                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null)
        {
            startActivity(new Intent(PhoneAuthentication.this, MainActivity.class));
            finish();
        }
    }
}