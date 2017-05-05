package com.brainiacs.seandroidapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Matthew on 2/21/17.
 * Overview for a class
 */
public class ClassHomeActivity extends AppCompatActivity implements View.OnClickListener{
    private JSONObject classData;
    private ArrayList<JSONObject> assignments_data;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_home);

        //Retrieves class names from selection Screen
        Intent oldIntent = getIntent();
        ((TextView)findViewById(R.id.textView)).setText(oldIntent.getStringExtra(ClassButtonAdapter.className));
        ((TextView)findViewById(R.id.textView)).setTextColor(Color.BLACK);
        try {
            classData = new JSONObject(oldIntent.getStringExtra("classData"));
        } catch (JSONException e) {}

        assignments_data = new ArrayList<>();
        try {
            JSONArray temp_data = classData.getJSONArray("assignments");
            for (int i = 0; i < temp_data.length(); ++i) {
                assignments_data.add(temp_data.getJSONObject(i));
            }
        } catch(JSONException e) {}

        userType = oldIntent.getStringExtra("userType");
        if(userType.equals("teacher")) {
            Button showStudentButton = (Button) findViewById(R.id.StudentButton);
            showStudentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showStudents();
                }
            });

            Button showAssignmentButton = (Button) findViewById(R.id.AssignButton);
            showAssignmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAssignments();
                }
            });

        }
        else{
            LinearLayout showLayout = (LinearLayout) findViewById(R.id.showLayout);
            showLayout.setVisibility(View.GONE);
        }
        try {
            LinearLayout studentList;

            JSONArray assignmentList = classData.getJSONArray("assignments");
            for(int i = 0; i < assignmentList.length(); i++){
                Button assignment = new Button(this);
                assignment.setTextColor(Color.WHITE);
                assignment.setBackgroundColor(Color.BLACK);
                assignment.setGravity(Gravity.CENTER);
                assignment.setText(assignmentList.getJSONObject(i).getString("name"));
                assignment.setPadding(20,20,20,20);
                assignment.setTextSize(20);
                assignment.setId(i);
                assignment.setId(assignmentList.getJSONObject(i).getInt("id"));
                assignment.setOnClickListener(this);

                if(i % 3 == 0) {
                    studentList = (LinearLayout) findViewById(R.id.l1);
                    studentList.addView(assignment);
                }
                else if(i % 2 == 0){
                    studentList = (LinearLayout) findViewById(R.id.l2);
                    studentList.addView(assignment);
                }
                else{
                    studentList = (LinearLayout) findViewById(R.id.l3);
                    studentList.addView(assignment);
                }
            }
        } catch (JSONException e) {}

    }

    private void showStudents(){
        Intent intent = new Intent(this, AddToClassActivity.class);
        intent.putExtra("Type", "students");
        intent.putExtra("id", getIntent().getExtras().getInt("id"));
        intent.putExtra(ClassButtonAdapter.className, getIntent().getStringExtra(ClassButtonAdapter.className));
        try {
            intent.putExtra("classData", classData.getJSONArray("students").toString());
        } catch (JSONException e) {}
        startActivity(intent);
    }

    private void showAssignments(){
        Intent intent = new Intent(this, AddToClassActivity.class);
        intent.putExtra("Type", "assignments");
        intent.putExtra("id", getIntent().getExtras().getInt("id"));
        intent.putExtra(ClassButtonAdapter.className, getIntent().getStringExtra(ClassButtonAdapter.className));
        try {
            intent.putExtra("classData", classData.getJSONArray("assignments").toString());
        } catch (JSONException e) {}
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        if(userType.equals("student")){
            studentOnClick(v);
        }
        else{
            teacherOnClick(v);
        }
    }

    private void teacherOnClick(View v) {
        Intent intent = new Intent(this, AssignmentGradesActivity.class);
            intent.putExtra("id", v.getId());
        try {
            intent.putExtra("name", assignments_data.get(v.getId()).getString("name"));
        } catch (JSONException e) { }
        startActivity(intent);
        finish();
    }

    private void studentOnClick(View v){
        Button button = (Button) v;
        try {
            JSONArray questions_data = null;
            String mathType = "";
            for (JSONObject json : assignments_data) {
                if (json.getInt("id") == button.getId()) {
                    questions_data = json.getJSONArray("questions");
                    mathType = json.getString("math_type").toLowerCase();
                }
            }
            if(mathType.equals("addition")){
                Intent intent = new Intent(this, AdditionActivity.class);
                intent.putExtra("id", v.getId());
                intent.putExtra("questions_data", questions_data.toString());
                startActivity(intent);
                finish();
            }
            else if(mathType.equals("subtraction")){
                Intent intent = new Intent(this, BalloonPoppingActivity.class);
                intent.putExtra("id", v.getId());
                intent.putExtra("questions_data", questions_data.toString());
                startActivity(intent);
                finish();
            }
            else{
                Intent intent = new Intent(this, DuckGameActivity.class);
                intent.putExtra("id", v.getId());
                intent.putExtra("questions_data", questions_data.toString());
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {}
    }
}

