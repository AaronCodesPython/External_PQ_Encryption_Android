package com.example.externalpq;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.externalpq.data.Contact;
import com.example.externalpq.data.Crypto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImagePickerDialogFragment extends DialogFragment {

    private ImageView imageView;
    private Button selectImageButton;

    private ActivityResultLauncher<String> imagePickerLauncher;
    Uri currentUri = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        currentUri = uri;
                        imageView.setImageURI(uri);
                        imageView.setVisibility(View.VISIBLE);
                        // You can store the URI or handle it further here
                    }
                }
        );
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_contact, null);

        imageView = view.findViewById(R.id.imageView);
        selectImageButton = view.findViewById(R.id.selectImageButton);

        selectImageButton.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        builder.setView(view)
                .setTitle("Add a New Contact")
                .setPositiveButton("OK", (dialog, id) -> {
                    // here we want to read the qr code
                    String pubKey = QRCodeManager.decodeImageFromUri(getContext(), currentUri);
                    EditText nameField =  view.findViewById(R.id.NameInputField);
                    String name =nameField.getText().toString().replaceAll(";","");
                    if(pubKey != null && !name.isEmpty()){
                        writeToContacts((ContextWrapper) getContext(), name,pubKey);
                    }
                    dismiss();
                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    startActivity(intent);
                });

        return  builder.create();
    }

    public void writeToContacts(ContextWrapper t,String name, String pubkey){
        List<Contact> contacts = ContactActivity.readContacts(t);

        int newId  = (contacts.get(contacts.size()-1).id)+1;
        try (FileOutputStream fos = t.openFileOutput("contacts.txt", Context.MODE_APPEND)) {
            fos.write(("\n"+name+";"+pubkey+";"+newId).getBytes());
        } catch (IOException e) {}
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (positiveButton != null) {
                positiveButton.setTextSize(20);
                positiveButton.setTextColor(Color.BLACK);
            }
        }
    }
}

