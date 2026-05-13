package com.example.lostnfound;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailsActivity extends AppCompatActivity {

    private TextView textDetailType, textDetailCategory, textDetailDate, textDetailName, textDetailPhone, textDetailLocation, textDetailDescription;
    private ImageView imageDetailView;
    private Button btnRemove;
    private DatabaseHelper databaseHelper;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        databaseHelper = new DatabaseHelper(this);

        textDetailType = findViewById(R.id.textDetailType);
        textDetailCategory = findViewById(R.id.textDetailCategory);
        imageDetailView = findViewById(R.id.imageDetailView);
        textDetailDate = findViewById(R.id.textDetailDate);
        textDetailName = findViewById(R.id.textDetailName);
        textDetailPhone = findViewById(R.id.textDetailPhone);
        textDetailLocation = findViewById(R.id.textDetailLocation);
        textDetailDescription = findViewById(R.id.textDetailDescription);
        btnRemove = findViewById(R.id.btnRemove);

        if (getIntent().hasExtra("item")) {
            item = (Item) getIntent().getSerializableExtra("item");
            if (item != null) {
                textDetailType.setText(item.getType());
                textDetailCategory.setText(item.getCategory());
                textDetailDate.setText("Date: " + item.getDate());
                textDetailName.setText("Name: " + item.getName());
                textDetailPhone.setText("Phone: " + item.getPhone());
                textDetailLocation.setText("Location: " + item.getLocation());
                textDetailDescription.setText("Description: " + item.getDescription());

                if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
                    try {
                        imageDetailView.setImageURI(Uri.parse(item.getImageUri()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        btnRemove.setOnClickListener(v -> {
            if (item != null) {
                databaseHelper.deleteItem(item.getId());
                Toast.makeText(this, "Item Removed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
