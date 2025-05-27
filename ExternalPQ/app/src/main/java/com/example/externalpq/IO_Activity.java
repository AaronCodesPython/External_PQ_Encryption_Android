package com.example.externalpq;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.externalpq.data.Contact;
import com.example.externalpq.data.Crypto;
import com.example.externalpq.databinding.ActivityIoBinding;
import com.example.externalpq.databinding.ActivityMainBinding;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import java.security.Security;
import java.util.List;

public class IO_Activity extends AppCompatActivity {
    private ActivityIoBinding binding;
    List<Contact> contacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.outputTextfield.setFocusable(false);
        contacts = contact_screen.readContacts(this);
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
                //Log.d("encryption_info",Crypto.encrypt(s.toString(), (Contact) binding.contactSpinner.getSelectedItem()));
                try{
                    Log.i("textinfo", binding.inputField.getText().toString());
                    if(binding.mainToggle.getCheckedButtonId() == binding.encryptButton.getId()){
                        binding.outputTextfield.setText(Crypto.AESUtil.encrypt(binding.inputField.getText().toString(), Crypto.get_shared_secret()));

                    }else if(binding.mainToggle.getCheckedButtonId() == binding.decryptButton.getId()){
                        binding.outputTextfield.setText(Crypto.AESUtil.decrypt(binding.inputField.getText().toString(), Crypto.get_shared_secret()));

                    }
                    else{

                    }
                    //Toast.makeText(IO_Activity.this, , Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Log.d("encryption_info", "error catched 2:"+e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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


        binding.copyButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                ClipData clip = ClipData.newPlainText("Copied Text", binding.outputTextfield.getText().toString());
                clipboard.setPrimaryClip(clip);
            }

        });



       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        initBouncyCastle();
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