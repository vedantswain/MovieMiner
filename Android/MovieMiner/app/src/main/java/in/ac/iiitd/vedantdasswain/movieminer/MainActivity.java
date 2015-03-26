package in.ac.iiitd.vedantdasswain.movieminer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import in.ac.iiitd.vedantdasswain.movieminer.HttpTasks.GetMoviesTask;
import in.ac.iiitd.vedantdasswain.movieminer.OnTaskCompletedListeners.OnGetMoviesTaskCompleted;


public class MainActivity extends ActionBarActivity implements OnGetMoviesTaskCompleted{
    private static String authToken=""; //Django server token
    private static String accessToken=""; //Facebook token
    private static long id;
    private static final String TAG="MainActivity";
    private String TYPE="type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getCredentials();
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
        (new GetMoviesTask(this,authToken,type,pageNo)).execute();
    }


    @Override
    public void OnTaskCompleted(String msg) {
        try {
            JSONObject jsonResponse = new JSONObject(msg);
            Log.v(TAG,jsonResponse.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
