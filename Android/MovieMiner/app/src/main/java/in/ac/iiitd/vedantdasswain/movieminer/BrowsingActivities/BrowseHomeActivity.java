package in.ac.iiitd.vedantdasswain.movieminer.BrowsingActivities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.util.ArrayList;

import in.ac.iiitd.vedantdasswain.movieminer.Common;
import in.ac.iiitd.vedantdasswain.movieminer.ObjectClasses.GenreObject;
import in.ac.iiitd.vedantdasswain.movieminer.R;
import in.ac.iiitd.vedantdasswain.movieminer.UIClasses.GenreAdapter;


public class BrowseHomeActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {

    public final static String EXTRA_MESSAGE = "in.ac.iiitd.vedantdasswain.movieminer.MESSAGE";
    private static long id;
    private static final String TAG="BrowseHomeActivity";

    ObservableRecyclerView movieRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    ArrayList<GenreObject> genreList=new ArrayList<GenreObject>();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_home);
        context=this;
        setupGenres();
        setupRecyclerView();
        mAdapter = new GenreAdapter(this,genreList);
//            Log.v(TAG,"setting adapter");
        movieRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browse_home, menu);

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

    private void setupGenres(){
        for (int i=0;i< Common.genres.length;i++){
            GenreObject newGenre=new GenreObject(Common.genres[i],Common.icons[i]);
            genreList.add(newGenre);
        }
    }

    private void setupRecyclerView() {
        movieRecyclerView=(ObservableRecyclerView)findViewById(R.id.browse_genre_recycler_view);
        movieRecyclerView.setScrollViewCallbacks(this);
//        movieRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        movieRecyclerView.setLayoutManager(mLayoutManager);
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
