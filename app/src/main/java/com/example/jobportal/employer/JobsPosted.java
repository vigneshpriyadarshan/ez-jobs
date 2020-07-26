package com.example.jobportal.employer;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JobsPosted {
    DatabaseReference insertRef;
    public static String[] postedJobs;

    public void loadJobs(String userName) {
        postedJobs = null;
        final String getUserName = userName.replace(".", ",");
        insertRef = FirebaseDatabase.getInstance().getReference().child("EmployerJobs");
        insertRef.addListenerForSingleValueEvent(new ValueEventListener() {
            int iterator = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(getUserName)) {
                    DataSnapshot ds = dataSnapshot.child(getUserName);
                    postedJobs = new String[(int) ds.getChildrenCount()];
                    for (DataSnapshot snapShots : ds.getChildren()) {
                        String tempOne = snapShots.getKey();
                        String tempTwo = snapShots.child("jobTitle").getValue().toString();
                        postedJobs[iterator] = tempOne + " - " + tempTwo;
                        iterator++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
