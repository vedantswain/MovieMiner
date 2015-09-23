package in.ac.iiitd.vedantdasswain.movieminer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.ac.iiitd.vedantdasswain.movieminer.HttpTasks.SearchMoviesTask;
import in.ac.iiitd.vedantdasswain.movieminer.ObjectClasses.MovieObject;
import in.ac.iiitd.vedantdasswain.movieminer.OnTaskCompletedListeners.OnSearchMoviesTaskCompleted;
import in.ac.iiitd.vedantdasswain.movieminer.UIClasses.MovieAdapter;


public class SearchActivity extends ActionBarActivity implements OnSearchMoviesTaskCompleted{
    static final String TAG="SearchActivity";
    private static String authToken=""; //Django server token
    private static String accessToken=""; //Facebook token
    private static long id;
    private String query;
    RecyclerView movieRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    ArrayList<MovieObject> movieList;
    ProgressDialog pd;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        context=this;
        setupRecyclerView();
        movieList=new ArrayList<MovieObject>();
        getCredentials();
        pd=new ProgressDialog(this);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            Log.v(TAG,"Searching for: "+query);
            this.query=query;
            setTitle("Searching...");
            pd=ProgressDialog.show(context,"Searching","Looking for "+query+"...");
            fetchMovies(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

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
        else if (id==R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        movieRecyclerView=(RecyclerView)findViewById(R.id.search_movie_recycler_view);
//        movieRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        movieRecyclerView.setLayoutManager(mLayoutManager);

    }

    private void getCredentials() {
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        authToken = appPreferences.getString("token", "");
        accessToken = appPreferences.getString("access_token","");
        id = appPreferences.getLong("id", 0);
    }

    private void fetchMovies(String query) {
        (new SearchMoviesTask(this,authToken,accessToken,query,this)).execute();
    }

    @Override
    public void OnTaskCompleted(String msg) {
        try {
            JSONObject jsonResponse = new JSONObject(msg);
//            Log.v(TAG,jsonResponse.toString());
            JSONArray movieJSONArray = jsonResponse.getJSONArray("movies");
            parseJSONArray(movieJSONArray);
            setTitle("Results for: "+query);
            pd.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
            pd.dismiss();
            new AlertDialog.Builder(context)
                    .setTitle("No Results")
                    .setMessage("Please ensure the search query is the correct movie title")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
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

        if(movieList!=null){
            mAdapter = new MovieAdapter(this,movieList);
//            Log.v(TAG,"setting adapter");
            movieRecyclerView.setAdapter(mAdapter);
        }
    }
}
