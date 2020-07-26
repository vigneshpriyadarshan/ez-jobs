package com.example.jobportal.employer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jobportal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class EmployerJobView extends AppCompatActivity {
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final String jobId = intent.getExtras().get("JobId").toString();
        setContentView(R.layout.activity_employer_job_view);
        final StringBuffer stringBuf = new StringBuffer();
        myRef = FirebaseDatabase.getInstance().getReference().child("Jobs");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(jobId)) {
                    DataSnapshot ds = dataSnapshot.child(jobId);

                    stringBuf.append("Job Title : " + ds.child("jobTitle").getValue().toString() + " \n");
                    stringBuf.append("Description : " + ds.child("jobDescription").getValue().toString() + "\n");
                    stringBuf.append("Category : " + ds.child("jobCategory").getValue().toString() + " \n");
                    stringBuf.append("Job Location : " + ds.child("jobCity").getValue().toString() + " , " + ds.child("jobState").getValue().toString() + " \n");
                    stringBuf.append("Salary Expected : " + ds.child("jobSalary").getValue().toString() + " \n");
                    if (ds.child("jobRequirements") != null && ds.child("jobRequirements").getValue().toString().trim().length() > 0) {
                        stringBuf.append("Requirement : " + ds.child("jobRequirements").getValue().toString() + "\n");
                    }
                    ((TextView) findViewById(R.id.textViewJobDescription)).setText(stringBuf);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Button btnViewApplicants = (Button) findViewById(R.id.btnViewApplicants);
        btnViewApplicants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployerJobView.this, ApplicantsList.class);
                startActivity(intent);
            }
        });
    }
}
