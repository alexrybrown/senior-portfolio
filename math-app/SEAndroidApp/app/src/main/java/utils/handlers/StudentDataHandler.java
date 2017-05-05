package utils.handlers;

import android.content.Context;
import android.content.Intent;

import com.brainiacs.seandroidapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import utils.DBTools;

public class StudentDataHandler extends HttpHandler {
    public StudentDataHandler(String apiEndpoint, String success, String failure, Method method,
                              HashMap<String, String> params, Context context, Intent intent) {
        super(apiEndpoint, success, failure, method, params, context, intent);
    }

    protected String doInBackground(Void... params) {
        try {
            URL url = new URL(rootUrl + apiEndpoint);

            // Set the basics of the connection up
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            conn.setDoInput(true);
            conn.setRequestMethod(method.name());
            if (!intent.getStringExtra(context.getString(R.string.token)).isEmpty()) {
                conn.setRequestProperty("Authorization", "Token " + intent.getExtras().getString(context.getString(R.string.token)));
            }

            // If we have params send them to the server
            if(method.name().equals("POST")) {
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getParamsString());
                writer.flush();
                writer.close();
                os.close();
            }

            // Get the response code
            responseCode = conn.getResponseCode();

            // Handle the response
            return this.handleResponse(conn);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * Default method of handling a response.
     * @param conn is the http connection
     * @return a string that is used in the post execute.
     * @throws IOException
     */
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
                JSONObject json = new JSONObject(sb.toString());
                int id = json.getInt(context.getString(R.string.user_id));
                String token = intent.getExtras().getString(context.getString(R.string.token));
                String username = json.getString(context.getString(R.string.username));
                // Store the user in the database
                DBTools dbTools = new DBTools(context);
                dbTools.createUser(id, token, false, username);
                dbTools.close();
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
