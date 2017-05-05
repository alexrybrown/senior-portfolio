package utils.handlers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import utils.DBTools;

/**
 * Task will run communications to server through async task
 */
public class HttpHandler extends AsyncTask<Void, Void, String> {
    public enum Method {GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE}
    protected String rootUrl;
    protected String apiEndpoint;
    protected Method method;
    protected HashMap<String, String> params;
    protected int responseCode;
    protected Context context;
    protected Intent intent;
    protected String success;
    protected String failure;

    /**
     * Sets up the needed information to use the handler
     * @param apiEndpoint represents the endpoint on the server this connection
     *                    needs to connect to
     * @param method represents whether this is a 'GET', 'POST', 'HEAD', 'OPTIONS',
     *               'PUT', 'DELETE', or 'TRACE'
     * @param params required parameters to be put in the url for the given method
     */
    public HttpHandler(String apiEndpoint, String success, String failure, Method method,
                       HashMap<String, String> params, Context context, Intent intent) {
        this.apiEndpoint = apiEndpoint;
        this.method = method;
        this.params = params;
        this.responseCode = 0;
        this.context = context;
        this.intent = intent;
        this.success = success;
        this.failure = failure;
        this.rootUrl = "https://vast-hollows-88441.herokuapp.com/";
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
            DBTools dbTools = new DBTools(context);
            String token = dbTools.getToken();
            dbTools.close();
            // Check to see if we have a token
            if (!token.isEmpty()) {
                conn.setRequestProperty("Authorization", "Token " + token);
            }

            // If we have params send them to the server
            if(this.method == Method.POST || this.method == Method.PUT) {
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

    @Override
    protected void onPostExecute(String result) {
        if (!result.isEmpty()) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }
        if(!result.equals(failure) && intent != null) {
            context.startActivity(intent);
        }
    }

    /**
     * Changes the hash map of params into a string representation
     * @return string representation of the params
     */
    protected String getParamsString() throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()) {
            if(first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    /**
     * Default method of handling a response.
     * @param conn is the http connection
     * @return a string that is used in the post execute.
     * @throws IOException
     */
    protected String handleResponse(HttpURLConnection conn) throws IOException {
        if(responseCode == HttpURLConnection.HTTP_OK) {
            StringBuffer sb = new StringBuffer("Success");
            String line;
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while((line=br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        } else if(responseCode >= 200 && responseCode < 300) {
            return success;
        } else {
            return failure;
        }
    }
}
