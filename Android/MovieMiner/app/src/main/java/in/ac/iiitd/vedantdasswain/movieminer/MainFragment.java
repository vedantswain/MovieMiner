package in.ac.iiitd.vedantdasswain.movieminer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";


    private static String authToken=""; //Django server token
    private static String accessToken=""; //Facebook token
    private static long id;
    private String username;
    private GraphUser facebookUser;
    private int login_counter=0;
    ProgressDialog pd;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private UiLifecycleHelper uiHelper;

    public JSONObject userObject=new JSONObject();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        pd=new ProgressDialog(getActivity());
        getCredentials();

        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sign_in, container, false);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("public_profile","user_birthday","user_friends",
                "user_likes", "user_status","user_location","user_actions.video","user_actions.news"));

        return view;
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            // Request user data and show the results
            Request.newMeRequest(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        accessToken=session.getAccessToken();
                        Log.v(TAG,"Token: "+accessToken);
                        login_counter++;
                        if(login_counter==1) {
//                            pd=ProgressDialog.show(getActivity(),"Wait","Logging in...");
                            sendTokenToBackend(accessToken);
                        }
                        facebookUser=user;
                        // Display the parsed user info
                        //Log.i(TAG,buildUserInfoDisplay(user));
//                        goToMainActivity();
                    }
                }
            }).executeAsync();

            //facebookRequest(session,"me/movies?fields=name,id");

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    private void facebookRequest(Session session,String request){
        new Request(
                session,
                request,
                null,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        /* handle the result */
                        if(response!=null)
                            Log.i(TAG, fetchUserData(response));
                    }
                }
        ).executeAsync();
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

    @Override
    public void onResume() {
        super.onResume();
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private String fetchUserData(Response response){
        StringBuilder userInfo = new StringBuilder("");
        // Process the returned response
        GraphObject graphObject = response.getGraphObject();
        FacebookRequestError error = response.getError();
        // Default message
        String message = "Incoming request";

        if (graphObject != null) {
            // Check if there is extra data
            if (graphObject.getProperty("data") != null) {
                try {
                    // Get the data, parse info to get the key/value info
                    JSONArray dataArray =graphObject.getInnerJSONObject().getJSONArray("data");

                    Log.i(TAG,"response: "+graphObject.getInnerJSONObject().toString());

                    for(int i=0;i<dataArray.length();i++){
                        JSONObject unit=dataArray.getJSONObject(i);
                        userInfo.append(String.format("Name: %s\n",
                                unit.getString("name")));
                    }
                } catch (JSONException e) {
                    message = "Error getting request info";
                }
            } else if (error != null) {
                message = "Error getting request info";
            }
        }

        Log.i(TAG, message+" "+Boolean.toString(graphObject!=null));

        Request nextRequest=response.getRequestForPagedResults(Response.PagingDirection.NEXT);

        if(nextRequest!=null){
            Log.i(TAG,"Loading...");

            nextRequest.setCallback(new Request.Callback(){

                @Override
                public void onCompleted(Response response) {
                    if (response != null) {
                        // Display the parsed user info
                        Log.i(TAG,fetchUserData(response));
                    }
                }
            });

            nextRequest.executeAsync();
        }
        else
            Log.i(TAG,"No more results");

        return userInfo.toString();
    }

    private String buildUserInfoDisplay(GraphUser user) {
        Log.i(TAG,"Getting info...");

        String userInfo="";

        try {
            userObject.put("user_id",id);
            userObject.put("fb_id",user.getId());

            userObject.put("username",user.getName());

            userObject.put("location",user.getLocation().getProperty("name"));

            String[] dob=user.getBirthday().split("/");
            userObject.put("birthday",dob[2]+"-"+dob[0]+"-"+dob[1]);

            userInfo=userObject.toString();

            sendUserInfo(userObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return userInfo.toString();
    }

    /*
    Communication with backend server
     */
    private void sendUserInfo(final JSONObject userObject){
        new AsyncTask<Void,String,String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                msg = postUserInfo(userObject);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
                pd.dismiss();
//                pd=ProgressDialog.show(getActivity(),"Wait","Syncing movie likes...");
                storeMoviesAtBackend();
            }
        }.execute(null, null, null);
    }

    private String postUserInfo(JSONObject userObject){
        String msg="";
        HttpClient httpClient = new DefaultHttpClient();
        // replace with your url
        HttpPost httpPost = new HttpPost(Common.USER_API);
        httpPost.setHeader("Authorization","Token "+authToken);

        StringEntity se;
        try {
            se = new StringEntity(userObject.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
            httpPost.setEntity(se);

            HttpResponse response = httpClient.execute(httpPost);
            // write response to log
//            Log.d(TAG,"Post user info"+ response.getStatusLine().toString())
                return "Post user info"+ response.getStatusLine().toString();
//            Log.d(TAG, EntityUtils.toString(response.getEntity()));
        } catch (ClientProtocolException | UnsupportedEncodingException e) {
            // Log exception
            e.printStackTrace();
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }
        return msg;
    }

    private void sendTokenToBackend(final String token){
        new AsyncTask<Void,String,String>() {
            @Override
            protected void onPreExecute(){
                pd=ProgressDialog.show(getActivity(),"Wait","Getting access...");
            }

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                msg = postToken(token);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG,"token to backend: "+ msg);
                pd.dismiss();
            }
        }.execute(null, null, null);
    }

    private String postToken(String token){
        String msg="";
        HttpClient httpClient = new DefaultHttpClient();
        // replace with your url
        HttpPost httpPost = new HttpPost(Common.AUTH_API);
        httpPost.setHeader("Authorization","Token "+token);

        try {
            HttpResponse response = httpClient.execute(httpPost);
            // write response to log
            Log.d(TAG,"Post token response: "+ response.getStatusLine().toString());
            String responseBody=EntityUtils.toString(response.getEntity());
            Log.d(TAG,responseBody );
            if(response.getStatusLine().getStatusCode()==200){
                JSONObject authResponse=new JSONObject(responseBody);
                storeCredentials(authResponse);
            }
        } catch (ClientProtocolException | UnsupportedEncodingException e) {
            // Log exception
            e.printStackTrace();
        } catch (IOException | JSONException e) {
            // Log exception
            e.printStackTrace();
        }
        return msg;
    }

    private void storeMoviesAtBackend(){
        new AsyncTask<Void,String,String>() {
            public static final String TYPE = "me/";

            @Override
            protected void onPreExecute(){
                pd=ProgressDialog.show(getActivity(),"Wait","Syncing movie likes...");
            }

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                msg = postAccessToken(Common.MOVIES_API+TYPE);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
                pd.dismiss();
                pd.cancel();
                goToMainActivity();
            }
        }.execute(null, null, null);
    }

    private String postAccessToken(String API){
        String msg="";
        HttpClient httpClient = new DefaultHttpClient();
        // replace with your url
        HttpPost httpPost = new HttpPost(API);
        httpPost.setHeader("Authorization","Token "+authToken);

        JSONObject jsonObject=new JSONObject();

        StringEntity se;
        try {
            jsonObject.put("access_token",accessToken);
            jsonObject.put("fb_id",facebookUser.getId());

            se = new StringEntity(jsonObject.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
            httpPost.setEntity(se);

            HttpResponse response = httpClient.execute(httpPost);
            // write response to log
            Log.d(TAG,"Post AccessToken response: "+ response.getStatusLine().toString());
            String responseBody=EntityUtils.toString(response.getEntity());
            Log.d(TAG,responseBody );
        } catch (ClientProtocolException | UnsupportedEncodingException e) {
            // Log exception
            e.printStackTrace();
        } catch (IOException | JSONException e) {
            // Log exception
            e.printStackTrace();
        }
        return msg;
    }

    private void storeCredentials(JSONObject authResponse) throws JSONException {
        authToken=authResponse.getString("token");
        id=authResponse.getLong("id");
        username=authResponse.getString("name");
        SharedPreferences appPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor=appPreferences.edit();
        editor.putString("token",authToken);
        editor.putLong("id", id);
        editor.putString("username",username);
        editor.putString("access_token",accessToken);
        editor.putString("name",facebookUser.getName());
        editor.putString("user_id",facebookUser.getId());
        editor.commit();

        Log.d(TAG,"Object sent: "+buildUserInfoDisplay(facebookUser));
    }

    private void getCredentials(){
        SharedPreferences appPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        authToken=appPreferences.getString("token","");
        id=appPreferences.getLong("id",0);

        if(!authToken.isEmpty())
            Log.d(TAG,"Already registered to backend, User id= "+id);
    }

    private void goToMainActivity(){
        Intent intent = new Intent(getActivity(),HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
