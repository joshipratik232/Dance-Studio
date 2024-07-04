package com.example.apptest.downloadBackground;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.apptest.activities.Utils;
import com.example.apptest.cache.Cache;
import com.example.apptest.cache.Encryption;
import com.example.apptest.model.downloaded.VideoModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import timber.log.Timber;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static com.example.apptest.activities.Utils.desc;
import static com.example.apptest.activities.Utils.name;

public class TestService extends Service {
    private static Notification notification;
    @SuppressLint("StaticFieldLeak")
    private static NotificationCompat.Builder notificationBuilder;
    private static NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        String channelId = "id";
        Timber.d("TestService:::::::> oncreate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel();
        }
        notificationBuilder = new NotificationCompat.Builder(this, channelId);
        notification = notificationBuilder.setOngoing(true)
                .setPriority(PRIORITY_MIN)
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(101, notification);
        /*final DownloadTask downloadTask = new DownloadTask(getApplicationContext());
        downloadTask.execute(Utils.url);*/
        DownloadManager req = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        req.enqueue(downloadFile(Utils.url, getApplicationContext()));
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                stopSelf();
            }

            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                startForeground(101, notification);
            }
        });
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.d("TestService:::::> onDestroy");
        super.onDestroy();
    }

    /*public static class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context context;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(String... sUrl) {

            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                String password = "Rasik@987";
                String username = "ACGHOST/rjoshi";
                String userpass = username + ":" + password;
                String basicAuth = "Basic :" + new String(Base64.getEncoder().encode(userpass.getBytes()));
                System.out.println(basicAuth);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", basicAuth);

                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();
                File file = new File(context.getExternalFilesDir(
                        Environment.getDataDirectory().getAbsolutePath()),
                        "DStudio/" + name + ".mp4");
                output = new FileOutputStream(file);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW);

                notificationChannel.setDescription("no sound");
                notificationChannel.setSound(null, null);
                notificationChannel.enableLights(false);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }


            notificationBuilder = new NotificationCompat.Builder(context, "id")
                    .setSmallIcon(R.drawable.ic_get_app_24px)
                    .setContentTitle(name)
                    .setContentText(desc)
                    .setDefaults(0)
                    .setAutoCancel(true);
            notificationManager.notify(0, notificationBuilder.build());
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            //Log.d("###", String.valueOf(progress[0]));
            notificationBuilder.setProgress(100, progress[0], false);
            notificationBuilder.setContentText("Downloaded: " + progress[0] + "%");
            notificationManager.notify(0, notificationBuilder.build());
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null)
                Log.d("###", result);
            else {
                try {
                    Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                    notificationManager.cancelAll();
                    //decrypt();
                    saveToCache();
                    //encrypt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                context.stopService(new Intent(context, TestService.class));
            }
        }

        private void encrypt() throws Exception {
            File file = new File(context.getExternalFilesDir(
                    Environment.getDataDirectory().getAbsolutePath()),
                    "DStudio");
            if (file.listFiles() != null) {
                int i = file.listFiles().length - 1;
                String temp = file.listFiles()[i].getName().substring(0, file.listFiles()[i].getName().length() - 4);
                File file2 = new File(context.getExternalFilesDir(
                        Environment.getDataDirectory().getAbsolutePath() + File.separator + "Cache2"),
                        temp);
                Encryption.enc(
                        "kdjfhAS5D4ASsa3d", "as1d2wS21DASfdg3",
                        new FileInputStream(file.listFiles()[i]), new FileOutputStream(file2)
                );
                file.listFiles()[i].delete();
            }
        }

        *//*private void decrypt() throws Exception {
            File cache2 = new File(context.getExternalFilesDir(
                    Environment.getDataDirectory().getAbsolutePath()),
                    "Cache2");
            if (cache2.listFiles() != null) {
                int i = cache2.listFiles().length - 1;
                while (i != -1) {
                    File file = new File(context.getExternalFilesDir(
                            Environment.getDataDirectory().getAbsolutePath()),
                            "DStudio/"+name+".mp4");
                    Encryption.dec("kdjfhAS5D4ASsa3d", "as1d2wS21DASfdg3",
                            new FileInputStream(cache2.listFiles()[i]), new FileOutputStream(file));
                    cache2.listFiles()[i].delete();
                    i = cache2.listFiles().length - 1;
                }
            }
        }*//*

        private void saveToCache() throws IOException {
            Timber.d("saveToCache() called");
            File file = new File(context.getExternalFilesDir(
                    Environment.getDataDirectory().getAbsolutePath()),
                    "DStudio");
            File cacheFile = new File(context.getExternalFilesDir(
                    Environment.getDataDirectory().getAbsolutePath()),
                    "Cache");

            Cache cache = new Cache();
            int i = 0;
            List<VideoModel> list = new ArrayList<>();
            while (i != file.listFiles().length) {
                VideoModel videoModel = new VideoModel();
                videoModel.setVideoName(file.listFiles()[i].getName());
                videoModel.setFilePath(file.listFiles()[i].getAbsolutePath());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Bitmap a = ThumbnailUtils.createVideoThumbnail(file.listFiles()[i], new Size(100, 60),
                            new CancellationSignal());
                    videoModel.setVideoThumb(Utils.encodeBitmap(a));
                } else {
                    Bitmap a = ThumbnailUtils.createVideoThumbnail(file.listFiles()[i].getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
                    videoModel.setVideoThumb(Utils.encodeBitmap(a));
                }
                list.add(videoModel);
                i = i + 1;
            }
            cache.saveObject(list, cacheFile);
        }
    }*/


    public DownloadManager.Request downloadFile(String url, Context context) {
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(url).buildUpon()
                .scheme("http").build());
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        down.setAllowedOverRoaming(false);
        down.setTitle(name);
        down.setDescription(desc);
        down.setDestinationInExternalFilesDir(context, Environment.getDataDirectory().getPath() + File.separator + "DStudio"
                , name + ".mp4");
        return down;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        NotificationChannel chan = new NotificationChannel("my_service",
                "My Background Service", NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return "my_service";
    }

    private void encrypt() throws Exception {
        File file = new File(getApplicationContext().getExternalFilesDir(
                Environment.getDataDirectory().getAbsolutePath()),
                "DStudio");

        final File[] sortedByDate = file.listFiles();

        if (sortedByDate != null && sortedByDate.length > 1) {
            Arrays.sort(sortedByDate, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {
                    return (int) (Math.max(object1.lastModified(), object2.lastModified()));
                }
            });
        }

        if (file.listFiles() != null) {
            int i = file.listFiles().length - 1;
            String temp = file.listFiles()[i].getName().substring(0, file.listFiles()[i].getName().length() - 4);
            File file2 = new File(getApplicationContext().getExternalFilesDir(
                    Environment.getDataDirectory().getAbsolutePath() + File.separator + "Cache2"),
                    temp);
            Encryption.enc(
                    "kdjfhAS5D4ASsa3d", "as1d2wS21DASfdg3",
                    new FileInputStream(file.listFiles()[i]), new FileOutputStream(file2)
            );
            file.listFiles()[i].delete();
        }
    }

    private void saveToCache() throws IOException {
        Timber.d("saveToCache() called");
        File file = new File(getApplicationContext().getExternalFilesDir(
                Environment.getDataDirectory().getAbsolutePath()),
                "DStudio");
        File cacheFile = new File(getApplicationContext().getExternalFilesDir(
                Environment.getDataDirectory().getAbsolutePath()),
                "Cache");

        Cache cache = new Cache();
        int i = file.listFiles().length - 1;
        List<VideoModel> list;
        if (cache.getObject(cacheFile) == null) {
            list = new ArrayList<>();
        } else {
            list = cache.getObject(cacheFile);
        }
        VideoModel videoModel = new VideoModel();
        videoModel.setVideoName(file.listFiles()[i].getName());
        videoModel.setFilePath(file.listFiles()[i].getAbsolutePath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Bitmap a = ThumbnailUtils.createVideoThumbnail(file.listFiles()[i], new Size(100, 60),
                    new CancellationSignal());
            videoModel.setVideoThumb(Utils.encodeBitmap(a));
        } else {
            Bitmap a = ThumbnailUtils.createVideoThumbnail(file.listFiles()[i].getAbsolutePath(), MediaStore.Images.Thumbnails.MINI_KIND);
            videoModel.setVideoThumb(Utils.encodeBitmap(a));
        }
        list.add(videoModel);
        cache.saveObject(list, cacheFile);
    }
}
