package com.mental_elemental.android.support.db;

import com.mental_elemental.android.support.Serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import androidx.annotation.NonNull;

public class SimpleSavables<T> extends Savables<T>
{
    public SimpleSavables(@NonNull DbHelper dbHelper, @NonNull String type, @NonNull Serializer<T> serializer)
    {
        super(dbHelper, type, serializer);
    }

    public FutureTask<String> save(String key, T obj)
    {
        return super.save(key, obj);
    }

    public FutureTask<String> save(T obj)
    {
        return save(UUID.randomUUID().toString(), obj);
    }

    public T get(final String key)
    {
        return super.get(key);
    }

    public void remove(String key)
    {
        super.remove(key);
    }

    public FutureTask<Void> save(final Collection<T> container)
    {
        FutureTask<Void> future = new FutureTask<>(new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                clear().get();

                List<DbHelper.Projection> projections = new ArrayList<>();
                for (T obj : container)
                {
                    DbHelper.Projection projection = new DbHelper.Projection();
                    projection.type = type;
                    projection.key = UUID.randomUUID().toString();
                    projection.data = serializer.serialize(obj);
                    if (projection.data == null)
                        continue;
                    projections.add(projection);
                }

                dbHelper.setAll(type, projections).get();

                return null;
            }
        });

        executor.execute(future);

        return future;
    }

    public long count(String startKey) {
        return super.count(startKey);
    }
}
