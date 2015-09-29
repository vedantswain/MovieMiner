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

    final int upvote=R.mipmap.ic_upvote;
    final int upvoted=R.mipmap.ic_upvoted;
    final int downvote=R.mipmap.ic_downvote;
    final int downvoted=R.mipmap.ic_downvoted;

    private void getCredentials() {
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        authToken = appPreferences.getString("token", "");
    }

    @Override
    public void OnTaskCompleted(String msg, MovieObject mo,View icon,ImageView sibling,String rel) {
        Log.v(TAG,msg);
        if(!msg.contains("200 OK") && !msg.contains("201 CREATED") ){
            if(rel.equals("like")){
                ((ImageView) icon).setImageResource(upvote);
            }
            else if(rel.equals("dislike")){
                ((ImageView) icon).setImageResource(downvote);
            }
            else if(rel.equals("unlike")){
                ((ImageView) icon).setImageResource(upvoted);
            }
            else if(rel.equals("undislike")){
                ((ImageView) icon).setImageResource(downvoted);
            }
        }
        else{
            if(rel.equals("like")){
                sibling.setImageResource(downvote);
            }
            else if(rel.equals("dislike")){
                sibling.setImageResource(upvote);
            }
        }
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
                    mListener.onUpvote(v,(MovieObject)v.getTag(R.string.movie_object_id),(ImageView)v.getTag(R.string.sibling_id));
                }
                else if(v==downvoteImageView){
                    mListener.onDownvote(v,(MovieObject)v.getTag(R.string.movie_object_id),(ImageView)v.getTag(R.string.sibling_id));
                }
            }
        }

        public static interface viewHolderClicks {
            public void onUpvote(View icon,MovieObject mo,ImageView sibling);
            public void onDownvote(View icon,MovieObject mo,ImageView sibling);
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
            public void onUpvote(View icon,MovieObject mo,ImageView sibling) {
                ImageView icon_button=(ImageView)icon;
                Log.v(TAG,"clicking upvote "+mo.getRel());
                if(mo.getRel().equals("none") || mo.getRel().equals("disliked")){
                    icon_button.setImageResource(upvoted);
                    mo.setRel("liked");
                    postRelTask("like", mo, icon, sibling);
                }
                else if(mo.getRel().equals("liked")){
                    icon_button.setImageResource(upvote);
                    mo.setRel("none");
                    postRelTask("unlike", mo, icon, sibling);
                }
            }

            @Override
            public void onDownvote(View icon,MovieObject mo,ImageView sibling) {
                ImageView icon_button=(ImageView)icon;
                Log.v(TAG,"clicking downvote "+mo.getRel());
                if(mo.getRel().equals("none") || mo.getRel().equals("liked")){
                    icon_button.setImageResource(downvoted);
                    mo.setRel("disliked");
                    postRelTask("dislike",mo,icon, sibling);
                }
                else if(mo.getRel().equals("disliked")){
                    icon_button.setImageResource(downvote);
                    mo.setRel("none");
                    postRelTask("undislike",mo,icon, sibling);
                }
            }
        });
        return vh;
    }

    public void postRelTask(String rel,MovieObject mo,View icon,ImageView sibling){
        (new MovieRelationTask(context,authToken,rel,mo,icon,sibling,this)).execute();
    }


    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder viewHolder, int i) {
        viewHolder.titleTextView.setText(movieObjects.get(i).getTitle());
        viewHolder.actorTextView.setText(movieObjects.get(i).getActors());
        viewHolder.directorTextView.setText(movieObjects.get(i).getDirector());
        viewHolder.genreTextView.setText(movieObjects.get(i).getGenre());

        if(movieObjects.get(i).getRel().equals("liked")){
            viewHolder.upvoteImageView.setImageResource(R.mipmap.ic_upvoted);
        }
        viewHolder.upvoteImageView.setTag(R.string.movie_object_id,movieObjects.get(i));
        viewHolder.upvoteImageView.setTag(R.string.sibling_id,viewHolder.downvoteImageView);

        if(movieObjects.get(i).getRel().equals("disliked")){
            viewHolder.downvoteImageView.setImageResource(R.mipmap.ic_downvoted);
        }
        viewHolder.downvoteImageView.setTag(R.string.movie_object_id,movieObjects.get(i));
        viewHolder.downvoteImageView.setTag(R.string.sibling_id,viewHolder.upvoteImageView);

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