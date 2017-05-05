package com.brainiacs.seandroidapp;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import utils.Equation;
import utils.Point;
import utils.handlers.HttpHandler;


public class BalloonPoppingActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView equationTextView;
    private RelativeLayout relativeLayout;
    private ArrayList<Equation> equations;
    private ArrayList<Point> points;
    private Equation currentEquation;
    private int correctAnswers;
    private int incorrectAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balloonpopping);
        initializeClassVariables();
        initializeWidgets();
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        // Check our current equation compared to the answer
        if (button.getId() == Integer.parseInt(currentEquation.getAnswer())) {
            // They got the right answer so set the button invisible and increment
            // correct answers
            button.setVisibility(View.INVISIBLE);
            ++correctAnswers;
        } else {
            // They got the wrong answer so turn the balloon that has the right
            // answer red and increment incorrect answers
            button = (Button) relativeLayout.findViewById(Integer.parseInt(currentEquation.getAnswer()));
            button.setBackgroundResource(R.drawable.redballoon);
            button.setClickable(false);
            ++incorrectAnswers;
        }
        // Set up our next equation or go to next activity
        if (!equations.isEmpty()) {
            currentEquation = getRandomEquation();
            equationTextView.setText(currentEquation.getEquation());
        } else {
            postGrade(incorrectAnswers, correctAnswers);
            // Create alert with score
            final Intent intent = new Intent(this, DashboardActivity.class);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Score");
            alertDialogBuilder.setMessage("Answers Correct: " + correctAnswers + "\nAnswers Incorrect: " + incorrectAnswers);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(intent);
                    finish();
                }
            });

            // create the box
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }
    }

    private void initializeClassVariables() {
        // Initialize points to an empty array list
        points = new ArrayList<>();

        // Get equations setup
        equations = new ArrayList<>();
        try {
            JSONArray temp_data = new JSONArray(this.getIntent().getExtras().getString("questions_data"));
            for (int i = 0; i < temp_data.length(); ++i) {
                JSONObject equation_data = temp_data.getJSONObject(i);
                equations.add(new Equation(
                        equation_data.getString("question"), equation_data.getString("answer")));
            }
        } catch(JSONException e) { }

        // Start correct and incorrect answers at zero
        correctAnswers = 0;
        incorrectAnswers = 0;
    }

    private void initializeWidgets() {
        // Get out linear layout and equation text view
        relativeLayout = (RelativeLayout) findViewById(R.id.balloonpopping);
        equationTextView = (TextView) findViewById(R.id.equation);
        equationTextView.setTextSize(getResources().getDimension(R.dimen.textsize));

        // Create buttons for all of the answers
        for (Equation equation : equations) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(350, 350);
            setRandomPlacement(layoutParams);
            Button balloonButton = new Button(this);
            balloonButton.setTextSize(getResources().getDimension(R.dimen.textsize));
            balloonButton.setText(equation.getAnswer());
            balloonButton.setId(Integer.parseInt(equation.getAnswer()));
            balloonButton.setLayoutParams(layoutParams);
            balloonButton.setBackgroundResource(R.drawable.greenballoon);
            balloonButton.setOnClickListener(this);
            relativeLayout.addView(balloonButton);
        }

        // Set the first equation
        currentEquation = getRandomEquation();
        equationTextView.setText(currentEquation.getEquation());
    }

    private Equation getRandomEquation() {
        int position = new Random().nextInt(equations.size());
        return equations.remove(position);
    }

    private void setRandomPlacement(RelativeLayout.LayoutParams layoutParams) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Random r = new Random();
        Point point = new Point();
        do {
            point.setX(r.nextInt(displayMetrics.widthPixels - 350));
            point.setY(r.nextInt(displayMetrics.heightPixels - 500));
        } while (isOverlap(point));
        points.add(point);
        layoutParams.leftMargin = point.getX();
        layoutParams.topMargin = point.getY();
    }

    private boolean isOverlap(Point point) {
        for (Point existingPoint : points) {
            if (((point.getX() >= (existingPoint.getX() - 350)) && (point.getX() <= (existingPoint.getX() + 350))) &&
                    ((point.getY() >= (existingPoint.getY() - 350)) && (point.getY() <= (existingPoint.getY() + 350)))) {
                return true;
            }
        }
        return false;
    }

    private void postGrade(int incorrectAnswers, int correctAnswers) {
        HashMap<String, String> params = new HashMap<>();
        params.put(getString(R.string.total_questions), Integer.toString(incorrectAnswers + correctAnswers));
        params.put(getString(R.string.correct_answers), Integer.toString(correctAnswers));
        params.put(getString(R.string.assignment), Integer.toString(getIntent().getExtras().getInt("id")));
        HttpHandler handler = new HttpHandler(
                getString(R.string.grades_url), "",
                getString(R.string.failed_to_post_grade), HttpHandler.Method.POST,
                params, this, null);
        handler.execute((Void) null);
    }
}