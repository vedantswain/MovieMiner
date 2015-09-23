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

import java.util.ArrayList;

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
    int[] icons = new int[]{R.mipmap.ic_genre_action,R.mipmap.ic_genre_adventure,R.mipmap.ic_genre_animated,
    R.mipmap.ic_genre_bipoic,R.mipmap.ic_genre_comedy,R.mipmap.ic_genre_crime,R.mipmap.ic_genre_documentary,
    R.mipmap.ic_genre_drama,R.mipmap.ic_genre_family,R.mipmap.ic_genre_fantasy,R.mipmap.ic_genre_noir,
    R.mipmap.ic_genre_history,R.mipmap.ic_genre_horror,R.mipmap.ic_genre_music,R.mipmap.ic_genre_musical,
    R.mipmap.ic_genre_mystery,R.mipmap.ic_genre_romance,R.mipmap.ic_genre_scifi,R.mipmap.ic_genre_sports,
    R.mipmap.ic_genre_thriller,R.mipmap.ic_genre_war,R.mipmap.ic_genre_western};
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
        for (int i=0;i<genres.length;i++){
            GenreObject newGenre=new GenreObject(genres[i],icons[i]);
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
