package in.innovatehub.mobile.ankita_mehta.tinyears;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.TabLayout;

import static in.innovatehub.mobile.ankita_mehta.tinyears.MainActivity.flag;


public class TabFragment extends Fragment {
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 4 ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View x = inflater.inflate(R.layout.fragment_tab,container,false);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);
        viewPager.setAdapter(new TabFragmentAdapter(getChildFragmentManager()));
        if(flag.equalsIgnoreCase("Dash")){
            viewPager.setCurrentItem(0);
        }
        else if(flag.equalsIgnoreCase("Rec")){
            viewPager.setCurrentItem(1);
        }
        else if(flag.equalsIgnoreCase("Last")){
            viewPager.setCurrentItem(2);
        }
        else if(flag.equalsIgnoreCase("Overall")){
            viewPager.setCurrentItem(3);
        }
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        return x;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(!checkInternet()){
            Intent newIntent = new Intent(getActivity(),ErrorActivity.class);
            startActivity(newIntent);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    boolean checkInternet(){
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


}
