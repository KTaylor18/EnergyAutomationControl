package com.ntu.energyautomationcontrol;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //help from https://medium.com/@peterekeneeze/add-firebase-authentication-to-your-app-in-7minutes-c13df58994bd
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private Button butlogin;
    private Button butreg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText)findViewById(R.id.LoginEmail);
        password = (EditText)findViewById(R.id.LoginPassword);
        butlogin = (Button)findViewById(R.id.loginbutton);
        butreg = (Button)findViewById(R.id.registerbutton);
        mAuth = FirebaseAuth.getInstance();

        regbutaction();
        logbutaction();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent checklog = new Intent(MainActivity.this, TempActivity.class);
            startActivity(checklog);
        }
    }

    public void reguser(){
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        if(TextUtils.isEmpty(Email)){
            Toast.makeText(this, "Email field is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(Password)){
            Toast.makeText(this, "Password field is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try{
                            if(task.isSuccessful()){

                                Toast.makeText(MainActivity.this, "Registration Successful",
                                        Toast.LENGTH_SHORT).show();
                                finish();

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference();
                                final String UID = mAuth.getUid();
                                myRef.child("users").child(UID).child("ct").setValue(25);
                                myRef.child("users").child(UID).child("tt").setValue(25);
                                //https://firebase.google.com/docs/auth/android/manage-users
                                String userEmail = mAuth.getCurrentUser().getEmail();
                                myRef.child("users").child(UID).child("email").setValue(userEmail);

                                startActivity(new Intent(getApplicationContext(), TempActivity.class));
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Registration Failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception err){
                            err.printStackTrace();
                        }
                    }
                });
    }

    public void loguser(){
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();
        if(TextUtils.isEmpty(Email)){
            Toast.makeText(this, "Email field is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(Password)){
            Toast.makeText(this, "Password field is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try{
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Login Successful",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), TempActivity.class));
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Login Failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception err){
                            err.printStackTrace();
                        }
                    }
                });
    }

    public void regbutaction(){
        butreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reguser();
            }
        });
    }

    public void logbutaction(){
        butlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loguser();
            }
        });
    }
}
