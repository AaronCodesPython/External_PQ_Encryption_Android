package com.example.externalpq.data;

import androidx.annotation.NonNull;

public class Contact{
    public String identifier;
    public String key;
    public int id;
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
