package cc.sandow.HumanActivityRecorder;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.util.Log;
import android.content.Context;

import org.greenrobot.eventbus.EventBus;



/* Put any functions used globally or by more than one other class here */
public class Util {
    private static final String LOG_TAG = "Util";
    /* Get an event to the UI process if it exists. Types of events are:
       - M - Simple message text possibly to be displayed in log style
     */
    static void sendToUI(String eventType, String message) {
        try {
            EventBus.getDefault().post(new ServiceEvent(message));
        } catch (Exception e) {
            Log.i(LOG_TAG, "No-UI attached: " + message);
        }
    }
    // schedule the start of the service every 10 - 30 seconds
    public static int scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, SensorJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(100); // wait before start
        builder.setOverrideDeadline(3 * 1000); // maximum delay
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        return(jobScheduler.schedule(builder.build()));
    }
    public static void unschedule(Context context,int jobID) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.cancel(jobID);
    }

}
