package com.example.lostnfound;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShowItemsActivity extends AppCompatActivity {

    private Spinner spinnerFilter;
    private RecyclerView recyclerViewItems;
    private ItemAdapter itemAdapter;
    private DatabaseHelper databaseHelper;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_items);

        databaseHelper = new DatabaseHelper(this);
        itemList = new ArrayList<>();

        spinnerFilter = findViewById(R.id.spinnerFilter);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);

        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter(this, itemList);
        recyclerViewItems.setAdapter(itemAdapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.filter_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                loadItems(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadItems("All");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String selectedCategory = spinnerFilter.getSelectedItem() != null ? spinnerFilter.getSelectedItem().toString() : "All";
        loadItems(selectedCategory);
    }

    private void loadItems(String category) {
        itemList = databaseHelper.getItemsByCategory(category);
        itemAdapter.setItemList(itemList);
    }
}
