package com.mental_elemental.android.support;

import android.text.Editable;

import androidx.viewpager.widget.ViewPager;

public class AbstractClassFrom
{
    public static abstract class TextWatcher implements android.text.TextWatcher
    {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int before, int count)
        {

        }

        @Override
        public void afterTextChanged(Editable editable)
        {

        }
    }

    public static abstract class OnPageChangeListener implements ViewPager.OnPageChangeListener
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {

        }

        @Override
        public void onPageScrollStateChanged(int state)
        {

        }
    }
}