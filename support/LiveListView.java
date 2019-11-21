package com.mental_elemental.android.support;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;

public class LiveListView extends ListView
{
    private static final int THRESHOLD = 10;
    private BaseAdapter adapter;
    private int selection = 0;
    private View selectedView;
    private OnItemClickListener listener;
    private OnScrollListener scrollListener = new OnScrollListener()
    {
        int y = INVALID_POSITION;

        @Override
        public void onScrollStateChanged(AbsListView listView, int scrollState)
        {
            if (scrollingDisabled())
                return;

            if (scrollState == SCROLL_STATE_TOUCH_SCROLL)
            {
                cleanSelection();
            }
            else if (scrollState == SCROLL_STATE_IDLE)
            {
                int newSelection = getFirstVisiblePosition() + ((getLastVisiblePosition() - getFirstVisiblePosition()) / 2);
                if (selection == INVALID_POSITION)
                {
                    setSelection(newSelection);
                    if (listener != null)
                        listener.onItemClick(listView, getChildAt((getLastVisiblePosition() - getFirstVisiblePosition()) / 2), selection, selection);
                }
            }
        }

        @Override
        public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if (scrollingDisabled() || selection != INVALID_POSITION)
                return;

            if (y == INVALID_POSITION)
            {
                y = getCorrectScrollY();
                return;
            }
            else if (Math.abs(y - getCorrectScrollY()) < THRESHOLD)
            {
                return;
            }

            int i = visibleItemCount / 2;
            int position = firstVisibleItem + i;
            if (listener != null)
                listener.onItemClick(listView, getChildAt(i), position, position);
        }
    };

    public LiveListView(Context context)
    {
        super(context);
    }

    public LiveListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LiveListView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void cleanSelection()
    {
        for (View view : Support.getChildViews(this))
        {
            TransitionDrawable transition = (TransitionDrawable) view.getBackground();
            transition.resetTransition();
        }

        selection = INVALID_POSITION;
    }

    public void notifyDataSetChanged()
    {
        adapter.notifyDataSetChanged();
        post(new Runnable()
        {
            @Override
            public void run()
            {
                showSelected();
            }
        });
    }

    @Override
    public void setAdapter(ListAdapter adapter) throws IllegalStateException
    {
        super.setAdapter(adapter);
        this.adapter = (BaseAdapter) adapter;
        post(new Runnable()
        {
            @Override
            public void run()
            {
                showSelected();
            }
        });
    }

    @Override
    public void setSelection(int position)
    {
        cleanSelection();
        selection = position;

        View view = getChildAt(0);
        if (view == null)
            return;

        showSelected();
        smoothScrollToPositionFromTop(selection, getHeight() / 2 - view.getHeight() / 2);
    }

    private boolean scrollingDisabled()
    {
        return getChildCount() == 0 || getChildCount() >= adapter.getCount();
    }

    private void showSelected()
    {
        selectedView = getChildAt(selection - getFirstVisiblePosition());
        if (selectedView == null)
            return;

        post(new Runnable()
        {
            @Override
            public void run()
            {
                selectedView = getChildAt(selection - getFirstVisiblePosition());
                if (selectedView != null)
                {
                    TransitionDrawable transition = (TransitionDrawable) selectedView.getBackground();
                    transition.startTransition(300);
                }
            }
        });
    }

    @Override
    public void setOnItemClickListener(@Nullable OnItemClickListener itemClickListener)
    {
        listener = itemClickListener;

        super.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                setSelection(position);
                if (listener != null)
                    listener.onItemClick(parent, view, position, id);
            }
        });

        setOnScrollListener(listener == null ? null : scrollListener);
    }

    private int getCorrectScrollY()
    {
        return -getChildAt(0).getTop() + getFirstVisiblePosition() * getChildAt(0).getHeight();
    }
}
