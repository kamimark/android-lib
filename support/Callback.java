package com.mental_elemental.android.support;

import androidx.annotation.Nullable;

public interface Callback<T>
{
    void called(@Nullable T obj);
}
