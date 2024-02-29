package com.example.payment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    private EditText email,password;
    private TextView register;
    private Button btnlogin;
    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.login_email);
        password=findViewById(R.id.login_password);
        btnlogin = findViewById(R.id.login_button);
        register=findViewById(R.id.register);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();
                if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Login user
                mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        // Fetch user's phone number from Firestore using their UID
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("users").document(user.getUid())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful() && task.getResult() != null) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                String userPhoneNumber = document.getString("phoneNumber");
                                                                String uid = user.getUid();
                                                                Log.d(TAG, "User's phone number: " + userPhoneNumber);
                                                                Log.d(TAG, "User's Id: " + uid);
                                                                //globale variable
                                                                Suid suid = com.example.payment.Suid.getInstance();
                                                                suid.setData(uid);
                                                                // Start the HomeActivity with the user's phone number
                                                                Intent in = new Intent(MainActivity.this, HomeActivity.class);
                                                               // in.putExtra("number", userPhoneNumber);
                                                                in.putExtra("uid", uid);
                                                                startActivity(in);
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    // Handle login failure
                                    Toast.makeText(MainActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(in);
                finish();
            }
        });


    }


}