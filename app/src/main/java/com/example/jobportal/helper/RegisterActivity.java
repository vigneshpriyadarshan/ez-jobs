package com.example.jobportal.helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobportal.R;
import com.example.jobportal.ui.login.LoginActivity;
import com.example.jobportal.vo.users;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText fname, lname, email, pwd, phno;
    Button regInsert;
    FirebaseDatabase database;
    RadioButton empy;
    RadioButton empr;
    DatabaseReference myRef;
    com.example.jobportal.vo.users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fname = (EditText) findViewById(R.id.editText);
        lname = (EditText) findViewById(R.id.editText2);
        email = (EditText) findViewById(R.id.editText5);
        pwd = (EditText) findViewById(R.id.editText4);
        phno = (EditText) findViewById(R.id.editText6);
        empy = (RadioButton) findViewById(R.id.radio_employee);
        empr = (RadioButton) findViewById(R.id.radio_employer);
        regInsert = (Button) findViewById(R.id.button);
        users = new users();
        myRef = database.getInstance().getReference().child("users");

        regInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int phoneNum = Integer.parseInt(phno.getText().toString().trim());
                String firstname = fname.getText().toString().trim();
                String lastname = lname.getText().toString().trim();
                String e_mail = email.getText().toString().trim();
                String password = pwd.getText().toString().trim();
                e_mail = e_mail.replaceAll("\\.", ",");
                users.setEmail(e_mail);
                users.setFirstname(firstname);
                users.setLastname(lastname);
                users.setPassword(password);
                users.setPhonenumber(phoneNum);
                if (empy.isChecked()) {
                    users.setEmployee(true);
                }
                if (empr.isChecked()) {
                    users.setEmployee(false);
                }
                myRef.child(e_mail).setValue(users);

                Toast.makeText(RegisterActivity.this, "data Inserted successfully", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(myIntent);
            }
        });
    }
}
