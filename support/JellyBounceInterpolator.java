package com.mental_elemental.android.support;

import android.view.animation.DecelerateInterpolator;

public class JellyBounceInterpolator extends DecelerateInterpolator
{
    @Override
    public float getInterpolation(float ratio)
    {
        ratio = super.getInterpolation(ratio);

        if (ratio == 0.0f || ratio == 1.0f) return ratio;
        else
        {
            float p = 0.6f;
            float two_pi = (float) (Math.PI * 2.7f);
            return (float) Math.pow(2.0f, -10.0f * ratio) * (float) Math.sin((ratio - (p / 5.0f)) * two_pi / p) + 1.0f;
        }
    }
}
