package com.mental_elemental.android.support;

import org.json.JSONException;

public abstract class JsonSerializer<T> implements Serializer<T>
{
    @Override
    public final T deserialize(String string)
    {
        if (string == null)
            return null;

        try
        {
            return deserializeJson(string);
        }
        catch (JSONException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    @Override
    public final String serialize(T object)
    {
        if (object == null)
            return null;

        try
        {
            return serializeJson(object);
        }
        catch (JSONException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    protected abstract T deserializeJson(String string) throws JSONException;
    protected abstract String serializeJson(T object) throws JSONException;
}
