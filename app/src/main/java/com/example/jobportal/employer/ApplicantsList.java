package com.example.jobportal.employer;

import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jobportal.R;
import com.example.jobportal.helper.CustomExpandableListAdapter;
import com.example.jobportal.helper.ExpandableListData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ApplicantsList extends AppCompatActivity {
    DatabaseReference myRef;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    public static List<String> expandableListTitle;
    public static String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants_list);
        expandableListTitle = new ArrayList<String>();
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        myRef = FirebaseDatabase.getInstance().getReference().child("Applicants");
        myRef.orderByChild("dateTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(jobId)) {
                    DataSnapshot snapShot = dataSnapshot.child(jobId);
                    for (DataSnapshot ss : snapShot.getChildren()) {
                        if (ss.child("firstName") != null && ss.child("lastName") != null && ss.child("firstName").getValue().toString().length() > 0 && ss.child("lastName").getValue().toString().length() > 0) {
                            String name = ss.child("firstName").getValue().toString() + " " + ss.child("lastName").getValue().toString();
                            expandableListTitle.add(name);
                        } else {
                            expandableListTitle.add(ss.getKey());
                        }
                    }
                    load();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void load() {
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, ExpandableListData.mp);
        expandableListView.setAdapter(expandableListAdapter);
    }
}
