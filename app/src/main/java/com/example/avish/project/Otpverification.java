package com.example.avish.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Otpverification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        Button verifybtn = findViewById(R.id.verifybtn);
        final EditText otpcode = findViewById(R.id.otpinput);
        verifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* OTP Verification Start */
                final int otp = Integer.parseInt(otpcode.getText().toString());
                final int code = Integer.parseInt(getIntent().getStringExtra("otpcode"));
                final String phonenumber = getIntent().getStringExtra("phonenumber");
                Log.d("codes value", otp + " " + code + " " + phonenumber);
                if(otp == code){
                    Intent UserDetailactivityintent = new Intent(Otpverification.this, UserFormActivity.class);
                    Log.d("phonenumber", phonenumber);
                    UserDetailactivityintent.putExtra("phonenumber", phonenumber);
                    startActivity(UserDetailactivityintent);
                    Log.d("Verification message ", "verified");
                }
                else{
                    Log.d("Verification message ", "Not Verified");
                }
                /* OTP Verification end */
            }
        });

    }
}
