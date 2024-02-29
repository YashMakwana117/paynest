package com.example.payment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SendActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch users from Firestore and set up the adapter
        if (recyclerView.getAdapter() == null) {
            // Fetch users from Firestore and set up the adapter only if it's not already set
            fetchUsers();
        }
    }

    private void fetchUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> userList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String uid = document.getId();
                    User user = document.toObject(User.class);
                    user.setUid(uid);

                    String name = document.getString("name");
                    String profileImg = document.getString("Profile_img");

                    // Set the fetched data to the User object
                    user.setName(name);
                    user.setPhotoUrl(profileImg);

                    userList.add(user);
                }

                // Initialize and set up the RecyclerView and adapter
                UserAdapter adapter = new UserAdapter(userList, uid -> {
                    // Handle item click here
                    // You can start a new activity with the selected user's UID
                    Intent in = new Intent(SendActivity.this,TransferActivity.class);
                    in.putExtra("uid",uid);
                    startActivity(in);
                    //Toast.makeText(this, "uid :" +uid, Toast.LENGTH_SHORT).show();
                },this);
                recyclerView.setAdapter(adapter);
            } else {
                // Handle the error
            }
        });
    }
}
