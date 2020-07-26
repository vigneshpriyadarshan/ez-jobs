package com.example.jobportal.employer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jobportal.R;
import com.example.jobportal.helper.MapsActivity;
import com.example.jobportal.vo.Jobs;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PostJob extends AppCompatActivity {
    Spinner spinner2;
    static final int OBTAIN_LOCATION = 1;
    TextView userCategory;
    String country = "", state = "", city = "";
    LatLng latlng = null;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String recentId = "-1";
    String uname = "";
    Jobs jobs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uname = getIntent().getExtras().getString("username");
        setContentView(R.layout.activity_post_job);

        //To list the available categories
        addItemsOnSpinner2();

        //Create a DB reference
        myRef = database.getInstance().getReference();

        //To check for Required fields
        final EditText jobTitle = findViewById(R.id.jobTitle);
        final EditText description = findViewById(R.id.description);
        final TextView category = findViewById(R.id.userCategory);
        final Button btnPostJob = (Button) findViewById(R.id.btnPostJob);
        final Button btnLocation = (Button) findViewById(R.id.btnLocation);
        final EditText salary = findViewById(R.id.salary);
        final EditText age = findViewById(R.id.age);
        final EditText experience = findViewById(R.id.experience);
        final EditText otherRequirements = findViewById(R.id.otherRequirements);

        //To get the most recent Job ID
        Query latestJobIdQuery = myRef.child("Jobs").orderByKey().limitToLast(1);
        latestJobIdQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                while (items.hasNext()) {
                    DataSnapshot item = items.next();
                    recentId = item.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnPostJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jobTitle.getText().toString().trim().isEmpty() || jobTitle.getText().toString() == null) {
                    jobTitle.setError("Job title is required");
                }
                if (description.getText().toString().trim().isEmpty() || description.getText().toString() == null) {
                    description.setError("Job description is required");
                }
                if (category.getText().toString().trim().isEmpty() || category.getText().toString() == null) {
                    category.setError("Select a Category from the above list");
                }
                if (salary.getText().toString().trim().isEmpty() || salary.getText().toString() == null) {
                    salary.setError("Salary detail is required");
                }
                if (!(validateLocationDetails())) {
                    Toast.makeText(getApplicationContext(), "Error in getting location. Please try again", Toast.LENGTH_LONG).show();
                }

                if (uname == null) {
                    Toast.makeText(getApplicationContext(), "Error in getting user details. Please try again", Toast.LENGTH_LONG).show();
                } else {
                    jobs = new Jobs();
                    jobs.setJobTitle(jobTitle.getText().toString());
                    jobs.setJobDescription(description.getText().toString());
                    jobs.setJobCategory(category.getText().toString());
                    jobs.setJobCity(city);
                    jobs.setJobCountry(country);
                    jobs.setJobState(state);
                    jobs.setJobLatitude(latlng.latitude);
                    jobs.setJobLongitude(latlng.longitude);
                    jobs.setJobSalary(salary.getText().toString());
                    jobs.setJobAgeReq((age.getText().toString()) == null ? "" : age.getText().toString());
                    jobs.setJobExpReq((experience.getText().toString()) == null ? "" : experience.getText().toString());
                    jobs.setJobRequirements((otherRequirements.getText().toString()) == null ? "" : otherRequirements.getText().toString());
                    jobs.setUname(uname);
                    try {
                        //To push the record into Jobs table
                        int currJobId = Integer.parseInt(recentId) + 1;
                        myRef.child("Jobs").child(String.valueOf(currJobId)).setValue(jobs);
                        //Toast.makeText(getApplicationContext(), "Job Posted Successfully", Toast.LENGTH_LONG).show();

                        //To push the Job posting into JobLocation
                        String location = jobs.getJobCategory() + "-" + jobs.getJobCity() + "-" + jobs.getJobState();
                        myRef.child("JobsLocation").child(location).child(String.valueOf(currJobId)).setValue(jobs);

                        //To push data into EmployerJobs
                        myRef.child("EmployerJobs").child(uname).child(String.valueOf(currJobId)).setValue(jobs);
                        Toast.makeText(getApplicationContext(), "Job Posted Successfully", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } catch (Exception e) {
                        Log.e("Post a Job", "Exception while posting a new job :  " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Unable to post job. Please try again", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        //Set the value of category
        Spinner categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cate = parent.getItemAtPosition(position).toString();
                userCategory = (TextView) findViewById(R.id.userCategory);
                userCategory.setText(cate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void addItemsOnSpinner2() {
        spinner2 = (Spinner) findViewById(R.id.categorySpinner);
        List<String> list = new ArrayList<String>();
        list.add("Dining");
        list.add("Home Maintanence");
        list.add("Travel");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1; // you dont display last item. It is used as hint.
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (String s : list) {
            dataAdapter.add(s);
        }
        dataAdapter.add("Select a category");
        spinner2.setAdapter(dataAdapter);
        spinner2.setSelection(dataAdapter.getCount());
    }


    public void getLocation(View v) {
        try {
            Intent intent = new Intent(this, MapsActivity.class);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, OBTAIN_LOCATION);
            }
        } catch (Exception ex) {
            String s = ex.getMessage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OBTAIN_LOCATION && resultCode == RESULT_OK) {
            if (data.hasExtra("city")) {
                city = data.getExtras().getString("city");
                if (city == null || city.length() == 0 || city.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Error in getting location. Please try again", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error in getting location. Please try again", Toast.LENGTH_LONG).show();
            }
            if (data.hasExtra("country")) {
                country = data.getExtras().getString("country");
                if (country == null || country.length() == 0 || country.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Error in getting location. Please try again", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error in getting location. Please try again", Toast.LENGTH_LONG).show();
            }
            if (data.hasExtra("state")) {
                state = data.getExtras().getString("state");
                if (state == null || state.length() == 0 || state.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Error in getting location. Please try again", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error in getting location. Please try again", Toast.LENGTH_LONG).show();
            }
            if (data.hasExtra("latlng")) {
                latlng = (LatLng) data.getExtras().get("latlng");
                if (latlng == null) {
                    Toast.makeText(getApplicationContext(), "Error in getting location. Please try again", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error in getting location. Please try again", Toast.LENGTH_LONG).show();
            }
            Log.i("Post Job", "Location values are : City - " + city + " ,country - " + country + " state - " + state + " lat : " + latlng.latitude + " long-" + latlng.longitude
            );
        }
    }

    private boolean validateLocationDetails() {
        if (city == null || city.length() == 0 || city.isEmpty() || country == null || country.length() == 0 || country.isEmpty() || state == null || state.length() == 0 || state.isEmpty() || latlng == null) {
            return false;
        }
        return true;
    }
}
