package com.mental_elemental.android.support;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

public class Support
{
    private static final int IMAGE_TAG = 123456789;
    private static Executor executor = Executors.newSingleThreadExecutor();
    private static final Map<String, Dialog> dialogs = new HashMap<>();

    public static Iterable<View> getChildViews(final ViewGroup viewGroup)
    {
        return new Iterable<View>()
        {
            @NonNull
            @Override
            public Iterator<View> iterator()
            {
                return new Iterator<View>()
                {
                    int i = 0;

                    @Override
                    public boolean hasNext()
                    {
                        return i < viewGroup.getChildCount();
                    }

                    @Override
                    public View next()
                    {
                        View view = viewGroup.getChildAt(i);
                        ++i;
                        return view;
                    }
                };
            }
        };
    }

    public static int brighter(int color, float ratio)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] += ratio;
        return Color.HSVToColor(hsv);
    }

    public static AlertDialog createDialog(AlertDialog.Builder builder, final String title)
    {
        if (dialogs.containsKey(title))
            return null;

        builder.setTitle(title);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                dialogs.remove(title);
            }
        });

        AlertDialog dialog = builder.create();
        dialogs.put(title, dialog);

        return dialog;
    }

    public static CharSequence fromDouble(double number)
    {
        long roundedNumber = Math.round(number);
        if (number == roundedNumber)
            return String.valueOf(roundedNumber);

        return String.valueOf(1f * Math.round(number * 100) / 100);
    }

    public static String ordinal(int i)
    {
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100)
        {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];
        }
    }

    public static void displayError(Context context, String title, String error)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setMessage(error)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public static void report(@NonNull Context context, Exception exception)
    {
        exception.printStackTrace();
//        if ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0)
//            try
//            {
//                Crashlytics.logException(exception);
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
    }

    public static boolean equals(String str1, String str2)
    {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String arrayName)
    {
        JSONArray jsonArray = jsonObject.optJSONArray(arrayName);
        if (jsonArray == null)
        {
            jsonArray = new JSONArray();
            JSONObject singleElement = jsonObject.optJSONObject(arrayName);
            if (singleElement != null)
                jsonArray.put(singleElement);
        }

        return jsonArray;
    }

    public static <T> void chain(final Queue<T> arguments, final Chainable<T> chainable)
    {
        if (arguments.isEmpty())
            return;

        T argument = arguments.poll();
        chainable.run(argument, new Runnable()
        {
            @Override
            public void run()
            {
                chain(arguments, chainable);
            }
        }, arguments.isEmpty());
    }

    public static boolean equals(SparseArray<Double> sparseArray, SparseArray<Double> otherSparceArray)
    {
        if (sparseArray.size() != otherSparceArray.size())
            return false;

        for (int i = 0; i < sparseArray.size(); ++i)
        {
            if (sparseArray.keyAt(i) != otherSparceArray.keyAt(i))
                return false;
            int key = sparseArray.keyAt(i);
            if (!sparseArray.get(key).equals(otherSparceArray.get(key)))
                return false;
        }

        return true;
    }

    public static int[] getInts(JSONArray array) throws JSONException
    {
        int[] values = new int[array.length()];
        for (int i = 0; i < array.length(); ++i)
            values[i] = array.getInt(i);
        return values;
    }

    public static double[] getDoubles(JSONArray array) throws JSONException
    {
        double[] values = new double[array.length()];
        for (int i = 0; i < array.length(); ++i)
        {
            if (array.getString(i).equals("X"))
                values[i] = Double.MIN_VALUE;
            else
                values[i] = array.getDouble(i);
        }
        return values;
    }

    public static JSONArray jsonize(double[] doubles) throws JSONException
    {
        JSONArray jsonArray = new JSONArray();
        for (double d : doubles)
            jsonArray.put(d == Double.MIN_VALUE ? "X" : d);
        return jsonArray;
    }

    public static String toString(double value)
    {
        long intValue = Math.round(value);
        if (value == intValue)
            return String.valueOf(intValue);

        return String.valueOf(Math.round(value * 100) / 100);
    }

    public static int compare(long timestamp, long timestamp1)
    {
        long diff = timestamp - timestamp1;
        if (diff < 0)
            return -1;
        else if (diff > 0)
            return 1;
        return 0;
    }

    public static <T> boolean remove(Collection<T> collection, T object)
    {
        for (T obj : collection)
        {
            if (obj.equals(object))
            {
                collection.remove(obj);
                return true;
            }
        }

        return false;
    }

    public static <T> boolean contains(Collection<T> collection, T object)
    {
        for (T obj : collection)
            if (obj.equals(object))
                return true;
        return false;
    }

    public static Long parseLong(String longStr)
    {
        if (longStr == null || longStr.isEmpty())
            return null;

        try
        {
            return Long.parseLong(longStr);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public interface Chainable<T>
    {
        void run(T arg, Runnable completed, boolean lastOne);
    }

    public static void loadImage(ImageView imageView, String imagePath)
    {
        imageView.setTag(IMAGE_TAG, imagePath);

        if (imagePath.toLowerCase().startsWith("http://") || imagePath.toLowerCase().startsWith("https://"))
        {
            loadRemoteImage(imageView, imagePath);
            return;
        }

        File imageFile = new File(imagePath);
        String thumbnailFolderPath = imageFile.getParent() + File.separator + "thumbs";
        File thumbnailFolder = new File(thumbnailFolderPath);

        if (!thumbnailFolder.exists())
            thumbnailFolder.mkdirs();

        File thumbnail = new File(thumbnailFolderPath + File.separator + imageFile.getName());
        if (thumbnail.exists())
        {
            imageView.setImageURI(Uri.fromFile(thumbnail));
            return;
        }

        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        boolean createThumbnail = targetH != 0 && targetW != 0;

        if (targetW == 0)
            targetW = 500;
        if (targetH == 0)
            targetH = 500;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);

        if (createThumbnail)
        {
            try
            {
                thumbnail.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(thumbnail);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        imageView.setImageBitmap(bitmap);
    }

    private static void loadRemoteImage(ImageView imageView, String url)
    {
        File localImage = findCachedImage(imageView.getContext(), url);

        if (localImage.exists())
            loadImage(imageView, localImage.getAbsolutePath());
        else
            downloadImage(imageView, url, localImage);
    }

    private static String sanitizeFilename(String filename)
    {
        return filename.replaceAll("[^a-zA-Z0-9\\._]+", File.separator);
    }

    private static void downloadImage(final ImageView imageView, final String urlString, final File outputFile)
    {
        imageView.setVisibility(View.INVISIBLE);
        executor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    URL url = new URL(urlString);

                    URLConnection conn = url.openConnection();
                    int contentLength = conn.getContentLength();
                    if (contentLength == -1)
                        throw new RuntimeException("Image not accessible: " + urlString);

                    DataInputStream stream = new DataInputStream(url.openStream());

                    byte[] buffer = new byte[contentLength];
                    stream.readFully(buffer);
                    stream.close();

                    outputFile.getParentFile().mkdirs();
                    outputFile.createNewFile();

                    DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
                    fos.write(buffer);
                    fos.flush();
                    fos.close();

                    String tag = (String)imageView.getTag(IMAGE_TAG);
                    if (tag.equals(urlString))
                    {
                        Context context = imageView.getContext();
                        new Handler(context.getMainLooper()).post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                loadImage(imageView, outputFile.getAbsolutePath());
                                imageView.invalidate();
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private static File findCachedImage(Context context, String url)
    {
        if (context == null)
            return null;

        return new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + sanitizeFilename(url));
    }

    public static void openInputDialog(Context context, String title, String actionLabel, String defaultValue, final SingleArgumentFunction<Boolean, String> callable)
    {
        openInputDialog(context, title, actionLabel, defaultValue, callable, null, null);
    }

    public static void openInputDialog(Context context, String title, String actionLabel, String defaultValue, final SingleArgumentFunction<Boolean, String> callable, String neutralActionLabel,final SingleArgumentFunction<Boolean, String> neutral)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final EditText editText = new EditText(context);
        editText.setImeActionLabel(actionLabel, KeyEvent.KEYCODE_ENTER);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setSingleLine();
        editText.setText(defaultValue);

        builder.setView(editText);
        builder.setPositiveButton(actionLabel, null);
        if (neutralActionLabel != null)
            builder.setNeutralButton(neutralActionLabel, null);

        final AlertDialog dialog = Support.createDialog(builder, title);
        if (dialog == null)
            return;

        dialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialogInterface)
            {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View button)
                    {
                        if (callable.call(editText.getText().toString()))
                            dialog.dismiss();
                    }
                });
                if (neutral != null)
                    dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View button)
                        {
                            if (neutral.call(editText.getText().toString()))
                                dialog.dismiss();
                        }
                    });
            }
        });

        dialog.show();

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
            {
                if (actionId != KeyEvent.KEYCODE_ENTER
                        && keyEvent.getKeyCode() != KeyEvent.KEYCODE_ENTER)
                    return false;

                boolean result = callable.call(editText.getText().toString());

                if (result)
                    dialog.dismiss();

                return result;
            }
        });
    }

    public static int indexOf(int[] array, int element)
    {
        for (int i = 0; i < array.length; ++i)
            if (array[i] == element)
                return i;
        return -1;
    }

    public static int indexOf(Object[] standardDice, Object die)
    {
        for (int i = 0; i < standardDice.length; ++i)
            if (standardDice[i] == standardDice)
                return i;
        return -1;
    }

    public static void setDrawable(ImageView view, int res)
    {
        view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), res));
    }

    public static void setColorFilter(ImageView view, int res)
    {
        view.setColorFilter(ContextCompat.getColor(view.getContext(), res));
    }

    public static boolean isEmptyOrNull(String string)
    {
        return string == null || string.trim().isEmpty();
    }

    public abstract static class TextWatcher implements android.text.TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {

        }
    }

}