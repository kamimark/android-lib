package com.mental_elemental.android.support;

import androidx.annotation.Nullable;

public interface SingleArgumentFunction<R, T>
{
    R call(@Nullable T obj);
}
