package com.example.lostnfound;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper dbHelper;
    private List<Item> allItems;
    private EditText editRadius;
    private Button btnSearchRadius;
    private FusedLocationProviderClient fusedLocationClient;
    private double currentLat = 0.0;
    private double currentLng = 0.0;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    performRadiusSearch();
                } else {
                    Toast.makeText(this, "Permission denied. Cannot do radius search.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        dbHelper = new DatabaseHelper(this);
        allItems = dbHelper.getAllItems();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editRadius = findViewById(R.id.editRadius);
        btnSearchRadius = findViewById(R.id.btnSearchRadius);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnSearchRadius.setOnClickListener(v -> {
            String radiusStr = editRadius.getText().toString().trim();
            if (TextUtils.isEmpty(radiusStr)) {
                showAllMarkers();
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                } else {
                    performRadiusSearch();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showAllMarkers();
    }

    private void showAllMarkers() {
        if (mMap == null) return;
        mMap.clear();
        boolean moved = false;
        for (Item item : allItems) {
            if (item.getLatitude() != 0.0 || item.getLongitude() != 0.0) {
                LatLng pos = new LatLng(item.getLatitude(), item.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(item.getType() + ": " + item.getName())
                        .snippet(item.getDescription()));
                if (!moved) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
                    moved = true;
                }
            }
        }
    }

    private void performRadiusSearch() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                filterMarkersByRadius();
            } else {
                Toast.makeText(this, "Could not get current location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterMarkersByRadius() {
        String radiusStr = editRadius.getText().toString().trim();
        if (TextUtils.isEmpty(radiusStr)) return;
        double radiusKm = 0;
        try {
            radiusKm = Double.parseDouble(radiusStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid radius", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mMap == null) return;
        mMap.clear();

        LatLng currentPos = new LatLng(currentLat, currentLng);
        mMap.addMarker(new MarkerOptions().position(currentPos).title("You are here"));

        Location userLoc = new Location("");
        userLoc.setLatitude(currentLat);
        userLoc.setLongitude(currentLng);

        boolean moved = false;
        for (Item item : allItems) {
            if (item.getLatitude() != 0.0 || item.getLongitude() != 0.0) {
                Location itemLoc = new Location("");
                itemLoc.setLatitude(item.getLatitude());
                itemLoc.setLongitude(item.getLongitude());

                float distanceMeters = userLoc.distanceTo(itemLoc);
                if (distanceMeters <= radiusKm * 1000) {
                    LatLng pos = new LatLng(item.getLatitude(), item.getLongitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(pos)
                            .title(item.getType() + ": " + item.getName())
                            .snippet(item.getDescription()));
                    if (!moved) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12));
                        moved = true;
                    }
                }
            }
        }
        if (!moved) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 12));
        }
    }
}
