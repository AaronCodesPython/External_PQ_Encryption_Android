package com.example.externalpq.data;

import androidx.annotation.NonNull;

public class Contact{
    public String identifier;
    String key;
    public Contact(String identifier, String key){
        this.identifier = identifier;
        this.key = key;
    }

    @NonNull
    @Override
    public String toString() {
        return this.identifier;
    }

}
