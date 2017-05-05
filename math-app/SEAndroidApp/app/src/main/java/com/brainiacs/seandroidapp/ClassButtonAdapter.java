package com.brainiacs.seandroidapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import utils.DBTools;
import utils.handlers.HttpHandler;


/**
 * Created by Matthew on 2/21/17.
 * This class sets up a grid view of buttonNames
 * on the teacher dashboard.
 * Its necessary for the class buttonNames
 */

public class ClassButtonAdapter extends BaseAdapter {
    public static final String className = "className";
    private static ArrayList<JSONObject> classes_data;

    //RGB Values need to be individual ints
    private int[] colorArray = {66,149,244,244,66,191,13,219,61,237,22,7,214,7,237};
    private Context mContext;
    private ArrayList<String> buttonNames = new ArrayList<>();
    private ArrayList<Integer> buttonIDs = new ArrayList<>();
    private int i = 0;

    public ClassButtonAdapter(Context c){
        mContext = c;
        initializeClasses(c);
    }

    //Returns length of the adapter
    @Override
    public int getCount() {
        return buttonNames.size();
    }

    //Returns null, is not needed for this class
    @Override
    public Object getItem(int position) {
        return null;
    }

    //Returns position in the
    @Override
    public long getItemId(int position) {
        return position;
    }

    //Sets up buttonNames on grid.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        Button btn;
        if (convertView == null) {
            btn = new Button(mContext);
            btn.setLayoutParams(new GridView.LayoutParams(650, 250));
        }
        else{
            btn = (Button) convertView;
        }

        btn.setText(buttonNames.get(position));
        try {
            btn.setId(buttonIDs.get(position));
        } catch (IndexOutOfBoundsException e) {}

        //---------------- Apply RGB Values here -------------------
        btn.setBackgroundColor(Color.rgb(colorArray[i], colorArray[i+1], colorArray[i+2]));
        if (i + 2 == colorArray.length - 1) {
            i = 0;
        }
        else {
            i = i + 3;
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((Button) v).getText().toString().equals(v.getContext().getString(R.string.Create_New_Class))){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.New_class_name);
                    final EditText input = new EditText(mContext);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    final Activity activity = (Activity) v.getContext();
                    // Set up the buttonNames
                    builder.setPositiveButton(R.string.Create, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = input.getText().toString();
                            if (!name.isEmpty()) {
                                buttonNames.remove(activity.getString(R.string.Create_New_Class));
                                buttonNames.add(input.getText().toString());
                                buttonNames.add(mContext.getString(R.string.Create_New_Class));
                                HashMap<String, String> params = new HashMap<>();
                                params.put(activity.getString(R.string.class_name), name);
                                Intent intent = new Intent(mContext, DashboardActivity.class);
                                activity.finish();
                                HttpHandler handler = new HttpHandler(
                                        activity.getString(R.string.classes_url), "",
                                        activity.getString(R.string.failed_class_creation), HttpHandler.Method.POST,
                                        params, activity, intent);
                                // Execute the task, and forward to next activity if successful
                                handler.execute((Void) null);
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
                else {
                    Intent intent = new Intent(mContext, ClassHomeActivity.class);
                    intent.putExtra(className, ((Button) v).getText().toString());
                    intent.putExtra("classData", classes_data.get(pos).toString());
                    DBTools dbTools = new DBTools(mContext);
                    if (dbTools.isTeacher()) {
                        intent.putExtra("userType", "teacher");
                    }
                    else{
                        intent.putExtra("userType", "student");
                    }
                    dbTools.close();
                    intent.putExtra("id", v.getId());
                    mContext.startActivity(intent);
                }
            }
        });
        return btn;
        }

    private void initializeClasses(Context c){
        classes_data = DashboardActivity.getClassData();

        for (int i = 0; i < classes_data.size(); ++i) {
            try {
                buttonNames.add(classes_data.get(i).getString("name"));
                buttonIDs.add(classes_data.get(i).getInt("id"));
            } catch(JSONException e) {}
        }

        DBTools dbTools = new DBTools(c);
        if (dbTools.isTeacher()) {
            buttonNames.add(c.getString(R.string.Create_New_Class));
        }
        dbTools.close();
    }
}
