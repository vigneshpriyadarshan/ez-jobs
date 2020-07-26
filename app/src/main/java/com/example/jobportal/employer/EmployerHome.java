package com.example.jobportal.employer;

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
import com.example.jobportal.helper.ExpandableListData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmployerHome extends AppCompatActivity {
    ArrayList<String> array;
    ArrayAdapter<String> adapter;
    ListView lv;
    DatabaseReference myRef;
    public static String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_home);

        final String getUserName = getIntent().getStringExtra("userNameIntent");
        array = new ArrayList<String>();
        lv = (ListView) findViewById(R.id.fragment);
        adapter = new ArrayAdapter<String>(EmployerHome.this, android.R.layout.simple_list_item_1, array);

        lv.setAdapter(adapter);
        loadAppliedJobs(getUserName);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                String jobId = item.split("-")[0].trim();
                key = jobId;
                ApplicantsList.jobId = jobId;
                ExpandableListData.getData();

                Intent intent = new Intent(EmployerHome.this, EmployerJobView.class);
                intent.putExtra("JobId", jobId);
                startActivity(intent);
            }
        });

        Button btnPostJob = (Button) findViewById(R.id.btnPostJob);
        btnPostJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployerHome.this, PostJob.class);
                intent.putExtra("username", getUserName);
                startActivity(intent);
            }
        });
    }

//    @Override
//    public void onJobSelected(int jobId) {
//        Intent intent = new Intent(this,EmployerJobView.class);
//        intent.putExtra("JobId",jobId);
//        startActivity(intent);
//    }

    public void loadAppliedJobs(String username) {
        final String uname = username.replace(".", ",");
        myRef = FirebaseDatabase.getInstance().getReference().child("EmployerJobs");
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
