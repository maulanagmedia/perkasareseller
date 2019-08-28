package gmedia.net.id.perkasareseller.HomePulsa.Service;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import gmedia.net.id.perkasareseller.HomeInfoStok.DetailInfoStok;
import gmedia.net.id.perkasareseller.HomePulsa.OrderPulsa;
import gmedia.net.id.perkasareseller.Register.OtpRegisterActivity;
import gmedia.net.id.perkasareseller.Reset.OtpResetActivity;
import gmedia.net.id.perkasareseller.SideChangePassword.OtpChangePassword;

public class NotificationService extends NotificationListenerService {

    private static final String TAG = "NOTIF";
    Context context;

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        try {

            String pack = sbn.getPackageName();
            String ticker = "";
            if(sbn.getNotification().tickerText !=null) {
                ticker = sbn.getNotification().tickerText.toString();
            }
            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString("android.title");
            String text = extras.getCharSequence("android.text").toString();
            int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);

            Log.i("Package",pack);
            Log.i("Ticker",ticker);
            Log.i("Title",title);
            Log.i("Text",text);

            Intent msgrcv = new Intent("Msg");
            msgrcv.putExtra("package", pack);
            msgrcv.putExtra("ticker", ticker);
            msgrcv.putExtra("title", title);
            msgrcv.putExtra("text", text);

            saveNotif(title, ticker);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveNotif(String title, String text) {

        /*Log.d(TAG, "title: " + title);
        Log.d(TAG, "text: " + text);*/

        String[] separated = text.split(": ");

        if(separated.length > 0){

            text = text.replace(separated[0]+": ", "");

            if(DetailInfoStok.isActive){
                DetailInfoStok.addTambahBalasan(separated[0], text);
            }else if (OrderPulsa.isActive){
                OrderPulsa.addTambahBalasan(separated[0], text);
            }else if(OtpRegisterActivity.isActive){
                OtpRegisterActivity.fillOTP(text);
            }else if(OtpChangePassword.isActive){
                OtpChangePassword.fillOTP(text);
            }else if(OtpResetActivity.isActive){
                OtpResetActivity.fillOTP(text);
            }

            Log.d(TAG, "title: " + separated[0]);
            Log.d(TAG, "text: " + text);
        }
    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");

    }
}