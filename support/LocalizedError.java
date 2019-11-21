package com.mental_elemental.android.support;

public class LocalizedError extends Exception
{
    public int error;
    public String[] args;

    public LocalizedError(int error, String ... args)
    {
        this.error = error;
        this.args = args;
    }
}
