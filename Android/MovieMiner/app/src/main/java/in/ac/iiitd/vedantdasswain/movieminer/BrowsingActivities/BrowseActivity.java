package in.ac.iiitd.vedantdasswain.movieminer.BrowsingActivities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.ac.iiitd.vedantdasswain.movieminer.HttpTasks.BrowseMoviesTask;
import in.ac.iiitd.vedantdasswain.movieminer.ObjectClasses.MovieObject;
import in.ac.iiitd.vedantdasswain.movieminer.OnTaskCompletedListeners.OnBrowseMoviesTaskCompleted;
import in.ac.iiitd.vedantdasswain.movieminer.R;
import in.ac.iiitd.vedantdasswain.movieminer.UIClasses.MovieAdapter;


public class BrowseActivity extends ActionBarActivity implements OnBrowseMoviesTaskCompleted, ObservableScrollViewCallbacks {
    private static String authToken=""; //Django server token
    private static String accessToken=""; //Facebook token
    private static long id;
    private static final String TAG="BrowseActivity";
    private String CATEGORY="CATEGORY";
    ObservableRecyclerView movieRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    ArrayList<MovieObject> movieList;
    private int currPage=0;
    private int nextPage=1;
    String category;
    ProgressDialog pd;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        Intent intent = getIntent();
        category = intent.getExtras().getString(CATEGORY);
        Log.v(TAG,"Genre: "+category);
        setTitle(category);
        context=this;
        setupRecyclerView();
        movieList=new ArrayList<MovieObject>();
        getCredentials();
        pd=new ProgressDialog(this);
//        pd=ProgressDialog.show(this,"Loading","Fetching your movies...");
        fetchMovies(category,nextPage);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browse, menu);

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

        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        movieRecyclerView=(ObservableRecyclerView)findViewById(R.id.browse_movie_recycler_view);
        movieRecyclerView.setScrollViewCallbacks(this);
//        movieRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

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
                        Toast.makeText(context, "That\'s all we got for you", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        pd.cancel();
                    }
                    if(currPage!=nextPage) {
                        currPage=nextPage;
                        pd=ProgressDialog.show(context,"Loading","We\'ve got some more movies...");
                        fetchMovies(category, nextPage);
                    }
                }

            }
        });
    }

    private void getCredentials() {
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        authToken = appPreferences.getString("token", "");
        id = appPreferences.getLong("id", 0);
    }

    private void fetchMovies(String category,int pageNo) {
        (new BrowseMoviesTask(this,authToken,category,pageNo,this)).execute();
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
//            Log.v(TAG,"setting adapter");
            movieRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b2) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }
    }
}
