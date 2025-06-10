package com.example.externalpq.data;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Crypto {

    public static void generate_keys(){
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");

            keyPairGenerator.initialize(
                    new KeyGenParameterSpec.Builder(
                            "ec_dh_key",
                            KeyProperties.PURPOSE_AGREE_KEY
                    )
                            .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))  // NIST P-256
                            .setDigests(KeyProperties.DIGEST_SHA256)
                            .build()
            );
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
        }catch (Exception e){
            Log.d("debugInfo1", e.toString());
            e.printStackTrace();
        }

    }

    public static PrivateKey loadPrivateKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(
                    "ec_dh_key", null);

            return privateKeyEntry.getPrivateKey();

        } catch (Exception e) {
            Log.e("KeyStore", "Error loading private key", e);
            return null;
        }
    }

    public static PublicKey StringToPubkey(byte[] publicKeyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (Exception e) {
            Log.e("ECDH", "Failed to parse public key", e);
            return null;
        }
    }

    public static String getOwnPubKey(){
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            PublicKey publicKey = keyStore.getCertificate("ec_dh_key").getPublicKey();
            return pubkeyToString(publicKey);
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static String pubkeyToString(PublicKey pubKey){

            byte[] publicKeyBytes = pubKey.getEncoded();  // X.509 encoded
        return Base64.encodeToString(publicKeyBytes, Base64.NO_WRAP);

    }
    public static byte[] get_shared_secret(Contact receiver) throws Exception{
        try {
            // 1. Load your private key from KeyStore
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey("ec_dh_key", null);

            // 2. Create KeyAgreement instance
            KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(StringToPubkey(receiver.key.getBytes()), true);  // Supply peer's public key

            // 3. Generate the shared secret
            return keyAgreement.generateSecret();  // Returns byte[] (shared secret)
        } catch (Exception e) {
            Log.e("ECDH", "Shared secret computation failed", e);
            throw new Exception("Error in Shared Secret Generation");
        }
    }






    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }







    public static class AESUtil {

        public static String encrypt(String text, byte[] encryption_key) throws Exception {
            byte[] data = text.getBytes(StandardCharsets.UTF_8);
            SecretKey key = new SecretKeySpec(encryption_key, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // Generate random 16-byte IV
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] encrypted = cipher.doFinal(data);

            // Return IV + ciphertext
            byte[] output = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, output, 0, iv.length);
            System.arraycopy(encrypted, 0, output, iv.length, encrypted.length);
            String result = Base64.encodeToString(output, Base64.NO_WRAP);

            return result;
        }

        public static String decrypt(String encRes, byte[] aes256Key) throws Exception {

            SecretKey key = new SecretKeySpec(aes256Key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] ivAndCiphertext = Base64.decode(encRes, Base64.NO_WRAP);
            // Extract IV and ciphertext
            byte[] iv = new byte[16];
            byte[] ciphertext = new byte[ivAndCiphertext.length - 16];
            System.arraycopy(ivAndCiphertext, 0, iv, 0, 16);
            System.arraycopy(ivAndCiphertext, 16, ciphertext, 0, ciphertext.length);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        }
    }
}


/*
*   private String readPubKey(){
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
            Crypto.generate_keys();
            try (FileOutputStream fos = openFileOutput("pub_keyData.txt", MODE_PRIVATE)) {
                fos.write(Crypto.get_pubKey());
            } catch (IOException e) {}
            return readPubKey();
        }

        return sb.toString();

    }*/