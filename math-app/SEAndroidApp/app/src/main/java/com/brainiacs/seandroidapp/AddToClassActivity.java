package com.brainiacs.seandroidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;


import utils.handlers.ClassAssignmentListHandler;
import utils.handlers.HttpHandler;
import utils.handlers.TeacherStudentListHandler;

public class AddToClassActivity extends AppCompatActivity {
    public static JSONArray classData;
    public static LinearLayout nameContainer, checkboxContainer;
    private String addType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_class);


        Intent oldIntent = getIntent();
        addType = oldIntent.getStringExtra("Type");
        try {
            classData = new JSONArray(oldIntent.getStringExtra("classData"));
        } catch (JSONException e) {}
        ((TextView) findViewById(R.id.typeOfAddButton)).setText("Add " + addType + " to this class");
        //Retrieves class names from selection Screen

        nameContainer = (LinearLayout) findViewById(R.id.layout1);
        checkboxContainer = (LinearLayout) findViewById(R.id.layout2);
        Button mSubmitButton = (Button) findViewById(R.id.SubmitButton);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitChanges();
            }
        });

        if(addType.equals("students")){
            getStudentInfo();
        }
        if(addType.equals("assignments")){
            getAssignmentInfo();
        }
    }

    //pulls data from teacher and checks any students that are already in the class
    private void getStudentInfo(){
        TeacherStudentListHandler handler = new TeacherStudentListHandler(
                getString(R.string.teachers_url), "", "Failed to fetch students",
                HttpHandler.Method.GET, null, this, null, null);
        handler.execute((Void) null);
    }

    //Pulls data from teacher assignments and checks any assignments already in the class
    private void getAssignmentInfo(){
        ClassAssignmentListHandler handler = new ClassAssignmentListHandler(
                getString(R.string.teachers_url) + getString(R.string.assignments_url), "", "Failed to fetch assignments",
                HttpHandler.Method.GET, null, this, null, null);
        handler.execute((Void) null);
    }

    private void submitChanges(){
        if(addType.equals("students")){
            postStudentInfo();
        }
        if(addType.equals("assignments")){
            postAssignmentInfo();
        }
    }

    private void postStudentInfo(){
        int id = getIntent().getExtras().getInt("id");
        Intent intent = new Intent(this, DashboardActivity.class);
        TeacherStudentListHandler handler = new TeacherStudentListHandler(
                getString(R.string.classes_url) + id + "/", "", "Failed to post students",
                HttpHandler.Method.PUT, null, this, intent, getIntent().getStringExtra(ClassButtonAdapter.className));
        handler.execute((Void) null);
    }

    private void postAssignmentInfo(){
        int id = getIntent().getExtras().getInt("id");
        Intent intent = new Intent(this, DashboardActivity.class);
        ClassAssignmentListHandler handler = new ClassAssignmentListHandler(
                getString(R.string.classes_url) + id + "/", "", "Failed to post assignments",
                HttpHandler.Method.PUT, null, this, intent, getIntent().getStringExtra(ClassButtonAdapter.className));
        handler.execute((Void) null);
    }
}
