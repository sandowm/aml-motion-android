package cc.sandow.HumanActivityRecorder;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class SensorJobService extends JobService {
    private static final String LOG_TAG = "SensorJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        //ExceptionHandler.register(this, "https://unibe.sandow.cc/exception.php");
        Intent service = new Intent(getApplicationContext(), SensorService.class);
        try {
            getApplicationContext().startService(service);
        } catch (IllegalStateException e) {
            // TODO Make background Jobs Android Orion compatible
        }
        //Util.scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
