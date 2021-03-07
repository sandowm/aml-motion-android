package cc.sandow.HumanActivityRecorder;

import android.app.Application;
import android.content.Context;

public class HARApplication extends Application {
    private float[][] sendeableArray;



    public float[][] getSendeableArray() {
        return sendeableArray;
    }
    private int collectorJobID;
    private static Context appContext;
    private static String lastMessage = ""; // Save the last message, so that the gui can show it, once it comes back online

    public static String getLastMessage() {
        return lastMessage;
    }

    public static void setLastMessage(String lastMessage) {
        HARApplication.lastMessage = lastMessage;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void setAppContext(Context appContext) {
        HARApplication.appContext = appContext;
    }


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
