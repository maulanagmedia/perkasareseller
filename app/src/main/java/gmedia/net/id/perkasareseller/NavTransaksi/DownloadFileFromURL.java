package gmedia.net.id.perkasareseller.NavTransaksi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import gmedia.net.id.perkasareseller.BuildConfig;
import gmedia.net.id.perkasareseller.R;

public class DownloadFileFromURL extends AsyncTask<String, Integer, String> {

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder build;
    private File fileurl;
    int id = 123;
    OutputStream output;
    private Context context;
    private String selectedDate;
    private String ts = "";

    public DownloadFileFromURL(Context context, String selectedDate) {
        this.context = context;
        this.selectedDate = selectedDate;

    }

    protected void onPreExecute() {
        super.onPreExecute();
        ts = selectedDate;


        //CustomToast.showToast(context,msg);
    }

    @Override
    protected String doInBackground(String... f_url) {

        int count;
        ts = selectedDate;

        try {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            int lenghtOfFile = conection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);
            // Output stream
            fileurl = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), ts + ".pdf");
            output = new FileOutputStream(fileurl);
            byte[] data = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                int cur = (int) ((total * 100) / lenghtOfFile);

                publishProgress(Math.min(cur, 100));
                if (Math.min(cur, 100) > 98) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Log.d("Failure", "sleeping failure");
                    }
                }
                Log.i("currentProgress", "currentProgress: " + Math.min(cur, 100) + "\n " + cur);

                output.write(data, 0, count);
            }

            output.flush();

            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        //build.setProgress(100, progress[0], false);
        //mNotifyManager.notify(id, build.build());
        super.onProgressUpdate(progress);
    }

    @Override
    protected void onPostExecute(String file_url) {

        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",fileurl);

        Intent openFile = new Intent(Intent.ACTION_VIEW, uri);
        openFile.setDataAndType(uri, "application/pdf");
        openFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent p = PendingIntent.getActivity(context, 0, openFile, 0);

        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        build = new NotificationCompat.Builder(context);
        build.setContentTitle("Download")
                .setContentText("Download in progress")
                .setChannelId(id + "")
                .setAutoCancel(false)
                .setDefaults(0)
                .setContentIntent(p)
                .setSmallIcon(R.drawable.ic_menu_download);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id + "",
                    "Social Media Downloader",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("no sound");
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(false);
            mNotifyManager.createNotificationChannel(channel);

        }
        build.setProgress(100, 0, false);
        mNotifyManager.notify(id, build.build());
        String msg = "Download started";

        build.setContentText("Download complete");
        build.setProgress(0, 0, false);

        mNotifyManager.notify(id, build.build());
    }
}
