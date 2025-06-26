package io.github.chiffre.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import io.github.chiffre.R;
import io.github.chiffre.data.Contact;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    //private final List<Contact> contactList;
    private final OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onDelete(Contact contact);
    }

    public ContactAdapter(Context context, List<Contact> contacts, OnDeleteClickListener listener) {
        super(context, 0, contacts);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.contact_list_item, parent, false);
        }

        TextView name = convertView.findViewById(R.id.contactName);
        ImageButton delete = convertView.findViewById(R.id.deleteButton);

        assert contact != null;
        name.setText(contact.identifier);

        delete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(contact);
            }
        });

        return convertView;
    }
}

