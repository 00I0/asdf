package com.example.asdf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;

    private FirebaseAuth auth;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        auth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences(Objects.requireNonNull(getClass().getPackage()).toString(), MODE_PRIVATE);
    }

    public void signup(View view) {
        startActivity(new Intent(this, SignupActivity.class));
    }

    public void login(View view) {
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(this, ShoppingActivity.class));
                return;
            }

            Toast.makeText(this, "Nem sikerült bejelentkezni: " + task.getException().getMessage(), Toast.LENGTH_LONG)
                    .show();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", email.getText().toString());
        editor.putString("password", password.getText().toString());
        editor.apply();
    }
}