package in.ac.iiitd.vedantdasswain.movieminer.HttpTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import in.ac.iiitd.vedantdasswain.movieminer.Common;
import in.ac.iiitd.vedantdasswain.movieminer.OnTaskCompletedListeners.OnMovieRelationTaskCompleted;

/**
 * Created by vedantdasswain on 24/09/15.
 */
public class MovieRelationTask extends AsyncTask<Void,Void,String> {

    private static final String TAG ="BrowseMoviesTask" ;
    //    private static final String TYPE ="relation" ;
    private static final String PAGE ="page" ;
    Context context;
    String relation;
    String fb_id;
    private String authToken;
    OnMovieRelationTaskCompleted listener;

    public MovieRelationTask(Context context,String authToken,String relation,String fb_id,OnMovieRelationTaskCompleted listener){
        this.context=context;
        this.authToken=authToken;
        this.relation=relation;
        this.fb_id=fb_id;
        this.listener=listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        String msg = "";
//                Log.v(TAG,"Doing in background");
        msg = postMovieRel(relation, fb_id);
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Log.i(TAG, msg);
        listener.OnTaskCompleted(msg);
    }

    private String postMovieRel(String relation,String fb_id){
        String msg="";
        HttpClient httpClient = new DefaultHttpClient();

//        Log.v(TAG,"Get movies");

        String url= Common.MOVIE_REL_API+relation+"/";

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization","Token "+authToken);

        JSONObject jsonObject=new JSONObject();

        StringEntity se;
        try {
            jsonObject.put("movie_id",fb_id);

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
}

