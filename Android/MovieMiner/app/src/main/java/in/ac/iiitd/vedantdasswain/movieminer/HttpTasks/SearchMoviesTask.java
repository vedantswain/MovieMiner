package in.ac.iiitd.vedantdasswain.movieminer.HttpTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import in.ac.iiitd.vedantdasswain.movieminer.Common;
import in.ac.iiitd.vedantdasswain.movieminer.OnTaskCompletedListeners.OnSearchMoviesTaskCompleted;

/**
 * Created by vedantdasswain on 23/09/15.
 */
public class SearchMoviesTask extends AsyncTask<Void,Void,String> {

    private static final String TAG ="SearchMoviesTask" ;
    //    private static final String TYPE ="query" ;
    private static final String QUERY ="q" ;
    Context context;
    String query;
    private String authToken;
    private String accessToken;
    OnSearchMoviesTaskCompleted listener;

    public SearchMoviesTask(Context context,String authToken,String accessToken,String query,OnSearchMoviesTaskCompleted listener){
        this.context=context;
        this.authToken=authToken;
        this.accessToken=accessToken;
        this.query=query;
        this.listener=listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        String msg = "";
//                Log.v(TAG,"Doing in background");
        msg = getMovies(query);
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Log.i(TAG, msg);
        listener.OnTaskCompleted(msg);
    }

    private String getMovies(String query){
        String msg="";
        HttpClient httpClient = new DefaultHttpClient();

//        Log.v(TAG,"Get movies");

        String url= Common.SEARCH_API;

        if(!url.endsWith("?"))
            url += "?";

        List<NameValuePair> params = new LinkedList<NameValuePair>();

        params.add(new BasicNameValuePair(QUERY, query));
        params.add(new BasicNameValuePair("access_token",accessToken));

        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Authorization","Token "+authToken);


        try {
            HttpResponse response = httpClient.execute(httpGet);
            // write response to log
            Log.d(TAG,"Get movies: "+ response.getStatusLine().toString());
//            Log.d(TAG, EntityUtils.toString(response.getEntity()));
            return EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException | UnsupportedEncodingException e) {
            // Log exception
            e.printStackTrace();
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }
        return msg;
    }
}

