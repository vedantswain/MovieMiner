package in.ac.iiitd.vedantdasswain.movieminer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

import in.ac.iiitd.vedantdasswain.movieminer.HttpTasks.OmdbTask;
import in.ac.iiitd.vedantdasswain.movieminer.OnTaskCompletedListeners.OnOmdbTaskCompleted;

public class TitleActivity extends ActionBarActivity implements ObservableScrollViewCallbacks, OnOmdbTaskCompleted {

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

    ProgressDialog pd;


    private View mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private TextView mPlotView;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.hide();

        Intent intent = getIntent();
        fb_id=intent.getExtras().getString("fb_id");
        imdb_id=intent.getExtras().getString("imdb_id");
        title=intent.getExtras().getString("title");
        director=intent.getExtras().getString("director");
        actors=intent.getExtras().getString("actors");
        genre=intent.getExtras().getString("genre");
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
        mPlotView = (TextView) findViewById(R.id.plotTextView);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(title);

        pd=new ProgressDialog(this);

        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, mFlexibleSpaceImageHeight - mActionBarSize);
            }
        });

        setTitle(null);
        pd.show();
        fetchMovie(imdb_id);
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
            pd.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJSON(JSONObject jsonResponse) {
        try {
            plot=jsonResponse.getString("Plot");
            mPlotView.setText(plot+plot+plot);
            year=jsonResponse.getString("Year");
            rating=jsonResponse.getString("imdbRating");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
