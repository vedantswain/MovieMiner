package in.ac.iiitd.vedantdasswain.movieminer.UIClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.ac.iiitd.vedantdasswain.movieminer.HttpTasks.MovieRelationTask;
import in.ac.iiitd.vedantdasswain.movieminer.ObjectClasses.MovieObject;
import in.ac.iiitd.vedantdasswain.movieminer.OnTaskCompletedListeners.OnMovieRelationTaskCompleted;
import in.ac.iiitd.vedantdasswain.movieminer.R;

/**
 * Created by vedantdasswain on 26/03/15.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> implements OnMovieRelationTaskCompleted{
    ArrayList<MovieObject> movieObjects;
    Context context;
    static final String TAG="MovieAdapter";
    String authToken;

    private void getCredentials() {
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        authToken = appPreferences.getString("token", "");
    }

    @Override
    public void OnTaskCompleted(String msg, MovieObject mo) {
        Log.v(TAG,msg);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        public TextView titleTextView;
        public TextView actorTextView;
        public TextView directorTextView;
        public TextView genreTextView;
        public ImageView posterImageView;
        public ImageView upvoteImageView;
        public ImageView downvoteImageView;
        public viewHolderClicks mListener;
//        String imageUri;

        public ViewHolder(View v,viewHolderClicks mListener) {
            super(v);
            this.mListener=mListener;
            titleTextView=(TextView)v.findViewById(R.id.titleTextView);
            actorTextView=(TextView)v.findViewById(R.id.actorTextView);
            directorTextView=(TextView)v.findViewById(R.id.directorTextView);
            genreTextView=(TextView)v.findViewById(R.id.genreTextView);
            posterImageView=(ImageView)v.findViewById(R.id.posterImageView);
            upvoteImageView=(ImageView)v.findViewById(R.id.upvoteImageView);
            downvoteImageView=(ImageView)v.findViewById(R.id.downvoteImageView);
            upvoteImageView.setOnClickListener(this);
            downvoteImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v instanceof ImageView){
                if(v==upvoteImageView){
                    mListener.onUpvote(v,(MovieObject)v.getTag());
                }
                else if(v==downvoteImageView){
                    mListener.onDownvote(v,(MovieObject)v.getTag());
                }
            }
        }

        public static interface viewHolderClicks {
            public void onUpvote(View icon,MovieObject mo);
            public void onDownvote(View icon,MovieObject mo);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MovieAdapter( Context context,ArrayList<MovieObject> movieObjects ) {
        this.context=context;
        this.movieObjects=movieObjects;
        getCredentials();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_movie, parent, false);

        ViewHolder vh = new ViewHolder(v, new ViewHolder.viewHolderClicks() {
            @Override
            public void onUpvote(View icon,MovieObject mo) {
                int upvote=R.mipmap.ic_upvote;
                int upvoted=R.mipmap.ic_upvoted;
                Log.v(TAG,"clicking upvote "+mo.getRel());
                if(mo.getRel().equals("none")){
                    postRelTask("like",mo);
                }
                else if(mo.getRel().equals("like")){
                    postRelTask("unlike",mo);
                }
            }

            @Override
            public void onDownvote(View icon,MovieObject mo) {
                int downvote=R.mipmap.ic_downvote;
                int downvoted=R.mipmap.ic_downvoted;
                Log.v(TAG,"clicking downvote "+mo.getRel());
                if(mo.getRel().equals("none")){
                    postRelTask("dislike",mo);
                }
                else if(mo.getRel().equals("like")){
                    postRelTask("undislike",mo);
                }
            }
        });
        return vh;
    }

    public void postRelTask(String rel,MovieObject mo){
        (new MovieRelationTask(context,authToken,rel,mo,this)).execute();
    }


    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder viewHolder, int i) {
        viewHolder.titleTextView.setText(movieObjects.get(i).getTitle());
        viewHolder.actorTextView.setText(movieObjects.get(i).getActors());
        viewHolder.directorTextView.setText(movieObjects.get(i).getDirector());
        viewHolder.genreTextView.setText(movieObjects.get(i).getGenre());

        if(movieObjects.get(i).getRel().equals("like")){
            viewHolder.upvoteImageView.setImageResource(R.mipmap.ic_upvoted);
        }
        viewHolder.upvoteImageView.setTag(movieObjects.get(i));

        if(movieObjects.get(i).getRel().equals("dislike")){
            viewHolder.downvoteImageView.setImageResource(R.mipmap.ic_downvoted);
        }
        viewHolder.downvoteImageView.setTag(movieObjects.get(i));

        Glide.with(context)
                .load(movieObjects.get(i).getImageUri())
                .into(viewHolder.posterImageView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return movieObjects.size();
    }
}