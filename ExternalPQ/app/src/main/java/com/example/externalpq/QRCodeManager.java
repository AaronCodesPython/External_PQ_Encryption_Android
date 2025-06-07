package com.example.externalpq;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.externalpq.databinding.ActivityMainBinding;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.InputStream;

public class QRCodeManager {
    public static String decodeImageFromUri(Context context, Uri uri) {
        try {
            // Step 1: Load Bitmap from URI
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) return null;

            // Step 2: Convert Bitmap to BinaryBitmap
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            // Step 3: Decode using ZXing
            MultiFormatReader reader = new MultiFormatReader();
            Result result = reader.decode(binaryBitmap);

            return result.getText(); // This is the decoded string

        } catch (Exception e) {
            Log.d("imageInfo", e.toString());
            e.printStackTrace();
            return null;
        }
    }

    static void generateQRCode(String text, ActivityMainBinding binding){
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400);
            binding.qrCodeImage.setImageBitmap(bitmap);
        }
        catch (WriterException e) {
            e.printStackTrace();
        }
    }

}
