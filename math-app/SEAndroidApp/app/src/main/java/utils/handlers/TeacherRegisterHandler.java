package utils.handlers;

import android.content.Context;
import android.content.Intent;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class TeacherRegisterHandler extends HttpHandler {
    public TeacherRegisterHandler(String apiEndpoint, String success, String failure, Method method,
                                  HashMap<String, String> params, Context context, Intent intent) {
        super(apiEndpoint, success, failure, method, params, context, intent);
    }

    // Starts the communication process with the server
    protected String doInBackground(Void... params) {
        try {
            URL url = new URL(rootUrl + apiEndpoint);

            // Set the basics of the connection up
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            conn.setDoInput(true);
            conn.setRequestMethod(method.name());

            // If we have params send them to the server
            if(this.method == Method.POST) {
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
}
