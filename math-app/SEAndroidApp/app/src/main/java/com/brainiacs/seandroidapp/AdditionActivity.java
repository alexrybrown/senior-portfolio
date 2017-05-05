package com.brainiacs.seandroidapp;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import utils.Point;
import utils.Equation;
import utils.handlers.HttpHandler;


public class AdditionActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView equationTextView;
    private RelativeLayout relativeLayout;
    private Equation currentEquation;
    ArrayList <Equation> equations;
    ArrayList <Point> points;
    ArrayList<Integer> animalHeads = new ArrayList<>();
    private int correctAnswers, incorrectAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addition_layout);
        initializeClassVariables();
        initializeWidgets();
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        // Check our current equation compared to the answer
        if (button.getTag().toString().equals(currentEquation.getAnswer())) {
            ++correctAnswers;
        }
        else {
            ++incorrectAnswers;
        }

        if (!equations.isEmpty()) {
            relativeLayout.removeAllViewsInLayout();
            arrangeAnimalHeads();
        }
        else {
            // Post grade
            postGrade(incorrectAnswers, correctAnswers);
            // Create alert with score
            relativeLayout.removeAllViewsInLayout();
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

            // create and show the box
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void arrangeAnimalHeads() {
        ArrayList<String> used = new ArrayList<>();
        // Makes it so the same animal isn't always the correct answer
        int correctHead = new Random().nextInt(animalHeads.size());
        currentEquation = getRandomEquation();
        used.add(currentEquation.getAnswer());

        for (int i = 0; i < animalHeads.size(); ++i) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(350, 350);
            Button animalButton = new Button(this);
            animalButton.setId(i);

            if (i == correctHead) {
                animalButton.setTag(currentEquation.getAnswer());
            }
            else {
                String k = Integer.toString(randInt(1, 20));
                while (used.contains(k)) {
                    k = Integer.toString(randInt(1, 20));
                }
                animalButton.setTag(k);
            }

            setPlacement(layoutParams, animalButton.getId(), animalButton);
            animalButton.setBackgroundResource(animalHeads.get(i));
            animalButton.setLayoutParams(layoutParams);
            animalButton.setOnClickListener(this);
            animalButton.setTextSize(getResources().getDimension(R.dimen.textsize));

            relativeLayout.addView(animalButton);
        }

        relativeLayout = (RelativeLayout) findViewById(R.id.addition_layout);
        equationTextView = (TextView) findViewById(R.id.equation);
        equationTextView.setTextSize(getResources().getDimension(R.dimen.textsize));
        equationTextView.setText(currentEquation.getEquation());
    }

    private void initializeWidgets() {
        relativeLayout = (RelativeLayout) findViewById(R.id.addition_layout);
        equationTextView = (TextView) findViewById(R.id.equation);
        equationTextView.setTextSize(getResources().getDimension(R.dimen.textsize));


        animalHeads.add(R.drawable.elephant_head);
        animalHeads.add(R.drawable.frog_head);
        animalHeads.add(R.drawable.owl_head);
        animalHeads.add(R.drawable.tiger_head);

        arrangeAnimalHeads();
    }


    private int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    private Equation getRandomEquation() {
        int position = new Random().nextInt(equations.size());
        return equations.remove(position);
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

    public void setPlacement(RelativeLayout.LayoutParams layoutParams, int id, Button animalButton) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        TextView TV = new TextView(this);
        Point point = new Point();
        int widthPixels = (displayMetrics.widthPixels - 350) / 4;
        int heightPixels = (displayMetrics.heightPixels - 500) / 4;
        if (id == 0) {
            point.setX(widthPixels);
            point.setY(heightPixels);

        }
        else if (id == 1) {
            point.setX(widthPixels * 3);
            point.setY(heightPixels);

        }

        else if (id == 2) {
            point.setX(widthPixels);
            point.setY(heightPixels * 3);

        }

        else {
            point.setX(widthPixels * 3);
            point.setY(heightPixels * 3);

        }

        TV.setX(point.getX() + 100);
        TV.setY((point.getY()) - 150);
        TV.setText(animalButton.getTag().toString());
        TV.setTextSize(getResources().getDimension(R.dimen.textsize));
        TV.setGravity(Gravity.CENTER_HORIZONTAL);
        TV.setGravity(Gravity.CENTER_VERTICAL);

        layoutParams.leftMargin = point.getX();
        layoutParams.topMargin = point.getY();
        relativeLayout.addView(TV);
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