package com.mental_elemental.android.support;

import androidx.appcompat.app.AppCompatActivity;

public class Fragment extends android.app.Fragment
{
    protected AppCompatActivity getAppCompactActivity()
    {
        return (AppCompatActivity) getActivity();
    }

}
