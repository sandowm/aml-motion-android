package cc.sandow.HumanActivityRecorder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.greenrobot.eventbus.EventBus;



/* Put any functions used globally or by more than one other class here */
public class Util {
    private static String CHANNEL_ID = "1";
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
        Context context = HARApplication.getAppContext();
        HARApplication.setLastMessage(message);
    }
    // schedule the start of the service every 10 - 30 seconds
    public static int scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, SensorJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(2000); // wait 2s before start
        builder.setOverrideDeadline(4 * 1000); // maximum delay 4s
        builder.setRequiresCharging(false);
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        return(jobScheduler.schedule(builder.build()));
    }
    public static void unschedule(Context context,int jobID) {
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.cancel(jobID);
    }

    public static void signalStartRecording (Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("HumanActivityRecorder")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("Recording Started...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTimeoutAfter(60000); // 60000 == 1 minute
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(4, builder.build());
        vibrate(context,500,600);
    }
    public static void signalStoppedRecording (Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("HumanActivityRecorder")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("Recording finished")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTimeoutAfter(60000); // 60000 == 1 minute
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(4, builder.build());
        vibrate(context,1000,1000,1000,1000);
    }

    public static void vibrate(Context context, Integer... times) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        for (Integer t : times) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(t, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(t);
            }
        }
    }

    public static void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Context context = HARApplication.getAppContext();

            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
