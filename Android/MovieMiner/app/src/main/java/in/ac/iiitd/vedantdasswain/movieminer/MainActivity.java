package in.ac.iiitd.vedantdasswain.movieminer;

import android.app.ProgressDialog;
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
import android.widget.Toast;

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
    private LinearLayoutManager mLayoutManager;
    ArrayList<MovieObject> movieList;
    private int currPage=0;
    private int nextPage=1;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupRecyclerView();
        movieList=new ArrayList<MovieObject>();
        getCredentials();
        pd=new ProgressDialog(this);
//        pd=ProgressDialog.show(this,"Loading","Fetching your movies...");
        fetchMovies("me",nextPage);

    }

    private void setupRecyclerView() {
        movieRecyclerView=(RecyclerView)findViewById(R.id.movie_recycler_view);
        movieRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        movieRecyclerView.setLayoutManager(mLayoutManager);

        movieRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int visibleItemCount,totalItemCount,pastVisiblesItems;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

//                Log.v(TAG,"They see me scrollin...");

                if ( (visibleItemCount+pastVisiblesItems) >= totalItemCount) {
//                    Log.v(TAG, "They hatin...");
                    //To check redundant calls at end of list
                    if(nextPage==-1) {
                        Toast.makeText(MainActivity.this, "That\'s all we got for you", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        pd.cancel();
                    }
                    if(currPage!=nextPage) {
                        currPage=nextPage;
                        pd=ProgressDialog.show(MainActivity.this,"Loading","You\'ve seen some more movies...");
                        fetchMovies("me", nextPage);
                    }
                }

            }
        });
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
//        fetchMovies("me",0);
    }

    private void fetchMovies(String type,int pageNo) {
        (new GetMoviesTask(this,authToken,type,pageNo,this)).execute();
    }


    @Override
    public void OnTaskCompleted(String msg) {
        try {
            JSONObject jsonResponse = new JSONObject(msg);
//            Log.v(TAG,jsonResponse.toString());
            JSONArray movieJSONArray = jsonResponse.getJSONArray("movies");
            nextPage=jsonResponse.getInt("next_page_number");
            parseJSONArray(movieJSONArray);
            pd.dismiss();
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
                        movieJSON.getString("genre"),movieJSON.getString("image_uri"),
                        movieJSON.getString("rel"));
                movieList.add(movieObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i++;
        }

        if(movieList!=null && currPage==0){
            mAdapter = new MovieAdapter(this,movieList);
            movieRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.notifyDataSetChanged();
        }
    }
}
