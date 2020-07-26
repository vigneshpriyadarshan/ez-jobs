package com.example.jobportal.employee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jobportal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmployeeHome extends AppCompatActivity {

    ArrayList<String> array;
    ArrayAdapter<String> adapter;
    ListView lv;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobshome);

        final String username = getIntent().getExtras().getString("userNameIntent");
        array = new ArrayList<String>();
        lv = (ListView) findViewById(R.id.jobsListView);
        adapter = new ArrayAdapter<String>(EmployeeHome.this, android.R.layout.simple_list_item_1, array);

        lv.setAdapter(adapter);
        loadAppliedJobs(username);


        Button btnFindJob = findViewById(R.id.btnFindJob);
        btnFindJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeHome.this, FindJob.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                String jobId = item.split("-")[0].trim();
                Intent intent = new Intent(EmployeeHome.this, ApplicantJobView.class);
                intent.putExtra("JobId", jobId);
                startActivity(intent);
            }
        });
    }

    public void loadAppliedJobs(String username) {
        final String uname = username;
        myRef = FirebaseDatabase.getInstance().getReference().child("AppliedJobs");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uname)) {
                    DataSnapshot ds = dataSnapshot.child(uname);
                    for (DataSnapshot snapShots : ds.getChildren()) {
                        StringBuilder sb = new StringBuilder();
                        String tempOne = snapShots.getKey();
                        String tempTwo = snapShots.child("jobTitle").getValue().toString();
                        sb.append(tempOne + " - " + tempTwo);
                        array.add(sb.toString());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
