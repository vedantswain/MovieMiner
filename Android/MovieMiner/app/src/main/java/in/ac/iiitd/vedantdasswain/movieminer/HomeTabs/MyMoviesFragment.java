package in.ac.iiitd.vedantdasswain.movieminer.HomeTabs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.ac.iiitd.vedantdasswain.movieminer.HttpTasks.GetMoviesTask;
import in.ac.iiitd.vedantdasswain.movieminer.ObjectClasses.MovieObject;
import in.ac.iiitd.vedantdasswain.movieminer.OnTaskCompletedListeners.OnGetMoviesTaskCompleted;
import in.ac.iiitd.vedantdasswain.movieminer.R;
import in.ac.iiitd.vedantdasswain.movieminer.UIClasses.MovieAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyMoviesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyMoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyMoviesFragment extends Fragment implements OnGetMoviesTaskCompleted {
    private static String authToken=""; //Django server token
    private static String accessToken=""; //Facebook token
    private static long id;
    private static final String TAG="MyMoviesFragment";
    private String TYPE="type";
    RecyclerView movieRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    ArrayList<MovieObject> movieList;
    private int currPage=0;
    private int nextPage=1;
    ProgressDialog pd;
    View rootView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyMoviesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyMoviesFragment newInstance(String param1, String param2) {
        MyMoviesFragment fragment = new MyMoviesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MyMoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_my_movies, container, false);
        setupRecyclerView();
        movieList=new ArrayList<MovieObject>();
        getCredentials();
        pd=new ProgressDialog(getActivity());
//        pd=ProgressDialog.show(this,"Loading","Fetching your movies...");
        fetchMovies("me",nextPage);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void setupRecyclerView() {
        movieRecyclerView=(RecyclerView)rootView.findViewById(R.id.my_movie_recycler_view);
//        movieRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
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
                        Toast.makeText(getActivity(), "That\'s all we got for you", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        pd.cancel();
                    }
                    if(currPage!=nextPage) {
                        currPage=nextPage;
                        pd=ProgressDialog.show(getActivity(),"Loading","You\'ve seen some more movies...");
                        fetchMovies("me", nextPage);
                    }
                }

            }
        });
    }

    private void getCredentials() {
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        authToken = appPreferences.getString("token", "");
        id = appPreferences.getLong("id", 0);
    }

    public void onGet(View view){
        Log.v(TAG, "Sending GET request");
//        fetchMovies("me",0);
    }

    private void fetchMovies(String type,int pageNo) {
        (new GetMoviesTask(getActivity(),authToken,type,pageNo,this)).execute();
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
            mAdapter = new MovieAdapter(getActivity(),movieList);
//            Log.v(TAG,"setting adapter");
            movieRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.notifyDataSetChanged();
        }
    }

}
