package com.mental_elemental.android.support.adapters;

import android.content.Context;

import java.util.Collection;
import java.util.List;

public abstract class ListAdapter<T> extends CollectionAdapter<T>
{
    public ListAdapter(Context context, Collection<T> items)
    {
        super(context, items);
    }

    @Override
    public T getItem(int position)
    {
        if (items instanceof List)
            return ((List<T>) items).get(position);

        int i = -1;
        for (T item : items)
            if (++i == position)
                return item;

        return null;
    }
}
