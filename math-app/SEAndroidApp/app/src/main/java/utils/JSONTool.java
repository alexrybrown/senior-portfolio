package utils;

import org.json.JSONArray;

public class JSONTool {
    private JSONArray jsonArray;

    public JSONTool() {
        this.jsonArray = null;
    }

    public JSONTool(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }
}
