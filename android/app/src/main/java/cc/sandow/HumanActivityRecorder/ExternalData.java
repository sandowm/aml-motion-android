package cc.sandow.HumanActivityRecorder;


import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;

interface RepositoryCallback<T> {
    void onComplete(Result<T> result);
}

public class ExternalData {
    private URL url;
    private final Executor executor;

    public ExternalData(Executor executor) {
        try {
            url = new URL("https://unibe.sandow.cc/my-university.php");
        } catch(MalformedURLException e){
            url = null;
        }
        this.executor = executor;
    }

    public void sendData(
            final float[][] sendeableArray,
            final RepositoryCallback<HttpResult> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result<HttpResult> result = makeSyncJsonPost(sendeableArray);
                callback.onComplete(result);
            }
        });
    }
    public HttpResult makeSyncJsonPost(float[][] sendeableArray) {

        HttpResult httpResult = new HttpResult();
        Gson gson = new Gson();
        String postingString = gson.toJson(sendeableArray);
        byte[] input = new byte[0];
        try {
            input = postingString.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
        } catch (IOException e) {
            // status_txt.setText(getString(R.string.sendStatus_result, e.getMessage()));
            httpResult.text=e.getMessage();
            e.printStackTrace();
            return httpResult;
        }

        try (OutputStream os = con.getOutputStream()) {
            os.write(input, 0, input.length);
        } catch(Exception e) {
            //status_txt.setText(getString(R.string.sendStatus_result, e.toString()));
            httpResult.text=e.getMessage();
            e.printStackTrace();
            return httpResult;
        }
        httpResult.text = "SupiDupi";
        return httpResult;

    }
}
