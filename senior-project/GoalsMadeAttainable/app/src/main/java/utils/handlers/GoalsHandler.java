package utils.handlers;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.goalsmadeattainable.goalsmadeattainable.GoalDetailsActivity;
import com.goalsmadeattainable.goalsmadeattainable.Main.GoalsAdapter;
import com.goalsmadeattainable.goalsmadeattainable.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import utils.DBTools;
import utils.Goal;

public class GoalsHandler extends HttpHandler {
    private RecyclerView goalsRecyclerView;
    private RecyclerView.Adapter goalsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GoalDetailsActivity goalDetailsActivity;
    private TextView emptyView;

    public GoalsHandler(String success, String failure, Intent intent,
                        GMAUrlConnection gmaUrlConnection,
                        RecyclerView goalsRecyclerView, RecyclerView.Adapter goalsAdapter,
                        SwipeRefreshLayout swipeRefreshLayout, GoalDetailsActivity goalDetailsActivity,
                        TextView emptyView) {
        super(success, failure, intent, gmaUrlConnection);
        this.goalsRecyclerView = goalsRecyclerView;
        this.goalsAdapter = goalsAdapter;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.goalDetailsActivity = goalDetailsActivity;
        this.emptyView = emptyView;
    }

    protected void onPreExecute() {}

    /**
     * Default method of handling a response.
     * @param conn is the http connection
     * @return a string that is used in the post execute.
     * @throws IOException
     */
    protected String handleResponse(HttpURLConnection conn) throws IOException {
        if(responseCode == HttpURLConnection.HTTP_OK) {
            // Convert the stream to a string
            String line;
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while((line=br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            // Create a JSONArray to get our data
            try {
                final ArrayList<Goal> goals = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(sb.toString());
                DBTools dbTools = new DBTools(gmaUrlConnection.getContext());
                for(int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    // Create the goal in the database
                    Goal goal = new Goal();
                    // Required fields need no attempts to get data
                    goal.userID = json.getInt(gmaUrlConnection.getContext().getString(R.string.user_user));
                    goal.goalID = json.getInt(gmaUrlConnection.getContext().getString(R.string.goal_id));
                    goal.title = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_title));
                    goal.description = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_description));
                    goal.createdAt = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_created_at));
                    goal.expectedCompletion = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_expected_completion));
                    goal.lastModified = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_last_modified));
                    goal.archived = json.getBoolean(gmaUrlConnection.getContext().getString(R.string.goal_archived));
                    // Attempt to get the future goal id
                    if (!json.isNull(gmaUrlConnection.getContext().getString(R.string.goal_future_goal))) {
                        goal.futureGoalID = json.getInt(gmaUrlConnection.getContext().getString(R.string.goal_future_goal));
                    } else {
                        goal.futureGoalID = null;
                    }
                    // Attempt to get comment
                    if (!json.isNull(gmaUrlConnection.getContext().getString(R.string.goal_comment))) {
                        goal.comment = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_comment));
                    } else {
                        goal.comment = null;
                    }
                    // Attempt to get finished at time
                    if (!json.isNull(gmaUrlConnection.getContext().getString(R.string.goal_finished_at))) {
                        goal.finishedAt = json.getString(gmaUrlConnection.getContext().getString(R.string.goal_finished_at));
                    } else {
                        goal.finishedAt = null;
                    }
                    dbTools.createOrUpdateGoal(goal);
                    goals.add(goal);
                }
                dbTools.close();
                // Update data in recycler view
                final Activity activity = (Activity) gmaUrlConnection.getContext();
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        int fragmentNumber = ((GoalsAdapter) goalsAdapter).getFragmentNumber();
                        goalsAdapter = new GoalsAdapter(goals, fragmentNumber);
                        goalsRecyclerView.removeAllViews();
                        goalsRecyclerView.setAdapter(goalsAdapter);
                        goalsAdapter.notifyDataSetChanged();
                        if (goalDetailsActivity != null) {
                            if (goals.size() > 0) {
                                goalDetailsActivity.viewSubGoals();
                            }
                        } else {
                            if (emptyView != null && goals.isEmpty()) {
                                goalsRecyclerView.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);
                            } else if (emptyView != null) {
                                goalsRecyclerView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.GONE);
                            }
                        }
                    }
                });
                return success;
            } catch (JSONException e) {
                System.err.print(e.getMessage());
                return failure;
            }
        } else if(responseCode >= 200 && responseCode < 300) {
            return success;
        } else {
            return failure;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        swipeRefreshLayout.setRefreshing(false);
        if (!result.isEmpty()) {
            Toast.makeText(gmaUrlConnection.getContext(), result, Toast.LENGTH_LONG).show();
        }
        if (result.equals(success)) {
            if (intent != null) {
                gmaUrlConnection.getContext().startActivity(intent);
            }
        }
    }
}
