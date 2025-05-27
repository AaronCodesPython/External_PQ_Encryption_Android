package com.example.externalpq.data;

import androidx.annotation.NonNull;

public class Contact{
    public String identifier;
    String key;
    int id;
    public Contact(String identifier, String key, int id){
        this.identifier = identifier;
        this.key = key;
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return this.identifier;
    }

}
