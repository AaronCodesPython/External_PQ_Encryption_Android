package io.github.chiffre;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import io.github.chiffre.data.Contact;
import io.github.chiffre.data.Crypto;
import io.github.chiffre.databinding.ActivityIoBinding;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import java.security.Security;
import java.util.List;

public class IO_Activity extends AppCompatActivity {
    private ActivityIoBinding binding;
    List<Contact> contacts;
    Contact currentContact;
    Crypto cryptoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIoBinding.inflate(getLayoutInflater());
        cryptoManager = new Crypto();
        setContentView(binding.getRoot());
        initBouncyCastle();
        binding.outputTextfield.setFocusable(false);
        contacts = ContactActivity.readContacts(this);
        currentContact = (Contact) binding.contactSpinner.getSelectedItem();


        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);


        ArrayAdapter<Contact> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, contacts);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.contactSpinner.setAdapter(adapter);

        binding.inputField.addTextChangedListener(new  android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new Thread(()->{
                    final String input = s.toString();
                    String set_to = "";
                    try{
                        if(binding.mainToggle.getCheckedButtonId() == binding.encryptButton.getId()){
                             set_to = Crypto.AESUtil.encrypt(binding.inputField.getText().toString(), cryptoManager.get_shared_secret(currentContact));

                        }else if(binding.mainToggle.getCheckedButtonId() == binding.decryptButton.getId()){
                            set_to = Crypto.AESUtil.decrypt(binding.inputField.getText().toString(), cryptoManager.get_shared_secret(currentContact), IO_Activity.this);
                        }
                        final String output = set_to;


                            if(input.equals(binding.inputField.getText().toString())){
                                runOnUiThread(() -> binding.outputTextfield.setText(output));
                            }
                            else{
                                Log.d("DataRace", input+" : "+binding.inputField.getText().toString());
                            }

                    }
                    catch (Exception e){
                        Log.d("encryption_info", "error catched 2:"+e.toString());
                        e.printStackTrace();
                    }
                }).start();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.contactSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentContact = (Contact) parent.getItemAtPosition(position);

                // Re-run logic to update outputTextfield
                String inputText = binding.inputField.getText().toString();
                if (!inputText.isEmpty()) {
                    try {
                        if (binding.mainToggle.getCheckedButtonId() == binding.encryptButton.getId()) {
                            binding.outputTextfield.setText(
                                    Crypto.AESUtil.encrypt(inputText, cryptoManager.get_shared_secret(currentContact))
                            );
                        } else if (binding.mainToggle.getCheckedButtonId() == binding.decryptButton.getId()) {
                            binding.outputTextfield.setText(
                                    Crypto.AESUtil.decrypt(inputText, cryptoManager.get_shared_secret(currentContact), IO_Activity.this)
                            );
                        }
                    } catch (Exception e) {
                        Log.e("encryption_info", "Failed to update on contact change: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        binding.switchButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                  int currSelection = binding.mainToggle.getCheckedButtonId();
                  binding.inputField.setText("");
                  binding.outputTextfield.setText("");

                if(currSelection == binding.encryptButton.getId()) {
                    binding.mainToggle.check(binding.decryptButton.getId());
                }
                else if(currSelection == binding.decryptButton.getId()){
                    binding.mainToggle.check(binding.encryptButton.getId());
                }

            }

        });

        binding.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(IO_Activity.this, MainActivity.class);
                startActivity(intent);
            }

        });

        binding.copyButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ClipData clip = ClipData.newPlainText("Copied Text", binding.outputTextfield.getText().toString());
                clipboard.setPrimaryClip(clip);
            }

        });

        binding.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String message = binding.outputTextfield.getText().toString();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, message);

                Intent chooser = Intent.createChooser(intent, "Share via");
                startActivity(chooser);
            }

        });

       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/


    }

    private void initBouncyCastle() {
        // this way for adding bouncycastle to android
        Security.removeProvider("BC");
        // Confirm that positioning this provider at the end works for your needs!
        Security.addProvider(new BouncyCastleProvider());
        if (Security.getProvider("BCPQC") == null) {
            Security.addProvider(new BouncyCastlePQCProvider());
        }
        //printlnX("Android version: " + getAndroidVersion());
        //printlnX("BouncyCastle version: " + getBouncyCastleVersion());
        //printlnX("BouncyCastle PQC version: " + getBouncyCastlePqcVersion());
    }
}