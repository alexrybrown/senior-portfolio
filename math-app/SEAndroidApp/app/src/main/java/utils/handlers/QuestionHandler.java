package utils.handlers;

import android.content.Context;
import android.content.Intent;

import com.brainiacs.seandroidapp.AssignmentCreationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;

public class QuestionHandler extends HttpHandler {
    public QuestionHandler(String apiEndpoint, String success, String failure,
                           Method method, HashMap<String, String> params,
                           Context context, Intent intent) {
        super(apiEndpoint, success, failure, method, params, context, intent);
    }

    protected String handleResponse(HttpURLConnection conn) throws IOException {
        if(responseCode == HttpURLConnection.HTTP_CREATED) {
            // Convert the stream to a string
            String line;
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while((line=br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            // Create a JSONObject to get our data
            try {
                JSONObject jsonObject = new JSONObject(sb.toString());
                final int id = jsonObject.getInt("id");
                ((AssignmentCreationActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AssignmentCreationActivity activity = ((AssignmentCreationActivity) context);
                        activity.addQuestionID(id);
                    }
                });
                return success;
            } catch (JSONException e) {
                System.err.print(e.getMessage());
                return failure;
            }
        } else if(responseCode >= 200 && responseCode < 300) {
            return success;
        } else {
            return failure;
        }
    }
}
