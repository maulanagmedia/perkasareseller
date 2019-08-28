package gmedia.net.id.perkasareseller.HomePulsa.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import gmedia.net.id.perkasareseller.HomeInfoStok.DetailInfoStok;
import gmedia.net.id.perkasareseller.HomePulsa.OrderPulsa;
import gmedia.net.id.perkasareseller.Register.OtpRegisterActivity;
import gmedia.net.id.perkasareseller.Reset.OtpResetActivity;
import gmedia.net.id.perkasareseller.SideChangePassword.OtpChangePassword;

/**
 * Created by Shinmaul on 3/9/2018.
 */

public class SmsReceiverService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus=(Object[])intent.getExtras().get("pdus");
        SmsMessage shortMessage=SmsMessage.createFromPdu((byte[]) pdus[0]);

        Log.d("SMSReceiver","SMS message sender: "+
                shortMessage.getOriginatingAddress());
        Log.d("SMSReceiver","SMS message text: "+
                shortMessage.getDisplayMessageBody());

        if(DetailInfoStok.isActive){
            DetailInfoStok.addTambahBalasan(shortMessage.getOriginatingAddress(), shortMessage.getDisplayMessageBody());
        }else if (OrderPulsa.isActive){
            OrderPulsa.addTambahBalasan(shortMessage.getOriginatingAddress(), shortMessage.getDisplayMessageBody());
        }else if(OtpRegisterActivity.isActive){
            OtpRegisterActivity.fillOTP(shortMessage.getDisplayMessageBody());
        }else if(OtpChangePassword.isActive){
            OtpChangePassword.fillOTP(shortMessage.getDisplayMessageBody());
        }else if(OtpResetActivity.isActive){
            OtpResetActivity.fillOTP(shortMessage.getDisplayMessageBody());
        }
    }
}
