package utils.handlers;

import android.content.Context;
import android.content.Intent;

import com.brainiacs.seandroidapp.AssignmentCreationActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AssignmentHandler extends HttpHandler {
    private ArrayList<Integer> questionIds;

    public AssignmentHandler(String apiEndpoint, String success, String failure,
                           Method method, HashMap<String, String> params,
                           Context context, Intent intent) {
        super(apiEndpoint, success, failure, method, params, context, intent);
    }

    protected String doInBackground(Void... params) {
        AssignmentCreationActivity activity = ((AssignmentCreationActivity) context);
        while (activity.getQuestionIDs().size() != activity.getQuestions().size()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
        questionIds = activity.getQuestionIDs();
        return super.doInBackground(params);
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

        for (Integer id : questionIds) {
            result.append("&");
            result.append(URLEncoder.encode("questions", "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(id.toString(), "UTF-8"));
        }

        return result.toString();
    }
}
