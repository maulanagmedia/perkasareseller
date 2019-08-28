package gmedia.net.id.perkasareseller.HomePulsa.Service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import gmedia.net.id.perkasareseller.HomeInfoStok.DetailInfoStok;
import gmedia.net.id.perkasareseller.HomePulsa.OrderPulsa;

/**
 * Created by Shinmaul on 3/5/2018.
 */

public class USSDService extends AccessibilityService {
    private static final String TAG = "USSDService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent");
        String text = event.getText().toString();
        if(event.getPackageName().toString().trim().toLowerCase().equals("com.android.phone")){

            if(DetailInfoStok.isActive){
                DetailInfoStok.addTambahBalasan("", text);
            }else if (OrderPulsa.isActive){
                OrderPulsa.addTambahBalasan("",text);
            }

            Log.d(TAG, text);
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        //info.flags = AccessibilityServiceInfo.DEFAULT;
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        info.packageNames = new String[]{"com.android.phone"};
        setServiceInfo(info);
        super.onServiceConnected();
    }
}