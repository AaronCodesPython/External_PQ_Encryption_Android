package com.example.externalpq;

import android.content.ContextWrapper;
import android.os.Bundle;


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

public class contact_screen extends AppCompatActivity {

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
    }

    public static List<Contact> readContacts(ContextWrapper t){
        ArrayList<Contact> contacts = new ArrayList<>();
        File file = new File(t.getFilesDir(), "contacts.txt");
        if(file.exists()){
            try (FileInputStream fis = new FileInputStream(file);
                 InputStreamReader isr = new InputStreamReader(fis);
                 BufferedReader reader = new BufferedReader(isr)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(";")) {
                        String[] parts = line.split(";", 2); // limit = 2 ensures name can have semicolon
                        contacts.add(new Contact(parts[0].trim(), parts[1].trim()));
                    }
                }
            } catch (IOException e) {}
        }
        else{
            try (FileOutputStream fos = t.openFileOutput("contacts.txt", MODE_PRIVATE)) {
                fos.write("Alice;000000000000000000\nBob;000000000000000000".getBytes());
            } catch (IOException e) {}
            return readContacts(t);

        }
        return contacts;
    }

   // private voi deleteContact()


}
