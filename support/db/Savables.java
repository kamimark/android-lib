package com.mental_elemental.android.support.db;

import com.mental_elemental.android.support.Callback;
import com.mental_elemental.android.support.Serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import androidx.annotation.NonNull;

public abstract class Savables<T>
{
    protected DbHelper dbHelper;
    protected final String type;
    protected final Serializer<T> serializer;
    protected Executor executor = Executors.newSingleThreadExecutor();

    protected Savables(@NonNull DbHelper dbHelper, @NonNull String type, @NonNull Serializer<T> serializer)
    {
        this.dbHelper = dbHelper;
        this.type = type;
        this.serializer = serializer;
    }

    public FutureTask<Void> clear()
    {
        return dbHelper.remove(type, null);
    }

    public Collection<T> load(final Collection<T> container)
    {
        try
        {
            loadLater(container).get();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return container;
    }

    public FutureTask<Collection<T>> loadLater(final Collection<T> container)
    {
        FutureTask<Collection<T>> future = new FutureTask<>(new Callable<Collection<T>>()
        {
            @Override
            public Collection<T> call() throws Exception
            {
                List<DbHelper.Projection> projections = dbHelper.getAll(type).get();
                for (DbHelper.Projection projection : projections)
                    try
                    {
                        container.add(serializer.deserialize(projection.data));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                return container;
            }
        });

        executor.execute(future);

        return future;
    }

    public void load(final Callback<Collection<T>> callback)
    {
        FutureTask<Void> future = new FutureTask<Void>(new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                List<T> container = new ArrayList<>();
                List<DbHelper.Projection> projections = dbHelper.getAll(type).get();
                for (DbHelper.Projection projection : projections)
                    try
                    {
                        container.add(serializer.deserialize(projection.data));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                callback.called(container);
                return null;
            }
        });

        executor.execute(future);
    }

    public long count() {
        return count(null);
    }

    protected long count(final String startKey)
    {
        try
        {
            return dbHelper.count(type, startKey).get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    protected FutureTask<String> save(String key, T obj)
    {
        return dbHelper.set(type, key, serializer.serialize(obj));
    }

    protected T get(final String key)
    {
        try
        {
            return serializer.deserialize(dbHelper.get(type, key).get().data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    protected void remove(String key)
    {
        dbHelper.remove(type, key);
    }

    protected Collection<T> getAll(final String startKey)
    {
        Collection<T> collection = new ArrayList<>();
        try
        {
            for (DbHelper.Projection projection : dbHelper.like(type, startKey).get())
            {
                try
                {
                    collection.add(serializer.deserialize(projection.data));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return collection;
    }
}
