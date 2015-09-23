package in.ac.iiitd.vedantdasswain.movieminer.UIClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.ac.iiitd.vedantdasswain.movieminer.BrowsingActivities.BrowseActivity;
import in.ac.iiitd.vedantdasswain.movieminer.ObjectClasses.GenreObject;
import in.ac.iiitd.vedantdasswain.movieminer.R;

/**
 * Created by vedantdasswain on 23/09/15.
 */
public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    ArrayList<GenreObject> genreObjects;
    static Context context;
    static String TAG="GenreAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView titleTextView;
        public ImageView posterImageView;
        public viewHolderClicks mListener;
//        String imageUri;

        public ViewHolder(View v, viewHolderClicks listener) {
            super(v);
            mListener=listener;
            titleTextView=(TextView)v.findViewById(R.id.titleTextView);
            posterImageView=(ImageView)v.findViewById(R.id.posterImageView);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
//            String genreName= (String) ((TextView)v.findViewById(R.id.titleTextView)).getText();
//            Log.v(TAG,"Clicked: "+genreName);
            mListener.onCard(v);
        }

        public static interface viewHolderClicks {
            public void onCard(View card);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GenreAdapter( Context context,ArrayList<GenreObject> genreObjects ) {
        this.context=context;
        this.genreObjects=genreObjects;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GenreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_genre, parent, false);

        ViewHolder vh = new ViewHolder(v,new GenreAdapter.ViewHolder.viewHolderClicks(){
            @Override
            public void onCard(View card) {
                String genreName= (String) ((TextView)card.findViewById(R.id.titleTextView)).getText();
                Log.v(TAG, "Clicked: " + genreName);
                Intent intent=new Intent(context.getApplicationContext(), BrowseActivity.class);
                intent.putExtra("CATEGORY",genreName);
                context.startActivity(intent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {   
        viewHolder.titleTextView.setText(genreObjects.get(i).getName());
        viewHolder.posterImageView.setImageResource(genreObjects.get(i).getIcon());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return genreObjects.size();
    }
}