package com.example.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    ImageView scanner, receive, add, send,profile;
    ImageButton blupdate;
    TextView balance, name;
    FirebaseFirestore db;


    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private ViewAnimator viewAnimator;
    private boolean isView1Visible = true;
    private ImageView profileImageView;
    private TextView nameTextView;
    private ListenerRegistration userListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        scanner = findViewById(R.id.Scanner);
        receive = findViewById(R.id.receive);
        balance = findViewById(R.id.balance);
        blupdate = findViewById(R.id.update);
        add = findViewById(R.id.addbtn);
        send = findViewById(R.id.send);
        db = FirebaseFirestore.getInstance();

        nameTextView = findViewById(R.id.userName);
        profileImageView = findViewById(R.id.imageView);

        //
        viewAnimator = findViewById(R.id.viewAnimator);
        // Set a slide-in and slide-out animation
        viewAnimator.setInAnimation(this, R.anim.slide_in_left);
        viewAnimator.setOutAnimation(this, R.anim.slide_out_right);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList, this);
        recyclerView.setAdapter(transactionAdapter);

        //Intent intent = getIntent();
        // String number = intent.getStringExtra("number");
        //  String suid1 = intent.getStringExtra("uid");

        //update
        Suid suid = com.example.payment.Suid.getInstance();
        String uid = suid.getData();
        fetchAndDisplayBalance(uid);
        //retrieveLastTransactions(uid);
        fetchAndDisplayTransactions(uid,true);
        //name and photo
        fetchUserData(uid);
        viewAnimator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isView1Visible) {
                    fetchAndDisplayTransactions(uid, false); // Change content for View 1
                    viewAnimator.showNext();
                } else {
                    fetchAndDisplayTransactions(uid,true); // Change content for View 2
                    viewAnimator.showPrevious();
                }
                isView1Visible = !isView1Visible;
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(HomeActivity.this,SettingsActivity.class);
                startActivity(in);
            }
        });
        blupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchAndDisplayBalance(uid);
            }
        });

        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(HomeActivity.this, ScannerActivity.class);
                in.putExtra("suid", uid);
                startActivity(in);
            }
        });
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(HomeActivity.this, QrcodeActivity.class);
                //in.putExtra("number", number);
                startActivity(in);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(HomeActivity.this, AddmoneyActivity.class);
                startActivity(in);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(HomeActivity.this, SendActivity.class);
                startActivity(in);
            }
        });

    }

    private void fetchAndDisplayBalance(String uid) {
        // Reference to the user's document in Firestore
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Get the balance field from the user's document
                            Long balanceValue = document.getLong("balance");

                            if (balanceValue != null) {
                                // Display the balance in the TextView
                                balance.setText("₹" + balanceValue.toString());
                            } else {
                                // Handle the case where 'balance' is not present or is null
                            }
                        } else {
                            // Handle the case where the user's document does not exist
                        }
                    } else {
                        // Handle exceptions while trying to get the document
                        Exception exception = task.getException();
                        if (exception != null) {
                            // Handle exceptions here
                        }
                    }
                });
    }
    private void fetchAndDisplayTransactions(String userUid, boolean isSender) {
        String fieldName;
        if(isSender == true)
        {
             fieldName = "senderUid";
        }
        else
        {
            fieldName="receiverUid";
        }

        db.collection("transactions")
                .whereEqualTo(fieldName, userUid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Transaction> transactionList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                double amount = document.getDouble("amount");
                                String receiverUid = document.getString("receiverUid");
                                String senderUid = document.getString("senderUid");
                                long timestamp = document.getTimestamp("timestamp").getSeconds();

                                // Fetch sender's and receiver's names

                                fetchUserData(senderUid, new UserDataCallback() {
                                    @Override
                                    public void onUserDataFetched(String senderName, String senderProfileImg) {
                                        fetchUserData(receiverUid, new UserDataCallback() {
                                            @Override
                                            public void onUserDataFetched(String receiverName, String receiverProfileImg) {
                                                Transaction transaction = new Transaction(amount, senderName, receiverName, timestamp, fieldName, receiverProfileImg,senderProfileImg);
                                                transactionList.add(transaction);

                                                if (transactionList.size() == task.getResult().size()) {
                                                    // All transactions are fetched, display them
                                                    displayTransactions(transactionList);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        } else {
                            // Handle errors while fetching data from Firestore
                        }
                    }
                });
    }

    private void fetchUserData(String uid, UserDataCallback callback) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String userName = document.getString("name");
                                String profileImg = document.getString("Profile_img");
                                callback.onUserDataFetched(userName, profileImg);
                            } else {
                                // Handle the case where the user document does not exist
                            }
                        } else {
                            // Handle errors while fetching user data
                        }
                    }
                });
    }

    // Define a callback interface for fetching user names
    private interface UserDataCallback {
        void onUserDataFetched(String userName, String profileImg);
    }

//logic
  /*  private void fetchSendDisplayTransactions(String userUid) {
        db.collection("transactions")
                .whereEqualTo("senderUid", userUid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Transaction> transactionList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                double amount = document.getDouble("amount");
                                String receiverUid = document.getString("senderUid");
                                String senderUid = document.getString("senderUid");
                                long timestamp = document.getTimestamp("timestamp").getSeconds();



                                Transaction transaction = new Transaction(amount, receiverUid, senderUid, timestamp);
                                transactionList.add(transaction);
                            }
                            displayTransactions(transactionList);
                        } else {
                            // Handle errors while fetching data from Firestore

                        }
                    }
                });
    }



    private void fetchReceiverDisplayTransactions(String userUid) {
        db.collection("transactions")
                .whereEqualTo("receiverUid", userUid)
                //.whereEqualTo("senderUid", userUid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Transaction> transactionList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                double amount = document.getDouble("amount");
                                String receiverUid = document.getString("senderUid");
                                String senderUid = document.getString("senderUid");
                                long timestamp = document.getTimestamp("timestamp").getSeconds();

                                Transaction transaction = new Transaction(amount, receiverUid, senderUid, timestamp);
                                transactionList.add(transaction);
                            }
                            displayTransactions(transactionList);
                        } else {
                            // Handle errors while fetching data from Firestore

                        }
                    }
                });
    }

   */

    private void displayTransactions(List<Transaction> transactionList) {
        TransactionAdapter transactionAdapter = new TransactionAdapter(transactionList, this);
        recyclerView.setAdapter(transactionAdapter);
    }



    private void fetchUserData(String uid) {
        // You can directly pass the UID to this function

        userListener = db.collection("users")
                .document(uid)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        // Handle Firestore error
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String profileImageURL = documentSnapshot.getString("Profile_img");
                        String email = documentSnapshot.getString("email");

                        // Update the TextView with the retrieved name
                        nameTextView.setText(name);
                        Suid suid = com.example.payment.Suid.getInstance();
                        suid.setName(name);
                        suid.setEmail(email);
                        suid.setImgurl(profileImageURL);


                        // Load and display the profile image using Glide or another image-loading library
                        // Assuming you have a function loadProfileImage() to load the image
                        loadProfileImage(profileImageURL);
                    }
                });
    }



    private void loadProfileImage(String imageURL) {
        if (imageURL != null && !imageURL.isEmpty()) {
            // Use an image-loading library like Glide to load and display the image
            // Replace R.drawable.default_profile with your default placeholder image
            Glide.with(this)
                    .load(imageURL)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(profileImageView);
        } else {
            // If no image URL is available, you can set a default placeholder image here
            profileImageView.setImageResource(R.drawable.profile);
        }


    }
}