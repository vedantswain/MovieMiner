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
import in.ac.iiitd.vedantdasswain.movieminer.OnTaskCompletedListeners.OnGetMoviesTaskCompleted;

/**
 * Created by vedantdasswain on 26/03/15.
 */
public class GetMoviesTask extends AsyncTask<Void,Void,String> {

    private static final String TAG ="GetMoviesTask" ;
//    private static final String TYPE ="type" ;
    private static final String PAGE ="page" ;
    Context context;
    String type;
    int pageNo;
    private String authToken;
    OnGetMoviesTaskCompleted listener;

    public GetMoviesTask(Context context,String authToken,String type,int pageNo,OnGetMoviesTaskCompleted listener){
        this.context=context;
        this.authToken=authToken;
        this.type=type;
        this.pageNo=pageNo;
        this.listener=listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        String msg = "";
//                Log.v(TAG,"Doing in background");
        msg = getMovies(type,pageNo);
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Log.i(TAG, msg);
        listener.OnTaskCompleted(msg);
    }

    private String getMovies(String type,int pageNo){
        String msg="";
        HttpClient httpClient = new DefaultHttpClient();

//        Log.v(TAG,"Get movies");

        String url=Common.MOVIES_API+type+"/";

        if(!url.endsWith("?"))
            url += "?";

        List<NameValuePair> params = new LinkedList<NameValuePair>();

        params.add(new BasicNameValuePair(PAGE, Integer.toString(pageNo)));

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
