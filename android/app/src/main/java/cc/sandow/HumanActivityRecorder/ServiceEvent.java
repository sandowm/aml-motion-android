package cc.sandow.HumanActivityRecorder;
/* Store Event Data needed to pass from Service(s) to main UI thread */

public class ServiceEvent {
    public final String message;

    public ServiceEvent(String message) {
        this.message = message;
    }
}
