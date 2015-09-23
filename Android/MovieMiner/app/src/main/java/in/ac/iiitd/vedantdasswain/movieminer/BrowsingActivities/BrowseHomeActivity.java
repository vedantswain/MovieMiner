package in.ac.iiitd.vedantdasswain.movieminer.BrowsingActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.ac.iiitd.vedantdasswain.movieminer.ObjectClasses.GenreObject;
import in.ac.iiitd.vedantdasswain.movieminer.R;
import in.ac.iiitd.vedantdasswain.movieminer.UIClasses.GenreAdapter;


public class BrowseHomeActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "in.ac.iiitd.vedantdasswain.movieminer.MESSAGE";
    private static long id;
    private static final String TAG="BrowseHomeActivity";
    String[] genres = new String[] {"Action","Adventure","Animation","Biography","Comedy","Crime","Documentary","Drama",
    "Family","Fantasy","Film-Noir","History","Horror","Music","Musical","Mystery","Romance","Sci-Fi","Sport","Thriller",
    "War","Western"};
    RecyclerView movieRecyclerView;
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
        getMenuInflater().inflate(R.menu.menu_browse, menu);
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
        List<String> genreNames = Arrays.asList(genres);
        for (String gn:genreNames){
            GenreObject newGenre=new GenreObject(gn,R.mipmap.ic_launcher);
            genreList.add(newGenre);
        }
    }

    private void setupRecyclerView() {
        movieRecyclerView=(RecyclerView)findViewById(R.id.browse_genre_recycler_view);
//        movieRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        movieRecyclerView.setLayoutManager(mLayoutManager);
    }

}
