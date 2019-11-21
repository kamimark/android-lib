package com.mental_elemental.android.support.adapters;

import android.content.Context;

import java.util.Collection;

public abstract class CollectionAdapter<T> extends IterableAdapter<T>
{
    protected Collection<T> itemsCollection;

    public CollectionAdapter(Context context, Collection<T> items)
    {
        super(context, items, items.size());
        itemsCollection = items;
    }

    protected void measureSize()
    {
        size = itemsCollection.size();
    }
}
