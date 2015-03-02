package in.ac.iiitd.vedantdasswain.movieminer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SignInActivity extends FragmentActivity implements MainFragment.OnFragmentInteractionListener {

    private MainFragment mainFragment;
    private String TAG="SignInActivity";

    private final String APP_ID="1648330488728020";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mainFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (MainFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
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
    protected void onResume() {
        super.onResume();

        Context context=this;

        // Logs 'install' and 'app activate' App Events.
        if(context!=null){
            com.facebook.AppEventsLogger.activateApp(context,APP_ID );
        }

         //AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Context context=this;

        // Logs 'app deactivate' App Event.
        if(context!=null){
            com.facebook.AppEventsLogger.deactivateApp(context, APP_ID);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
