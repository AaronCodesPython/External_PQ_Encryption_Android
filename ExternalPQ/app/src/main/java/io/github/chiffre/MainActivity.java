package io.github.chiffre;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import io.github.chiffre.data.Crypto;
import io.github.chiffre.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    TimerManager timerManager;
    Crypto cryptoManager;
    private void shareImage(Bitmap bitmap) {
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    file
            );

            if (contentUri != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share Image Via"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to share image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        cryptoManager = new Crypto();
        timerManager = new TimerManager();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                cryptoManager.generate_keys();
                QRCodeManager.generateQRCode(cryptoManager.getOwnPubKey(), binding);
                }

        });

        binding.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Bitmap bitmap = getBitmapFromImageView(binding.qrCodeImage);
                shareImage(bitmap);
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
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long currTime = Instant.now().getEpochSecond();
                long goaltime;
                if(timerManager.creationTime == -1){
                    goaltime = timerManager.getCreationTime(MainActivity.this);
                }else{
                    goaltime = timerManager.creationTime;
                }
                if(timerManager.needsRefresh(goaltime,currTime)){
                    cryptoManager.generate_keys();
                    timerManager.writeTime(MainActivity.this);
                }
                String textToShow ="Valid For: "+ TimerManager.formatTimeDifference(currTime,timerManager.creationTime);
                binding.remainingTime.setText(textToShow);
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);

        String qrData = cryptoManager.getOwnPubKey();
        QRCodeManager.generateQRCode(qrData,binding);
    }


    private Bitmap getBitmapFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }


}

/*
* // Used to load the 'externalpq' library on application startup.
    static {
        System.loadLibrary("externalpq");
    }*/