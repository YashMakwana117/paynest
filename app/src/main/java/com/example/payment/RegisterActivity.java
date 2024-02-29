package com.example.payment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.database.annotations.Nullable;



public class RegisterActivity extends AppCompatActivity {

    private EditText name,email,number,password;
    private TextView login,upload;
    private Button regbtn;
    private ImageView img;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name=findViewById(R.id.reg_uname);
        email=findViewById(R.id.reg_email);
        number=findViewById(R.id.reg_num);
        password=findViewById(R.id.reg_password);
        regbtn=findViewById(R.id.reg);
        login=findViewById(R.id.login);
        upload=findViewById(R.id.txt_upload);
        img=findViewById(R.id.profile_image);

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = name.getText().toString().trim();
                String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();
                String Number = number.getText().toString().trim();
                String userNumber = "+91" + Number;
                // Perform form validation
                if (TextUtils.isEmpty(userName) ||TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword) || TextUtils.isEmpty(userNumber)) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Intent data to next page
                if (imageUri!=null) {
                    Intent in = new Intent(RegisterActivity.this, otp_varify.class);
                    in.putExtra("name", userName);
                    in.putExtra("number", userNumber);
                    in.putExtra("email", userEmail);
                    in.putExtra("password", userPassword);
                    in.putExtra("img",  imageUri.toString());
                    startActivity(in);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
                }


            /*    // Register new user
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // User registered successfully
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        // Save additional user details to the Realtime Database
                                        String userId = user.getUid();
                                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                                        userRef.child("email").setValue(userEmail);
                                        userRef.child("phoneNumber").setValue(userNumber);
                                        Intent in = new Intent(RegisterActivity.this,MainActivity.class);
                                        startActivity(in);
                                        finish();
                                    }
                                } else {
                                    // Handle registration failure
                                    Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

               */
            }


        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in= new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(in);
                finish();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(RegisterActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }

        });

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                imageUri = data.getData(); // Update the imageUri field
                img.setImageURI(imageUri);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Image Selection is Failed", Toast.LENGTH_SHORT).show();
        }
    }




}
