package com.example.externalpq.data;

public class Crypto {
    private String pubKey;
    private String privKey;

    public static String generate_pubKey(){
        return "2304i22304i2532022304i2532022304i2532022304i2532022304i2532022304i2532022304i2532022304i2532022304i2532022304i2532022304i2532022304i25320253202";
    }

    public static String encrypt(String message, Contact receiverPubKey){
        return PqcChrystalsKyberKem.run(false);
        //return "current recPub:"+receiverPubKey.key;
    }
}
