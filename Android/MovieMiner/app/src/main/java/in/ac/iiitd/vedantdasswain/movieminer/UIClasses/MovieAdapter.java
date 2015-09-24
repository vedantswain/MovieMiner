package in.ac.iiitd.vedantdasswain.movieminer.UIClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.ac.iiitd.vedantdasswain.movieminer.ObjectClasses.MovieObject;
import in.ac.iiitd.vedantdasswain.movieminer.R;

/**
 * Created by vedantdasswain on 26/03/15.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    ArrayList<MovieObject> movieObjects;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleTextView;
        public TextView actorTextView;
        public TextView directorTextView;
        public TextView genreTextView;
        public ImageView posterImageView;
        public ImageView upvoteImageView;
        public ImageView downvoteImageView;
//        String imageUri;

        public ViewHolder(View v) {
            super(v);
            titleTextView=(TextView)v.findViewById(R.id.titleTextView);
            actorTextView=(TextView)v.findViewById(R.id.actorTextView);
            directorTextView=(TextView)v.findViewById(R.id.directorTextView);
            genreTextView=(TextView)v.findViewById(R.id.genreTextView);
            posterImageView=(ImageView)v.findViewById(R.id.posterImageView);
            upvoteImageView=(ImageView)v.findViewById(R.id.upvoteImageView);
            downvoteImageView=(ImageView)v.findViewById(R.id.downvoteImageView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MovieAdapter( Context context,ArrayList<MovieObject> movieObjects ) {
        this.context=context;
        this.movieObjects=movieObjects;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_movie, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
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

        if(movieObjects.get(i).getRel().equals("dislike")){
            viewHolder.downvoteImageView.setImageResource(R.mipmap.ic_downvoted);
        }

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