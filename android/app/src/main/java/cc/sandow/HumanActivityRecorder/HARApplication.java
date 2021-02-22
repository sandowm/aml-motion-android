package cc.sandow.HumanActivityRecorder;

import android.app.Application;

public class HARApplication extends Application {
    private float[][] sendeableArray;
    public float[][] getSendeableArray() {
        return sendeableArray;
    }

    public void setSendeableArray(float[][] sendeableArray) {
        this.sendeableArray = sendeableArray;
    }
}
