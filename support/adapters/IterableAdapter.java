package com.mental_elemental.android.support.adapters;

import android.content.Context;
import android.widget.BaseAdapter;

public abstract class IterableAdapter<T> extends BaseAdapter
{
    protected final Context context;
    protected final Iterable<T> items;
    protected int size;

    public IterableAdapter(Context context, Iterable<T> items, int size)
    {
        this.items = items;
        this.context = context;
        this.size = size;
    }

    @Override
    public int getCount()
    {
        return size;
    }

    @Override
    public T getItem(int position)
    {
        int i = 0;
        for (T item : items)
        {
            if (i == position)
                return item;
            ++i;
        }

        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public void notifyDataSetChanged()
    {
        measureSize();
        super.notifyDataSetChanged();
    }

    protected void measureSize()
    {
        size = 0;
        for (T item : items)
            ++size;
    }

}
