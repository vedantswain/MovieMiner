package in.ac.iiitd.vedantdasswain.movieminer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import in.ac.iiitd.vedantdasswain.movieminer.HttpTasks.MovieRelationTask;
import in.ac.iiitd.vedantdasswain.movieminer.HttpTasks.OmdbTask;
import in.ac.iiitd.vedantdasswain.movieminer.ObjectClasses.MovieObject;
import in.ac.iiitd.vedantdasswain.movieminer.OnTaskCompletedListeners.OnMovieRelationTaskCompleted;
import in.ac.iiitd.vedantdasswain.movieminer.OnTaskCompletedListeners.OnOmdbTaskCompleted;

public class TitleActivity extends ActionBarActivity implements ObservableScrollViewCallbacks, OnOmdbTaskCompleted, View.OnClickListener, OnMovieRelationTaskCompleted {

    private static final String TAG = "TitleActivity";
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    String fb_id;
    String imdb_id;
    String title;
    String director;
    String genre;
    String actors;
    String rel;
    String imageURI;
    String plot;
    String rating;
    String year;

//    ProgressDialog pd;


    private View mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private TextView mPlotView;
    private TextView mYearView;
    private TextView mRatingView;
    private TextView mDirectorView;
    private TextView mActorsView;
    private ImageView genreImageView1;
    private ImageView genreImageView2;
    private ImageView genreImageView3;
    private TextView genreTextView1;
    private TextView genreTextView2;
    private TextView genreTextView3;
    private ImageView upvoteImageView;
    private ImageView downvoteImageView;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;


    HashMap<String,Integer> genreIcons = new HashMap<String,Integer>();
    private String authToken;

    private void getCredentials() {
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        authToken = appPreferences.getString("token", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.hide();

        getCredentials();
        Intent intent = getIntent();
        fb_id=intent.getExtras().getString("fb_id");
        imdb_id=intent.getExtras().getString("imdb_id");
        title=intent.getExtras().getString("title");
        director=intent.getExtras().getString("director");
        actors=intent.getExtras().getString("actors");
        genre=intent.getExtras().getString("genre");
        String[] mgenres = genre.split(",");
        rel=intent.getExtras().getString("rel");
        imageURI=intent.getExtras().getString("imageURI");

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            mActionBarSize = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        mImageView = findViewById(R.id.image);
        Glide.with(this)
                .load(imageURI)
                .into((android.widget.ImageView) mImageView);
        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mPlotView = (TextView) findViewById(R.id.plotView);
        mYearView = (TextView) findViewById(R.id.yearView);
        mRatingView = (TextView) findViewById(R.id.ratingView);
        mActorsView = (TextView) findViewById(R.id.actorsView);
        mActorsView.setText(actors);
        mDirectorView = (TextView) findViewById(R.id.directorView);
        mDirectorView.setText(director);
//        mPlotView.setText("");
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(title);

        genreImageView1=(ImageView) findViewById(R.id.genreImageView1);
        genreImageView2=(ImageView) findViewById(R.id.genreImageView2);
        genreImageView3=(ImageView) findViewById(R.id.genreImageView3);

        genreTextView1=(TextView) findViewById(R.id.genreTextView1);
        genreTextView2=(TextView) findViewById(R.id.genreTextView2);
        genreTextView3=(TextView) findViewById(R.id.genreTextView3);

        upvoteImageView=(ImageView) findViewById(R.id.upvoteImageView);
        downvoteImageView=(ImageView) findViewById(R.id.downvoteImageView);
        upvoteImageView.setOnClickListener(this);
        downvoteImageView.setOnClickListener(this);

        fillHM();
//        Log.v(TAG,""+genreIcons.get(mgenres[0]));
        if(0<mgenres.length) {
            int gi=genreIcons.get(mgenres[0]);
            genreImageView1.setImageResource(gi);
            genreTextView1.setText(mgenres[0]);
        }
        if(1<mgenres.length) {
            int gi=genreIcons.get(mgenres[1].substring(1));
            genreImageView2.setImageResource(gi);
            genreTextView2.setText(mgenres[1]);
        }
        if(2<mgenres.length) {
            int gi=genreIcons.get(mgenres[2].substring(1));
            genreImageView3.setImageResource(gi);
            genreTextView3.setText(mgenres[2]);
        }

        MovieObject mo=new MovieObject(fb_id,imdb_id,title,director,actors,genre,imageURI,rel);

        if(rel.equals("liked")){
            upvoteImageView.setImageResource(R.mipmap.ic_upvoted);
        }
        upvoteImageView.setTag(R.string.movie_object_id,mo);
        upvoteImageView.setTag(R.string.sibling_id,downvoteImageView);

        if(rel.equals("disliked")){
            downvoteImageView.setImageResource(R.mipmap.ic_downvoted);
        }
        downvoteImageView.setTag(R.string.movie_object_id,mo);
        downvoteImageView.setTag(R.string.sibling_id,upvoteImageView);

//        pd=new ProgressDialog(this);

        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, mFlexibleSpaceImageHeight - mActionBarSize);
            }
        });

        setTitle(null);
//        pd.show();
        fetchMovie(imdb_id);
    }

    public void fillHM(){
        for(int i=0;i<Common.genres.length;i++){
            genreIcons.put(Common.genres[i],Common.icons[i]);
//            Log.v(TAG,genres[i]+" "+genreIcons.get(genres[i]));
        }
    }

    private void fetchMovie(String id) {
        (new OmdbTask(this,id,this)).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title, menu);
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

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void OnTaskCompleted(String msg) {
        try {
            JSONObject jsonResponse = new JSONObject(msg);
            Log.v(TAG, jsonResponse.toString());
            parseJSON(jsonResponse);
//            pd.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJSON(JSONObject jsonResponse) {
        try {
            plot=jsonResponse.getString("Plot");
            mPlotView.setText(plot);
            year=jsonResponse.getString("Year");
            mYearView.setText(year);
            rating=jsonResponse.getString("imdbRating");
            mRatingView.setText(rating);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v==upvoteImageView){
            onUpvote(v,(MovieObject)v.getTag(R.string.movie_object_id),(ImageView)v.getTag(R.string.sibling_id));
        }
        else if(v==downvoteImageView){
            onDownvote(v,(MovieObject)v.getTag(R.string.movie_object_id),(ImageView)v.getTag(R.string.sibling_id));
        }
    }

    public void onUpvote(View icon,MovieObject mo,ImageView sibling) {
        ImageView icon_button=(ImageView)icon;
        Log.v(TAG,"clicking upvote "+mo.getRel());
        if(mo.getRel().equals("none") || mo.getRel().equals("disliked")){
            icon_button.setImageResource(Common.upvoted);
            mo.setRel("liked");
            postRelTask("like", mo, icon, sibling);
        }
        else if(mo.getRel().equals("liked")){
            icon_button.setImageResource(Common.upvote);
            mo.setRel("none");
            postRelTask("unlike", mo, icon, sibling);
        }
    }

    public void onDownvote(View icon,MovieObject mo,ImageView sibling) {
        ImageView icon_button=(ImageView)icon;
        Log.v(TAG,"clicking downvote "+mo.getRel());
        if(mo.getRel().equals("none") || mo.getRel().equals("liked")){
            icon_button.setImageResource(Common.downvoted);
            mo.setRel("disliked");
            postRelTask("dislike",mo,icon, sibling);
        }
        else if(mo.getRel().equals("disliked")){
            icon_button.setImageResource(Common.downvote);
            mo.setRel("none");
            postRelTask("undislike",mo,icon, sibling);
        }
    }

    public void postRelTask(String rel,MovieObject mo,View icon,ImageView sibling){
        (new MovieRelationTask(this,authToken,rel,mo,icon,sibling,this)).execute();
    }

    @Override
    public void OnTaskCompleted(String msg, MovieObject mo, View icon, ImageView sibling, String rel) {
        Log.v(TAG,msg);
        if(!msg.contains("200 OK") && !msg.contains("201 CREATED") ){
            if(rel.equals("like")){
                ((ImageView) icon).setImageResource(Common.upvote);
            }
            else if(rel.equals("dislike")){
                ((ImageView) icon).setImageResource(Common.downvote);
            }
            else if(rel.equals("unlike")){
                ((ImageView) icon).setImageResource(Common.upvoted);
            }
            else if(rel.equals("undislike")){
                ((ImageView) icon).setImageResource(Common.downvoted);
            }
        }
        else{
            if(rel.equals("like")){
                sibling.setImageResource(Common.downvote);
            }
            else if(rel.equals("dislike")){
                sibling.setImageResource(Common.upvote);
            }
        }
    }
}
