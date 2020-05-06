package com.example.avish.project;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private String verificationid;
    private FirebaseAuth mAuth;
    private ProgressBar progressbar;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* temperary intant Start */

        Intent tempintent = new Intent(LoginActivity.this, UserFormActivity.class);
        startActivity(tempintent);

        /* Temporary intant end */

        Button loginbtn = findViewById(R.id.loginbtn);
        final EditText phonenumber = findViewById(R.id.phonenumber);
        final GradientDrawable gd = new GradientDrawable();
        mAuth = FirebaseAuth.getInstance();
        gd.setColor(Color.WHITE);
        phonenumber.setBackground(gd);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number =  phonenumber.getText().toString();
                int numberlen = number.length();
                if(numberlen > 10 || numberlen < 10){
                    gd.setColor(Color.RED);
                    gd.setStroke(4,Color.RED);
                    phonenumber.setBackground(gd);
                }else{
                    gd.setColor(Color.WHITE);
                    phonenumber.setBackground(gd);
                    String Cwnumber = "+91"+number;
                    Log.d("Phone number ", Cwnumber);
                    SendVerificationCode(Cwnumber);
                }
            }
        });
    }

    /* Send code */
    private void SendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            VerifyCode(code);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.d("message" , e.toString());
        }
    };

    /* send Code End */

    /* Redirecting to otpverification Activity */
    private void VerifyCode(String code){
        Intent verficationintent = new Intent(LoginActivity.this, Otpverification.class);
        verficationintent.putExtra("code", code);
        startActivity(verficationintent);
    }

    /* Redirecting to otpverification Activity */
}
