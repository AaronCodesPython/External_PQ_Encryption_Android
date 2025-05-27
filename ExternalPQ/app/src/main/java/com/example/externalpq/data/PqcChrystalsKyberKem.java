package com.example.externalpq.data;

import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec;
import org.bouncycastle.util.Arrays;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyGenerator;

public class PqcChrystalsKyberKem {

    public static void main(String[] args) {
        if (Security.getProvider("BCPQC") == null) {
            Security.addProvider(new BouncyCastlePQCProvider());
        }
    }

        out += "\n" + "\n************************************\n" +

    private static void runProcedure(KyberParameterSpec kyberParameterSpec, KeyPair alreadyGeneratedKeyPair){
        // when this function is called we assume we have made the necessary checks to guarantee a valid KeyPair exists
        PrivateKey privateKey = alreadyGeneratedKeyPair.getPrivate();
        PublicKey publicKey = alreadyGeneratedKeyPair.getPublic();
        byte[] privateKeyByte = privateKey.getEncoded();
        byte[] publicKeyByte = publicKey.getEncoded();
        PrivateKey privateKeyLoad = getChrystalsKyberPrivateKeyFromEncoded(privateKeyByte);
        PublicKey publicKeyLoad = getChrystalsKyberPublicKeyFromEncoded(publicKeyByte);
        SecretKeyWithEncapsulation secretKeyWithEncapsulationSender = pqcGenerateChrystalsKyberEncryptionKey(publicKeyLoad);
        byte[] encryptionKey = secretKeyWithEncapsulationSender.getEncoded();
        byte[] encapsulatedKey = secretKeyWithEncapsulationSender.getEncapsulation();
        byte[] decryptionKey = pqcGenerateChrystalsKyberDecryptionKey(privateKeyLoad, encapsulatedKey);
        boolean keysAreEqual = Arrays.areEqual(encryptionKey, decryptionKey);
    }

    private static void runProcedure(KyberParameterSpec kyberParameterSpec){
        KeyPair keyPair = generateChrystalsKyberKeyPair(kyberParameterSpec);
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        byte[] privateKeyByte = privateKey.getEncoded();
        byte[] publicKeyByte = publicKey.getEncoded();
        PrivateKey privateKeyLoad = getChrystalsKyberPrivateKeyFromEncoded(privateKeyByte);
        PublicKey publicKeyLoad = getChrystalsKyberPublicKeyFromEncoded(publicKeyByte);
        SecretKeyWithEncapsulation secretKeyWithEncapsulationSender = pqcGenerateChrystalsKyberEncryptionKey(publicKeyLoad);
        byte[] encryptionKey = secretKeyWithEncapsulationSender.getEncoded();
        byte[] encapsulatedKey = secretKeyWithEncapsulationSender.getEncapsulation();
        byte[] decryptionKey = pqcGenerateChrystalsKyberDecryptionKey(privateKeyLoad, encapsulatedKey);
        boolean keysAreEqual = Arrays.areEqual(encryptionKey, decryptionKey);
    }


    private static KeyPair generateChrystalsKyberKeyPair(KyberParameterSpec kyberParameterSpec) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("KYBER", "BCPQC");
            kpg.initialize(kyberParameterSpec, new SecureRandom());
            KeyPair kp = kpg.generateKeyPair();
            return kp;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SecretKeyWithEncapsulation pqcGenerateChrystalsKyberEncryptionKey(PublicKey publicKey) {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("KYBER", "BCPQC");
            keyGen.init(new KEMGenerateSpec((PublicKey) publicKey, "AES"), new SecureRandom());
            SecretKeyWithEncapsulation secEnc1 = (SecretKeyWithEncapsulation) keyGen.generateKey();

            return secEnc1;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] pqcGenerateChrystalsKyberDecryptionKey(PrivateKey privateKey, byte[] encapsulatedKey) {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("KYBER", "BCPQC");
            keyGen.init(new KEMExtractSpec((PrivateKey) privateKey, encapsulatedKey, "AES"), new SecureRandom());
            SecretKeyWithEncapsulation secEnc2 = (SecretKeyWithEncapsulation) keyGen.generateKey();
            return secEnc2.getEncoded();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static PrivateKey getChrystalsKyberPrivateKeyFromEncoded(byte[] encodedKey) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("KYBER", "BCPQC");
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static PublicKey getChrystalsKyberPublicKeyFromEncoded(byte[] encodedKey) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(encodedKey);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("KYBER", "BCPQC");
            return keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte b : bytes) result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
}