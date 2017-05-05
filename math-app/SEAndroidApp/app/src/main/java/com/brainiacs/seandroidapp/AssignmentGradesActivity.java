package com.brainiacs.seandroidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import utils.handlers.AssignmentGradeHandler;
import utils.handlers.ClassAssignmentListHandler;
import utils.handlers.HttpHandler;

public class AssignmentGradesActivity extends AppCompatActivity {
    public static LinearLayout studentName, grade;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_grades);

        Intent oldIntent = getIntent();
        ((TextView)findViewById(R.id.assignmentName)).setText(oldIntent.getStringExtra("name"));
        String assignId = Integer.toString(getIntent().getExtras().getInt("id"));
        studentName = (LinearLayout) findViewById(R.id.studentName);
        grade = (LinearLayout) findViewById(R.id.grade);

        AssignmentGradeHandler handler = new AssignmentGradeHandler(
                "assignments/" + assignId + "/student-grades/", "", "Failed to fetch assignments",
                HttpHandler.Method.GET, null, this, null, null);
        handler.execute((Void) null);
    }
}
