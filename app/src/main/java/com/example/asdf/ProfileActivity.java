package com.example.asdf;

import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private TextView username;
    private TextView email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setEnterTransition(new Explode());


        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);

        firestore.collection("Users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).get()
                .addOnSuccessListener(doc -> {
                    username.setText("Felhasználó név: " + doc.getString("username"));
                    email.setText("Email: " + doc.getString("email"));
                });
    }

    public void logout(View view) {
        auth.signOut();
        Toast.makeText(this, "Kijelentkezve", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void delete(View view) {
        firestore.collection("Users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).delete();
        firestore.collection("Carts").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).delete();
        auth.getCurrentUser().delete();
        auth.signOut();
        Toast.makeText(this, "Fiók törölve", Toast.LENGTH_LONG).show();
        finish();
    }
}
