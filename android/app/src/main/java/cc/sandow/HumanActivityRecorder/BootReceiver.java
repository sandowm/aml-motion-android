package cc.sandow.HumanActivityRecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    static final String LOG_TAG = HomeActivity.class.getSimpleName();
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO What do we really want to do after a reboot??
        // Util.scheduleJob(context);
        Log.e(LOG_TAG, "BootReceiver has started SensorService");
    }
}
