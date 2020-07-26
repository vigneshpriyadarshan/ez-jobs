package com.example.jobportal.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jobportal.R;
import com.example.jobportal.employee.EmployeeHome;
import com.example.jobportal.employer.EmployerHome;
import com.example.jobportal.helper.RegisterActivity;
import com.example.jobportal.vo.users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private String register = "Register here";
    EditText uname;
    EditText pwd;
    Button loginButton;
    users users;
    DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        uname = (EditText) findViewById(R.id.username);
        pwd = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login);
    }

    public void loginButton(View V) {
        final String valueUserName = uname.getText().toString().replace(".", ",");
        final String valPassword = pwd.getText().toString();
        myRef = FirebaseDatabase.getInstance().getReference().child("users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(valueUserName)) {
                    DataSnapshot snapShot = dataSnapshot.child(valueUserName);
                    String temp = snapShot.child("password").getValue().toString();
                    if (valPassword.equals(temp)) {
                        String flag = snapShot.child("employee").getValue().toString();
                        if (flag.equals("true")) {
                            Intent myIntent = new Intent(getBaseContext(), EmployeeHome.class);
                            myIntent.putExtra("userNameIntent", valueUserName);
                            startActivity(myIntent);
                        } else {
                            Intent myIntent = new Intent(getBaseContext(), EmployerHome.class);
                            myIntent.putExtra("userNameIntent", valueUserName);
                            startActivity(myIntent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Password Incorrect", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please register", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void registerActivity(View V) {
        Intent myIntent = new Intent(getBaseContext(), RegisterActivity.class);
        startActivity(myIntent);
    }
}
