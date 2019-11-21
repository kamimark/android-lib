package com.mental_elemental.android.support.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mental_elemental.android.support.Support;

import java.util.Collection;

import androidx.annotation.NonNull;

public class OrdinalArrayAdapter extends ListAdapter<String>
{
    final Context context;

    public OrdinalArrayAdapter(Context context, Collection<String> items)
    {
        super(context, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);

        TextView view = (TextView) convertView;
        view.setText(Support.ordinal(position + 1) + " - " + getItem(position));

        return view;
    }
}
