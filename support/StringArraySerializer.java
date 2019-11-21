package com.mental_elemental.android.support;

import org.json.JSONArray;
import org.json.JSONException;

public class StringArraySerializer implements Serializer<String[]>
{
    @Override
    public String[] deserialize(String string)
    {
        try
        {
            if (string == null)
                throw new JSONException("null string");
            JSONArray jsonArray = new JSONArray(string);
            String[] result = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); ++i)
                result[i] = jsonArray.getString(i);
            return result;
        }
        catch (JSONException e) {
            System.out.println(e.toString());
            return new String[0];
        }
    }

    @Override
    public String serialize(String[] strings)
    {
        if (strings == null)
            strings = new String[0];

        JSONArray jsonArray = new JSONArray();
        for (String string : strings)
            jsonArray.put(string);
        return jsonArray.toString();
    }
}
