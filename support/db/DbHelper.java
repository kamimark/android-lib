package com.mental_elemental.android.support.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public final class DbHelper extends SQLiteOpenHelper
{
    public static class Projection
    {
        String type;
        String key;
        String data;
    }

    public final String[] TABLES;

    public static class CacheEntry implements BaseColumns
    {
        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_VALUE = "value";
    }

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "dbHelper.db";

    String[] projection = {
            CacheEntry.COLUMN_NAME_KEY,
            CacheEntry.COLUMN_NAME_VALUE
    };

    String selection = CacheEntry.COLUMN_NAME_KEY + " = ?";
    String query = CacheEntry.COLUMN_NAME_KEY + " like ?";

    private Executor executor = Executors.newSingleThreadExecutor();

    public DbHelper(Context context, String[] tables)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        TABLES = tables;
    }

    public void onCreate(SQLiteDatabase db)
    {
        for (String table : TABLES)
            db.execSQL(getCreateTable(table));
    }

    private String getCreateTable(String table)
    {
        return "CREATE TABLE IF NOT EXISTS " + table + " (" +
                        CacheEntry.COLUMN_NAME_KEY + " TEXT PRIMARY KEY," +
                        CacheEntry.COLUMN_NAME_VALUE + " TEXT)";
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //for (String table : TABLES)
        //    db.execSQL("DROP TABLE IF EXISTS " + table);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void blockedRemove(final String type, final String key)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(type, selection, new String[]{key});
    }

    private void blockedClear(String type)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(type, null, null);
    }

    private void blockedSet(final String type, final String key, final String data)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CacheEntry.COLUMN_NAME_KEY, key);
        contentValues.put(CacheEntry.COLUMN_NAME_VALUE, data);

        db.insertWithOnConflict(type, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private void blockedSetAll(String type, Collection<Projection> projections)
    {
        SQLiteDatabase db = getWritableDatabase();

        for (Projection projection : projections)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CacheEntry.COLUMN_NAME_KEY, projection.key);
            contentValues.put(CacheEntry.COLUMN_NAME_VALUE, projection.data);

            db.insertWithOnConflict(type, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    private Projection blockedGet(String type, String key)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                type,
                projection,
                selection,
                new String[]{key},
                null,
                null,
                null);

        Projection projection = new Projection();
        projection.type = type;
        projection.key = key;

        if (cursor.moveToNext())
        {
            projection.data = cursor.getString(cursor.getColumnIndexOrThrow(CacheEntry.COLUMN_NAME_VALUE));
        }

        cursor.close();

        return projection;
    }

    private List<Projection> blockedGetAll(String type)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(type, projection, null, null, null, null, null);

        List<Projection> projections = new ArrayList<>();

        while (cursor.moveToNext())
        {
            Projection projection = new Projection();
            projection.type = type;
            projection.key = cursor.getString(cursor.getColumnIndexOrThrow(CacheEntry.COLUMN_NAME_KEY));
            projection.data = cursor.getString(cursor.getColumnIndexOrThrow(CacheEntry.COLUMN_NAME_VALUE));
            projections.add(projection);
        }

        cursor.close();

        return projections;
    }

    private List<Projection> blockedLike(String type, String startKey)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(type, projection, query, new String[] { startKey + "%" }, null, null, null);

        List<Projection> projections = new ArrayList<>();

        while (cursor.moveToNext())
        {
            Projection projection = new Projection();
            projection.type = type;
            projection.key = cursor.getString(cursor.getColumnIndexOrThrow(CacheEntry.COLUMN_NAME_KEY));
            projection.data = cursor.getString(cursor.getColumnIndexOrThrow(CacheEntry.COLUMN_NAME_VALUE));
            projections.add(projection);
        }

        cursor.close();

        return projections;
    }

    private long blockedCount(String type, String startKey)
    {
        SQLiteDatabase db = getReadableDatabase();
        if (startKey == null || startKey.trim().isEmpty())
            return DatabaseUtils.queryNumEntries(db, type);
        return DatabaseUtils.queryNumEntries(db, type, CacheEntry.COLUMN_NAME_KEY + " like ?", new String[] { startKey });
    }

    FutureTask<Void> setAll(final String type, final Collection<Projection> projections)
    {
        FutureTask<Void> future = new FutureTask<>(new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                blockedSetAll(type, projections);
                return null;
            }
        });

        executor.execute(future);

        return future;
    }

    FutureTask<String> set(final String type, final String key, final String data)
    {
        FutureTask<String> future = new FutureTask<>(new Callable<String>()
        {
            @Override
            public String call() throws Exception
            {
                blockedSet(type, key, data);
                return key;
            }
        });

        executor.execute(future);

        return future;
    }

    FutureTask<List<Projection>> getAll(final String type)
    {
        FutureTask<List<Projection>> future = new FutureTask<>(new Callable<List<Projection>>()
        {
            @Override
            public List<Projection> call() throws Exception
            {
                return blockedGetAll(type);
            }
        });

        executor.execute(future);

        return future;
    }

    FutureTask<List<Projection>> like(final String type, final String startKey)
    {
        FutureTask<List<Projection>> future = new FutureTask<>(new Callable<List<Projection>>()
        {
            @Override
            public List<Projection> call() throws Exception
            {
                return blockedLike(type, startKey);
            }
        });

        executor.execute(future);

        return future;
    }


    FutureTask<Projection> get(final String type, final String key)
    {
        FutureTask<Projection> future = new FutureTask<>(new Callable<Projection>()
        {
            @Override
            public Projection call() throws Exception
            {
                return blockedGet(type, key);
            }
        });

        executor.execute(future);

        return future;
    }

    FutureTask<Long> count(final String type, final String key)
    {
        FutureTask<Long> future = new FutureTask<>(new Callable<Long>()
        {
            @Override
            public Long call() throws Exception
            {
                return blockedCount(type, key);
            }
        });

        executor.execute(future);

        return future;
    }

    public FutureTask<Void> remove(final String type, final String key)
    {
        FutureTask<Void> future = new FutureTask<>(new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                if (key == null)
                    blockedClear(type);
                else
                    blockedRemove(type, key);
                return null;
            }
        });

        executor.execute(future);

        return future;
    }
}
