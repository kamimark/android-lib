package com.mental_elemental.android.support;

public interface ActivityAnimations
{
    void enterAnimation();
    void reenterAnimation();
    void exitAnimation(Runnable onCompleted);
    void finishAnimation(Runnable onCompleted);
}
