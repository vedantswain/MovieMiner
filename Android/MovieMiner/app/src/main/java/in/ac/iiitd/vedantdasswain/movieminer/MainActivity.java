package in.ac.iiitd.vedantdasswain.movieminer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.ac.iiitd.vedantdasswain.movieminer.HttpTasks.GetMoviesTask;
import in.ac.iiitd.vedantdasswain.movieminer.ObjectClasses.MovieObject;
import in.ac.iiitd.vedantdasswain.movieminer.OnTaskCompletedListeners.OnGetMoviesTaskCompleted;
import in.ac.iiitd.vedantdasswain.movieminer.UIClasses.MovieAdapter;


public class MainActivity extends ActionBarActivity implements OnGetMoviesTaskCompleted{
    private static String authToken=""; //Django server token
    private static String accessToken=""; //Facebook token
    private static long id;
    private static final String TAG="MainActivity";
    private String TYPE="type";
    RecyclerView movieRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<MovieObject> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupRecyclerView();

        getCredentials();
    }

    private void setupRecyclerView() {
        movieRecyclerView=(RecyclerView)findViewById(R.id.movie_recycler_view);
        movieRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        movieRecyclerView.setLayoutManager(mLayoutManager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCredentials() {
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        authToken = appPreferences.getString("token", "");
        id = appPreferences.getLong("id", 0);
    }

    public void onGet(View view){
        Log.v(TAG,"Sending GET request");
        fetchMovies("me",0);
    }

    private void fetchMovies(String type,int pageNo) {
        (new GetMoviesTask(this,authToken,type,pageNo,this)).execute();
    }


    @Override
    public void OnTaskCompleted(String msg) {
        try {
            JSONObject jsonResponse = new JSONObject(msg);
            Log.v(TAG,jsonResponse.toString());
            JSONArray movieJSONArray = jsonResponse.getJSONArray("movies");
            parseJSONArray(movieJSONArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parseJSONArray(JSONArray movieJSONArray){
        int i=0;
        while(i<movieJSONArray.length()){
            try {
                JSONObject movieJSON=movieJSONArray.getJSONObject(i);
                MovieObject movieObject=new MovieObject(movieJSON.getString("fb_id"),
                        movieJSON.getString("imdb_id"),movieJSON.getString("title"),
                        movieJSON.getString("director"),movieJSON.getString("actors"),
                        movieJSON.getString("genre"),movieJSON.getString("image_uri"));
                movieList.add(movieObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i++;
        }

        mAdapter = new MovieAdapter(this,movieList);
        movieRecyclerView.setAdapter(mAdapter);
    }
}
