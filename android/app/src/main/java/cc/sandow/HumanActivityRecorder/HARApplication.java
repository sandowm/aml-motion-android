package cc.sandow.HumanActivityRecorder;

import android.app.Application;

public class HARApplication extends Application {
    private float[][] sendeableArray;
    public float[][] getSendeableArray() {
        return sendeableArray;
    }
    private int collectorJobID;

    public void setSendeableArray(float[][] sendeableArray) {
        this.sendeableArray = sendeableArray;
    }

    public void setCollectorJobID(int collectorJobID) {
        this.collectorJobID = collectorJobID;
    }

    public int getCollectorJobID() {
        return collectorJobID;
    }
}
