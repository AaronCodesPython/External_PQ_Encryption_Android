package com.example.externalpq;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.externalpq.databinding.ActivityIoBinding;
import com.example.externalpq.databinding.ActivityMainBinding;

public class IO_Activity extends AppCompatActivity {
    private ActivityIoBinding binding;
    String[] choices = {"Option 1", "Option 2", "Option 3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.outputTextfield.setFocusable(false);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, choices);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.contactSpinner.setAdapter(adapter);
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
    }
}