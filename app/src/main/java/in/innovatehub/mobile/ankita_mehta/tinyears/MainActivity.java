package in.innovatehub.mobile.ankita_mehta.tinyears;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import static in.innovatehub.mobile.ankita_mehta.tinyears.TabFragment.viewPager;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "DownloadService";
    public static String flag = "";

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG,"Inside oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);

        mFragmentManager = getSupportFragmentManager();

        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,new TabFragment()).addToBackStack("newfrag").commit();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                int id = menuItem.getItemId();
                if (id == R.id.nav_item_dash) {
                    flag = "Dash";
                    Log.d(LOG_TAG,"Loading navugations ,oncreate");
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new TabFragment()).addToBackStack("newfrag").commit();
                   // viewPager.setCurrentItem(0);
                }
                else if (id == R.id.nav_item_rec) {
                    flag = "Rec";
                    Log.d(LOG_TAG,"Loading navugations ,oncreate");
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new TabFragment()).addToBackStack("newfrag").commit();
                }
                else if (id == R.id.nav_item_last) {
                    flag = "Last";
                    Log.d(LOG_TAG,"Loading navugations ,oncreate");
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new TabFragment()).addToBackStack("newfrag").commit();
                }
                else if (id == R.id.nav_item_overall) {
                    flag = "Overall";
                    Log.d(LOG_TAG,"Loading navugations ,oncreate");
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new TabFragment()).addToBackStack("newfrag").commit();
                }

                return false;
            }
        });

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(LOG_TAG, "Inside on start");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(LOG_TAG, "Inside on Resume");
        if(!checkInternet()){
            Intent newIntent = new Intent(getApplicationContext(),ErrorActivity.class);
            startActivity(newIntent);
        }
    }

    boolean checkInternet(){
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}
