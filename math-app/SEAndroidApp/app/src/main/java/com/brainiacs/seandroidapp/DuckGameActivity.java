package com.brainiacs.seandroidapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.Iterator;
import java.util.Random;


import utils.Equation;
import utils.Point;
import utils.handlers.HttpHandler;


public class DuckGameActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout relativeLayout;
    private Button evenButton;
    private Button oddButton;
    private Equation currentQuestion;
    private ArrayList<Equation> equations;
    private ArrayList<Point> points;
    private int correctAnswers;
    private int incorrectAnswers;
    private ArrayList<Integer> ducks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duck_game);
        initializeClassVariables();
        initializeWidgets();
        initializeListeners();
        setupNextRound();
    }


    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        if (button.getId() == R.id.even) {
            // check if the expression is even, yes then correct, no then not correct;
            String answer = currentQuestion.getAnswer();
            if (answer.equals("even")) {
                correctAnswers++;
            } else {
                incorrectAnswers++;
            }
            // IF click the odd butten
        } else if (button.getId() == R.id.odd) {
            String Answer = currentQuestion.getAnswer();
            if (Answer.equals("odd")) {
                correctAnswers++;
            } else {
                incorrectAnswers++;
            }
        }
        relativeLayout.removeAllViewsInLayout();
        setupNextRound();
    }

    private void setupNextRound() {
        if (!equations.isEmpty()) {
            Equation equation = getRandomEquation();
            currentQuestion = equation;
            int numberOfDucks = Integer.parseInt(equation.getEquation());
            points = new ArrayList<>();
            int duckColor = getRandomDuckColor();
            for (int i = 0; i < numberOfDucks; i++) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(350, 350);
                setRandomPlacement(layoutParams);
                Button duckButton = new Button(this);
                duckButton.setVisibility(View.VISIBLE);
                duckButton.setLayoutParams(layoutParams);
                duckButton.setBackgroundResource(duckColor);
                relativeLayout.addView(duckButton);
            }
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

    private void initializeWidgets() {
        relativeLayout = (RelativeLayout) findViewById(R.id.duckgame);
        evenButton = (Button) findViewById(R.id.even);
        oddButton = (Button) findViewById(R.id.odd);
    }

    private void initializeListeners() {
        evenButton.setOnClickListener(this);
        oddButton.setOnClickListener(this);
    }

    private Equation getRandomEquation() {
        int position = new Random().nextInt(equations.size());
        return equations.remove(position);
    }

    private Integer getRandomDuckColor() {
        int position = new Random().nextInt(ducks.size());
        return ducks.get(position);
    }

    private void initializeClassVariables() {
        // Initialize points to an empty array list
        ducks = new ArrayList<>();
        ducks.add(R.drawable.blueduck);
        ducks.add(R.drawable.greenduck);
        ducks.add(R.drawable.purpleduck);
        ducks.add(R.drawable.redduck);
        ducks.add(R.drawable.duck_icon);

        correctAnswers = 0;
        incorrectAnswers = 0;


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
        } catch (JSONException e) {
        }

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

