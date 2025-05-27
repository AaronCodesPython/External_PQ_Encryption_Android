package com.example.externalpq.data;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
//
//
//

public class Crypto {
    private String pubKey;
    private String privKey;

    public static byte[] generate_pubKey(){
        return "2304i22304i2532022304i2532022304i2532022304i2532022304i2532022304i2532022304i2532022304i2532022304i2532022304i2532022304i2532022304i25320253202".getBytes(StandardCharsets.UTF_8);
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

    public static byte[] get_shared_secret(Contact currentContact){
        String key;
        if(currentContact.id == 0){
            key= "f3c2b8a7e02e19d04bd44137e9a6d67c8df7b1c32d8b0b0b1cd2ab6f5f0d9a44";
        }
        else{
            key= "f3c2b8a7e02e72014bd44137e9a6d67c8df7b1c32d8b0b0b1cd2ab6f5f0d9a44";
        }
        byte[] keyBytes = hexToBytes(key);
        return keyBytes;
    }


    public static String encrypt(String message, Contact receiverPubKey){
        //return PqcChrystalsKyberKem.run(false);
        //return "current recPub:"+receiverPubKey.key;
        return "";
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
