package utils.handlers;

import android.content.Context;
import android.content.Intent;
import android.widget.GridView;

import com.brainiacs.seandroidapp.ClassButtonAdapter;
import com.brainiacs.seandroidapp.DashboardActivity;
import com.brainiacs.seandroidapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import utils.JSONTool;

public class ClassesHandler extends HttpHandler {
    private JSONTool jsonTool;

    public ClassesHandler(String apiEndpoint, String success, String failure,
                          Method method, HashMap<String, String> params,
                          Context context, Intent intent, JSONTool jsonTool) {
        super(apiEndpoint, success, failure, method, params, context, intent);
        this.jsonTool = jsonTool;
    }

    protected String handleResponse(HttpURLConnection conn) throws IOException {
        if(responseCode == HttpURLConnection.HTTP_OK) {
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
                jsonTool.setJsonArray(new JSONArray(sb.toString()));
                final DashboardActivity activity = ((DashboardActivity) context);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<JSONObject> classesData = new ArrayList<>();
                        try {
                            for (int i = 0; i < jsonTool.getJsonArray().length(); ++i) {
                                classesData.add(jsonTool.getJsonArray().getJSONObject(i));
                            }
                        } catch(JSONException e) {}

                        DashboardActivity.setClassesData(classesData);

                        GridView gridView = (GridView) activity.findViewById(R.id.gridview);
                        gridView.setAdapter(new ClassButtonAdapter(activity));

                        activity.setGridView(gridView);
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

    @Override
    protected void onPostExecute(String result) {
        if(!result.equals(failure) && intent != null) {
            context.startActivity(intent);
        }
    }
}
