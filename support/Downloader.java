package com.mental_elemental.android.support;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Downloader
{
    public interface OnFileDownloaded {
        void finished(String url, String fileLocation);
    }

    private static Downloader downloader = new Downloader();
    private static Executor executor = Executors.newFixedThreadPool(3);
    private static Map<String, List<OnFileDownloaded>> allEvents = new HashMap<>();

    private Downloader() {

    }

    private void raiseEvents(String url, String localFile) {
        for (OnFileDownloaded event : allEvents.remove(url))
            event.finished(url, localFile);
    }

    private void _download(final Context context, final String urlString, OnFileDownloaded event)
    {
        final String filename = urlString.replaceAll("[^a-zA-Z0-9\\._]+", File.separator);
        final File cachedFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + filename);

        if (cachedFile.exists()) // Already downloaded
        {
            event.finished(urlString, cachedFile.getAbsolutePath());
            return;
        }

        List<OnFileDownloaded> events = allEvents.get(urlString);
        if (events != null) { // Currently downloading
            events.add(event);
            return;
        }

        events = new ArrayList<>();
        allEvents.put(urlString, events);
        events.add(event);

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
                        throw new RuntimeException("File not accessible: " + urlString);

                    DataInputStream stream = new DataInputStream(url.openStream());

                    byte[] buffer = new byte[contentLength];
                    stream.readFully(buffer);
                    stream.close();

                    cachedFile.getParentFile().mkdirs();
                    cachedFile.createNewFile();

                    DataOutputStream fos = new DataOutputStream(new FileOutputStream(cachedFile));
                    fos.write(buffer);
                    fos.flush();
                    fos.close();

                    new Handler(context.getMainLooper()).post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            raiseEvents(urlString, filename);
                        }
                    });

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    new Handler(context.getMainLooper()).post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            raiseEvents(urlString, null);
                        }
                    });
                }
            }
        });
    }

    public static void download(final Context context, final String urlString, OnFileDownloaded event) {
        downloader._download(context, urlString, event);
    }
}
