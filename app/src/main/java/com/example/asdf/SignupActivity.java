package com.example.asdf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.asdf.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = SignupActivity.class.getName();
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText passwordConfirm;
    private ActivitySignupBinding binding;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        passwordConfirm = findViewById(R.id.editTextPasswordConfirm);

        preferences = getSharedPreferences(Objects.requireNonNull(getClass().getPackage()).toString(), MODE_PRIVATE);
        email.setText(preferences.getString("email", ""));
        password.setText(preferences.getString("password", ""));

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }


    public void cancel(View view) {
        finish();
    }

    public void signup(View view) {
        String name = this.name.getText().toString();
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        String passwordConfirm = this.passwordConfirm.getText().toString();


        if (name.replace("\\s+", "").length() < 2) {
            Toast.makeText(this, "A felhasználó névnek legalább 2 karakter hosszúnak kell lennie", Toast.LENGTH_SHORT)
                    .show();
            return;
        }


        if (!email.matches("^\\S+@\\S+\\.\\S+$")) {
            Toast.makeText(this, "A megadaott email nem érvényes", Toast.LENGTH_SHORT).show();
            return;
        }


        if (password.replace("\\s+", "").length() < 2) {
            Toast.makeText(this, "A jelszónak legalább 2 karakter hosszúnak kell lennie", Toast.LENGTH_SHORT)
                    .show();
            return;
        }


        if (!passwordConfirm.equals(password)) {
            Toast.makeText(this, "A jelszó és a megerősítése nem egyezeik meg.", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(this, "A regisztráció nem sikerült: "
                        + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                return;
            }


            CompletableFuture.runAsync(() -> {
                String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                firestore.collection("Users").document(uid).set(Map.of(
                        "email", email,
                        "id", uid,
                        "username", name
                ));
                firestore.collection("Carts").document(uid).set(Map.of(
                        "items", List.of(),
                        "userId", uid
                ));
            });

            startActivity(new Intent(this, ShoppingActivity.class));
        });
    }
}