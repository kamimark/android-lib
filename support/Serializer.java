package com.mental_elemental.android.support;

import androidx.annotation.Nullable;

public interface Serializer<T>
{
    T deserialize(@Nullable String string);

    String serialize(@Nullable T object);
}
