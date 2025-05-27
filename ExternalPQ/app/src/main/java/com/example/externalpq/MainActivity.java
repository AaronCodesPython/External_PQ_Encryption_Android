package com.example.externalpq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.externalpq.data.Crypto;
import com.example.externalpq.databinding.ActivityMainBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Button generateQrBtn;
    private ImageView qrCodeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int qrWidth = binding.qrCodeImage.getWidth();
        int qrHeight = binding.qrCodeImage.getHeight();

        binding.refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                generateQRCode("you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you \n" +
                        "you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe, you stink Babe,", qrWidth, qrHeight);
                }

        });

        binding.encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IO_Activity.class);
                startActivity(intent);
            }
        });

        binding.contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, contact_screen.class);
                startActivity(intent);
            }
        });

        String qrData = readPubKey();
        generateQRCode(qrData,qrWidth,qrHeight);
    }
    private String readPubKey(){
        StringBuilder sb = new StringBuilder();
        File file = new File(getFilesDir(), "pub_keyData.txt");
        if(file.exists()){
            try (FileInputStream fis = new FileInputStream(file);
                 InputStreamReader isr = new InputStreamReader(fis);
                 BufferedReader reader = new BufferedReader(isr)) {

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

            } catch (IOException e) {}
        }
        else{
            try (FileOutputStream fos = openFileOutput("pub_keyData.txt", MODE_PRIVATE)) {
                fos.write(Crypto.generate_pubKey());
            } catch (IOException e) {}
            return readPubKey();
        }

        return sb.toString();

    }
    private void generateQRCode(String text, int qrWidth, int qrHeight){
        BarcodeEncoder barcodeEncoder
                = new BarcodeEncoder();
        try {

            // This method returns a Bitmap image of the
            // encoded text with a height and width of 400
            // pixels.
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, qrWidth, qrHeight);
            // Sets the Bitmap to ImageView
            binding.qrCodeImage.setImageBitmap(bitmap);
        }
        catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * A native method that is implemented by the 'externalpq' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}

/*
* // Used to load the 'externalpq' library on application startup.
    static {
        System.loadLibrary("externalpq");
    }*/