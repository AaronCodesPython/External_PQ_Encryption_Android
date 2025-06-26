package io.github.chiffre.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj.getClass() != this.getClass()){
            return false;
        }
        Contact c = (Contact) obj;
        return this.key.equals(c.key) && this.identifier.equals(c.identifier);
    }
}
