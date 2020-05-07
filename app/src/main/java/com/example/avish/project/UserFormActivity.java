package com.example.avish.project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserFormActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);
        final EditText Firstinp = findViewById(R.id.Firstnameinput);
        final EditText Lastinp = findViewById(R.id.Lastnameinput);
        final EditText emailinp = findViewById(R.id.emailinput);
        Button submitbtn = findViewById(R.id.submitdetails);
        ImageButton imageupload = findViewById(R.id.imageuploadbtn);
        Spinner genderselect = findViewById(R.id.genderselect);

        /* button Clicks Start */

        imageupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check runtime permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        // Permission not granted
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        // show popup for runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else{
                        pickImageFromGallery();
                    }
                }
                else{
                    pickImageFromGallery();
                }
            }
        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstname = Firstinp.getText().toString();
                String lastname = Lastinp.getText().toString();
                String email = emailinp.getText().toString();
                Submitdata(firstname, lastname, email);
            }


        });

        /* button Clicks end */
    }

    private void pickImageFromGallery() {
        //intent to pick image
        Intent imagepickintent = new Intent(Intent.ACTION_PICK);
        imagepickintent.setType("image/*");
        startActivityForResult(imagepickintent,IMAGE_PICK_CODE);
    }
    // Handle result of picked image

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            try {
                Resizeanduploadimage(data.getData());
                Storeimageinfirebase(data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /* Resize image Start */
    private void Resizeanduploadimage(Uri data) throws IOException {
        int width, height, newheight = 300, newwidth = 300;
        Bitmap OriginalImage, resizedBitmap;
        Matrix matrix;
        float scalewidth, scaleheight;
        ByteArrayOutputStream outputStream;
        OriginalImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data);
        width = OriginalImage.getWidth();
        height = OriginalImage.getHeight();
        Log.d("image height and width ", width + " " + height);
        matrix = new Matrix();
        scalewidth = ((float) newwidth)/width;
        scaleheight = ((float) newheight)/height;
        matrix.postScale(scalewidth,scaleheight);
        resizedBitmap = Bitmap.createBitmap(OriginalImage,0,0 ,width, height, matrix, true);
        outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ImageButton imageupload = findViewById(R.id.imageuploadbtn);
        imageupload.setImageBitmap(resizedBitmap);
        width = resizedBitmap.getWidth();
        height = resizedBitmap.getHeight();
        Log.d("resized height and width ", width + " " + height);
    }

    /* Resize image end */


    // Handle request of runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode){
           case PERMISSION_CODE:{
               if(grantResults.length > 0 && grantResults[0] ==
                       PackageManager.PERMISSION_GRANTED){
                   pickImageFromGallery();
               }else{
                   Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
               }
           }
       }
    }

    private void Storeimageinfirebase(Uri image){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference imagestorageref
                = storageReference
                .child(
                        "+917618426321/"
                                + UUID.randomUUID().toString());
        imagestorageref.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UserFormActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void Submitdata(String firstname, String lastname, String email){
        /* Initializing Firebase Database to store user information  Start */

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("+917618426321");

        /* Firebase Database initialisation end */

        if(firstname.length() == 0 && lastname.length() == 0 && email.length() == 0 ){
            Toast.makeText(this, "Invalid user details" , Toast.LENGTH_LONG).show();
        }else{
            myRef.child("Userdetails").child("Name").setValue(firstname + " " + lastname);
            myRef.child("Userdetails").child("Email").setValue(email);
            myRef.child("userhandle").child("Instagram").setValue("bansal_avish");
        }
    }
}
