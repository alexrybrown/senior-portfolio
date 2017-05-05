package com.brainiacs.seandroidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import utils.DBTools;

public class LoginSelectionActivity extends AppCompatActivity {

    //Finds button IDs on create and ready for button input.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_selection);

        DBTools dbTools = new DBTools(this);
        if (!dbTools.getToken().isEmpty()) {
            startActivity(new Intent(this, DashboardActivity.class));
        } else {
            Button mStudentLoginButton = (Button) findViewById(R.id.student_login);
            mStudentLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    studentLogin();
                }
            });

            Button mTeacherLoginButton = (Button) findViewById(R.id.teacher_login);
            mTeacherLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    teacherLogin();
                }
            });
        }
    }

    //Redirects to teacher login activity
    private void teacherLogin() {
        Intent intent = new Intent(this, TeacherLoginActivity.class);
        startActivity(intent);
    }

    //Redirects to student login activity
    private void studentLogin(){
        Intent intent = new Intent(this, StudentLoginActivity.class);
        startActivity(intent);
    }
}
