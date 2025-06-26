package com.example.externalpq;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.externalpq.adapter.ContactAdapter;
import com.example.externalpq.data.Contact;
import com.example.externalpq.databinding.ActivityContactScreenBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private ActivityContactScreenBinding binding;
    private List<Contact> contacts;
    private ContactAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Sample data
        contacts =readContacts(this);


        // Use built-in ArrayAdapter for simplicity


        adapter = new ContactAdapter(this, contacts, contact -> {
            contacts.remove(contact);
            adapter.notifyDataSetChanged();
            // Optionally delete from file
        });
        binding.contactListView.setAdapter(adapter);

        binding.addContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                ImagePickerDialogFragment dialog = new ImagePickerDialogFragment();
                dialog.show(getSupportFragmentManager(), "imagePickerDialog");

            }
        });

        binding.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public static List<Contact> readContacts(ContextWrapper t){
        ArrayList<Contact> contacts = new ArrayList<>();
        File file = new File(t.getFilesDir(), "contacts_data.txt");
        if(file.exists()){
            try (FileInputStream fis = new FileInputStream(file);
                 InputStreamReader isr = new InputStreamReader(fis);
                 BufferedReader reader = new BufferedReader(isr)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(";")) {
                        String[] parts = line.split(";", 3);
                        contacts.add(new Contact(parts[0].trim(), parts[1].trim(), Integer.parseInt(parts[2].trim())));
                    }
                }
            } catch (IOException e) {}
        }
        else{
            try (FileOutputStream fos = t.openFileOutput("contacts_data.txt", MODE_PRIVATE)) {
                fos.write("Alice;MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE+9IMLPT63wxKgSb/U0Nm/0TAN6uQUy/Q+WVNsuQHRCgu56onRzeqe/lBJBl/Fqzvma14G27GLaw4OPPwZO3GXA==;0\nBob;MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAErO7232dz7Q0UrPSgFlV/kDrXEttmtOQsZ0ytnEBntwk5CH3Bj7o29SqDYLOr7sMUdz5CBE7ObqUV4QMrc3yYDw==;1".getBytes());
            } catch (IOException e) {}
            return readContacts(t);

        }
        return contacts;
    }




}
