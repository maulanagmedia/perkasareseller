package gmedia.net.id.perkasareseller.NavHome.Adapter;

/**
 * Created by Shin on 2/28/2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ImageUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import gmedia.net.id.perkasareseller.R;

public class HeaderSliderAdapter extends PagerAdapter {

    private Context context;
    private List<CustomItem> resource;
    private int maxSlide;
    private ProgressDialog progressDialog;

    public HeaderSliderAdapter(Context context, List<CustomItem> resource) {
        this.context = context;
        this.resource = resource;
        maxSlide = 8;
    }

    @Override
    public int getCount() {
        if( maxSlide > resource.size()){
            return resource.size();
        }else{
            return maxSlide;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.slider_pager_item, container, false);

        final CustomItem data = resource.get(position);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);

        ImageUtils iu = new ImageUtils();
        if(data.getItem2() == null){
            iu.LoadRealImage(context, data.getItem6(), imageView);
        }else{
            iu.LoadRealImage(context, data.getItem2(), imageView);
        }

        container.addView(itemView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(data.getItem4().equals("")){

                    try {
                        new DownloadFileFromURL().execute(data.getItem2());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    /*Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(data.getItem2()), "image*//*");
                    context.startActivity(intent);*/
                }else{
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getItem4()));
                    context.startActivity(browserIntent);
                }

            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        private File f;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "downloadedfile.jpg");
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream("/sdcard/downloadedfile.jpg");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            try {

                String imagePath = String.valueOf(FileProvider.getUriForFile(context, context.getPackageName() + ".provider", f));
                // setting downloaded into image view
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(imagePath), "image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        private void showDialog(){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading file. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        private void dismissDialog(){
            progressDialog.dismiss();
        }
    }
}
