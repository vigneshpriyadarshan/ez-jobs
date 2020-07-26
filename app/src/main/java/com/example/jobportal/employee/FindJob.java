package com.example.jobportal.employee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jobportal.R;
import com.example.jobportal.helper.MapsActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FindJob extends AppCompatActivity {
    Spinner spinner2;
    TextView userCategory;
    static final int OBTAIN_LOCATION = 1;
    String country = "", state = "", city = "";
    LatLng latlng = null;
    ListView lv;
    ArrayList<String> array;
    ArrayAdapter<String> adapter;
    DatabaseReference myRef;
    TextView category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_job);

        final String username = getIntent().getExtras().getString("username");

        //To list the available categories
        addItemsOnSpinner2();

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
        lv = (ListView) findViewById(R.id.jobListView);
        array = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(FindJob.this, android.R.layout.simple_list_item_1, array);
        lv.setAdapter(adapter);

        //To check for the required preferences
        category = findViewById(R.id.userCategory);

        Button btnFindJob = (Button) findViewById(R.id.btnFindJob);
        btnFindJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category.getText().toString().trim().isEmpty() || category.getText().toString() == null) {
                    category.setError("Select a Category from the above list");
                }
                if (!(validateLocationDetails())) {
                    Toast.makeText(getApplicationContext(), "Error in getting location. Please try again", Toast.LENGTH_LONG).show();
                } else {
                    adapter.clear();
                    myRef = FirebaseDatabase.getInstance().getReference().child("JobsLocation");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        String categoryName = category.getText().toString().trim();
                        String locationDetail = city + "-" + state;
                        String loc = categoryName + "-" + locationDetail;

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(loc)) {
                                DataSnapshot ds = dataSnapshot.child(loc);
                                for (DataSnapshot ss : ds.getChildren()) {
                                    StringBuilder sb = new StringBuilder();
                                    Double locLat = Double.parseDouble(ss.child("jobLatitude").getValue().toString());
                                    Double locLon = Double.parseDouble(ss.child("jobLongitude").getValue().toString());
                                    if (distance(latlng.latitude, latlng.longitude, locLat, locLon) <= 5) {
                                        sb.append(ss.getKey() + " - " + ss.child("jobTitle").getValue().toString());
                                        array.add(sb.toString());
                                    }
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
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                String jobId = item.split("-")[0].trim();
                Intent intent = new Intent(FindJob.this, ApplyJob.class);
                intent.putExtra("JobId", jobId);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    public void getLocation(View v) throws Exception {
        try {
            Intent intent = new Intent(this, MapsActivity.class);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, OBTAIN_LOCATION);
            }
        } catch (Exception ex) {
            String s = ex.getMessage();
            throw new Exception(s);
        }
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

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
