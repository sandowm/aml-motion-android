package cc.sandow.HumanActivityRecorder;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static cc.sandow.HumanActivityRecorder.measurement.sendeableArray;

public class SendDataPage extends AppCompatActivity {
    private TextView status_txt;
    private JSONObject postData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data_page);
        status_txt = (TextView)findViewById(R.id.status);
        JSONObject postData = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://unibe.sandow.cc/my-university.php";
        status_txt.setText(getString(R.string.sendStatus_sending,url));
        try {
            Gson gson = new Gson();
            postData.put("data", new JSONArray(gson.toJson(sendeableArray[0])));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                status_txt.setText(getString(R.string.sendStatus_result, response.toString()));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(jsonObjectRequest);
    }




}