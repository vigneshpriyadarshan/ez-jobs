package com.example.jobportal.employee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jobportal.R;
import com.example.jobportal.vo.ApplicantDetail;
import com.example.jobportal.vo.AppliedJobs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;


public class ApplyJob extends AppCompatActivity {

    DatabaseReference myRef;
    DatabaseReference userRef;
    String jobTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final String jobId = intent.getExtras().get("JobId").toString();
        final String username = intent.getExtras().getString("username");

        setContentView(R.layout.activity_apply_job);

        final StringBuffer stringBuf = new StringBuffer();
        myRef = FirebaseDatabase.getInstance().getReference().child("Jobs");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    jobTitle = ds.child("jobTitle").getValue().toString();
                    ((TextView) findViewById(R.id.textViewJobDescription)).setText(stringBuf);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Button btnApplyJob = (Button) findViewById(R.id.btnApplyJob);
        btnApplyJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef = FirebaseDatabase.getInstance().getReference().child("users");
                myRef = FirebaseDatabase.getInstance().getReference();
                final ApplicantDetail ad = new ApplicantDetail();
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(username)) {
                            DataSnapshot ds = dataSnapshot.child(username);
                            String contact = ds.child("phonenumber").getValue().toString();
                            String fName = ds.child("firstname").getValue().toString();
                            String lName = ds.child("lastname").getValue().toString();
                            ad.setContact(contact);
                            ad.setJobStatus("open");
                            ad.setFirstName(fName);
                            ad.setLastName(lName);

                            ad.setDateTime(ServerValue.TIMESTAMP);
                            if (ad.getContact() != null && ad.getJobStatus() != null && !ad.getContact().trim().isEmpty() && !ad.getJobStatus().trim().isEmpty() && ad.getFirstName() != null && !ad.getFirstName().trim().isEmpty() && ad.getLastName() != null && !ad.getLastName().trim().isEmpty()) {
                                myRef.child("Applicants").child(jobId).child(username).setValue(ad);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                try {
                    //To check and push Applicant details into Applicants table
                    //Adding record to AppliedJobs table
                    AppliedJobs aj = new AppliedJobs();
                    aj.setJobId(jobId);
                    aj.setJobTitle(jobTitle);
                    aj.setJobStatus("applied");
                    myRef.child("AppliedJobs").child(username).child(jobId).setValue(aj);

                    Toast.makeText(getApplicationContext(), "Job Applied Successfully", Toast.LENGTH_LONG).show();
                    finish();
                } catch (Exception ex) {
                    Log.e("Apply for a Job", "Exception while applying for the job :  " + ex.getMessage());
                    Toast.makeText(getApplicationContext(), "Unable to apply. Please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
