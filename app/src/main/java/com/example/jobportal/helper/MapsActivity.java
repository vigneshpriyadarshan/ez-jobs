package com.example.jobportal.helper;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.jobportal.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnIndoorStateChangeListener {

    private GoogleMap mMap;
    public LatLng latLngValue;
    String city;
    String state;
    String country;
    PlacesClient placesClient;
    private static final String TAG = "Main Activity";
    private UiSettings mUiSettings;
    Marker mark;

    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        //Setting onclick listener for accept button
        Button btnLocation = (Button) findViewById(R.id.btnLocation);
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("city", city);
                intent.putExtra("country", country);
                intent.putExtra("state", state);
                intent.putExtra("latlng", latLngValue);
                setResult(RESULT_OK, intent);
                Toast.makeText(getBaseContext(), "Location obtained successfully", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        mUiSettings = mMap.getUiSettings();

        mUiSettings.setCompassEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setMapToolbarEnabled(true);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place
                latLngValue = place.getLatLng();
                String adr = place.getName();
                Geocoder coder = new Geocoder(getApplicationContext());
                List<Address> address;
                try {
                    address = coder.getFromLocationName(adr, 1);
                    if (address == null) {
                        return;
                    }
                    Address location = address.get(0);
                    country = location.getCountryName();
                    city = location.getLocality();
                    state = location.getAdminArea();
                    if (latLngValue == null) {
                        Double lat = location.getLatitude();
                        Double lon = location.getLongitude();
                        latLngValue = new LatLng(lat, lon);
                    }
                    mark.remove();
                    mark = mMap.addMarker(new MarkerOptions().position(latLngValue).title("Selected Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngValue));
                } catch (Exception ex) {

                }
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mark = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            setLocationDetails(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onIndoorBuildingFocused() {

    }

    @Override
    public void onIndoorLevelActivated(IndoorBuilding indoorBuilding) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        setLocationDetails(latLng.latitude, latLng.longitude);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        setLocationDetails(location.getLatitude(), location.getLongitude());
    }

    public void setLocationDetails(double latitude, double longitude) {
        Geocoder coder = new Geocoder(getApplicationContext());
        try {
            List<Address> addresses = coder.getFromLocation(latitude, longitude, 1);
            if (addresses == null) {
                return;
            }
            Address currLocation = addresses.get(0);
            country = currLocation.getCountryName();
            city = currLocation.getLocality();
            state = currLocation.getAdminArea();
            latLngValue = new LatLng(latitude, longitude);
            mark.remove();
            mark = mMap.addMarker(new MarkerOptions().position(latLngValue).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngValue));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
