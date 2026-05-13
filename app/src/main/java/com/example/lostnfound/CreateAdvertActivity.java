package com.example.lostnfound;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    private RadioGroup radioGroupType;
    private Spinner spinnerCategory;
    private Button btnUploadImage;
    private ImageView imageViewPreview;
    private EditText editName, editPhone, editDescription;
    private TextView textLocation;
    private Button btnGetCurrentLocation;
    private Button btnSave;

    private String selectedImageUri = "";
    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;
    private DatabaseHelper databaseHelper;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        selectedImageUri = uri.toString();
                        imageViewPreview.setImageURI(uri);
                        imageViewPreview.setVisibility(View.VISIBLE);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> autocompleteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    textLocation.setText(place.getName());
                    if (place.getLatLng() != null) {
                        selectedLatitude = place.getLatLng().latitude;
                        selectedLongitude = place.getLatLng().longitude;
                    }
                } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                    com.google.android.gms.common.api.Status status = Autocomplete.getStatusFromIntent(result.getData());
                    String errorMsg = status != null ? status.getStatusMessage() : "Unknown error";
                    Toast.makeText(CreateAdvertActivity.this, "Places API Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    android.util.Log.e("PlacesError", "Error: " + errorMsg);
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }

        databaseHelper = new DatabaseHelper(this);

        radioGroupType = findViewById(R.id.radioGroupType);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        imageViewPreview = findViewById(R.id.imageViewPreview);
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editDescription = findViewById(R.id.editDescription);
        textLocation = findViewById(R.id.textLocation);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        btnSave = findViewById(R.id.btnSave);

        btnUploadImage.setOnClickListener(v -> openImagePicker());

        textLocation.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(this);
            autocompleteLauncher.launch(intent);
        });

        btnGetCurrentLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        });

        btnSave.setOnClickListener(v -> saveAdvert());
    }

    private void getCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this, location -> {
            if (location != null) {
                selectedLatitude = location.getLatitude();
                selectedLongitude = location.getLongitude();
                
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(selectedLatitude, selectedLongitude, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        textLocation.setText(addresses.get(0).getAddressLine(0));
                    } else {
                        textLocation.setText("Lat: " + selectedLatitude + ", Lng: " + selectedLongitude);
                    }
                } catch (IOException e) {
                    textLocation.setText("Lat: " + selectedLatitude + ", Lng: " + selectedLongitude);
                }
            } else {
                Toast.makeText(this, "Could not get location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveAdvert() {
        int selectedId = radioGroupType.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String type = radioButton.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String locationStr = textLocation.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || description.isEmpty() || locationStr.isEmpty() || locationStr.equals("Tap to search location")) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri.isEmpty()) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Item item = new Item(0, type, category, selectedImageUri, date, name, phone, description, locationStr, selectedLatitude, selectedLongitude);
        long result = databaseHelper.insertItem(item);

        if (result != -1) {
            Toast.makeText(this, "Advert Saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show();
        }
    }
}
