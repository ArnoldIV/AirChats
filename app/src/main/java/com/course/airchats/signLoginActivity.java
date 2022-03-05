package com.course.airchats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signLoginActivity extends AppCompatActivity {

    private static final String TAG = "signLoginActivity";

    private FirebaseAuth auth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private EditText nameEditText;
    private TextView toggleLoginTextView;
    private Button loginSignUpButton;

    private boolean loginModeActive;

    private FirebaseDatabase database;
    private DatabaseReference usersDatabaseReferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_login);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://airchats-ae1fa-default-rtdb.firebaseio.com/");
        //creating a root in database with the name "users"
        usersDatabaseReferences = database.getReference().child("users");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        toggleLoginTextView = findViewById(R.id.toggleLoginSignUpTextView);
        loginSignUpButton = findViewById(R.id.loginSignUpButton);

        loginSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if button clicked,user is created
                loginSignUpUser(emailEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim());
            }
        });
        /*if the user is logged in, the user will be redirected to the chat activity,
         bypassing signLoginActivity
         */
        if (auth.getCurrentUser() !=null){
            startActivity(new Intent(signLoginActivity.this, UserListActivity.class));
        }
    }

    private void loginSignUpUser(String email,String password) {

        if (loginModeActive) {
            if (emailEditText.getText().toString().trim().equals("")){
                Toast.makeText(this,"Please,input your email",Toast.LENGTH_SHORT).show();
            }else if (passwordEditText.getText().toString().trim().length()<5) {
                Toast.makeText(this,"Passwords must be at least 6 characters",Toast.LENGTH_SHORT).show();
            }else{
                //sign in method
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    //   updateUI(user);
                                    Intent intent = new Intent
                                            (signLoginActivity.this,  UserListActivity.class);
                                    intent.putExtra("userName",nameEditText.getText().toString().trim());
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(signLoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //   updateUI(null);
                                }
                            }
                        });
            }
        } else {
            //password matching check,checking of email  and length of password
            if (!passwordEditText.getText().toString().trim().
                    equals(repeatPasswordEditText.getText().toString().trim())){
                Toast.makeText(this,"Passwords doesn't match",Toast.LENGTH_SHORT).show();
            }else if (emailEditText.getText().toString().trim().equals("")){
                Toast.makeText(this,"Please,input your email",Toast.LENGTH_SHORT).show();
            }else if (passwordEditText.getText().toString().trim().length()<5) {
                Toast.makeText
                        (this,"Passwords must be at least 6 characters",
                                Toast.LENGTH_SHORT).show();
            } else {
                //this methods create user with Email and passwords
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    createUser(user);
                                    // updateUI(user);
                                    // creating intent for redirecting user and passing user name into chat
                                    Intent intent = new Intent
                                            (signLoginActivity.this,  UserListActivity.class);
                                    intent.putExtra("userName",nameEditText.getText().toString().trim());
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(signLoginActivity.this,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    // updateUI(null);
                                }
                            }
                        });
            }
        }
    }
    //methods for creating user
    private void createUser(FirebaseUser firebaseUser) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setName(nameEditText.getText().toString().trim());

        usersDatabaseReferences.push().setValue(user);
    }

    //change text in textView and registration buttons during registration and login
    public void toggleLoginMode(View view) {
        if (loginModeActive){
            loginModeActive = false;
            loginSignUpButton.setText("Sing up");
            toggleLoginTextView.setText("Or, log in");
            repeatPasswordEditText.setVisibility(View.VISIBLE);
        }else {
            loginModeActive = true;
            loginSignUpButton.setText("Log in");
            toggleLoginTextView.setText("Or, sing up");
            repeatPasswordEditText.setVisibility(View.GONE);
        }
    }
}