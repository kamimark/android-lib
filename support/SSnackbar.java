package com.mental_elemental.android.support;

import com.google.android.material.snackbar.Snackbar;

import android.view.View;
import android.widget.TextView;

public class SSnackbar
{
    public static Snackbar make(View root, String text) {
        return make(root, text, Snackbar.LENGTH_INDEFINITE);
    }

    public static Snackbar make(View root, int res) {
        return make(root, res, Snackbar.LENGTH_INDEFINITE);
    }

    public static Snackbar make(View root, int res, int duration) {
        final Snackbar snackbar = Snackbar.make(root, res, duration);
        return patch(snackbar);
    }

    public static Snackbar make(View root, String text, int duration) {
        final Snackbar snackbar = Snackbar.make(root, text, duration);
        return patch(snackbar);
    }

    private static Snackbar patch(final Snackbar snackbar) {
        View view = snackbar.getView();
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                snackbar.dismiss();
            }
        });

        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(100);
        return snackbar;
    }

}
