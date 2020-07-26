package com.example.jobportal.helper;

import androidx.annotation.NonNull;

import com.example.jobportal.employer.EmployerHome;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class ExpandableListData {
    public static final LinkedHashMap<String, List<String>> mp = new LinkedHashMap<>();

    public static void getData() {
        DatabaseReference myRef;
        final String jobId = EmployerHome.key;
        myRef = FirebaseDatabase.getInstance().getReference().child("Applicants");
        myRef.orderByChild("dateTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(jobId)) {
                    DataSnapshot snapShot = dataSnapshot.child(jobId);
                    for (DataSnapshot ss : snapShot.getChildren()) {
                        if (ss.child("firstName") != null && ss.child("lastName") != null && ss.child("firstName").getValue().toString().length() > 0 && ss.child("lastName").getValue().toString().length() > 0) {
                            String name = ss.child("firstName").getValue().toString() + " " + ss.child("lastName").getValue().toString();
                            mp.put(name, new ArrayList<String>(Arrays.asList("Contact : " + ss.child("contact").getValue().toString() + " \n" + "Email : " + ss.getKey().replaceAll(",", "."))));
                        } else {
                            mp.put(ss.getKey(), new ArrayList<String>(Arrays.asList("Contact : " + ss.child("contact").getValue().toString() + " \n" + "Email : " + ss.getKey().replaceAll(",", "."))));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
