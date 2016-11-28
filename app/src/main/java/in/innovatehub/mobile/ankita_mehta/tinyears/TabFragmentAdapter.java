package in.innovatehub.mobile.ankita_mehta.tinyears;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import static in.innovatehub.mobile.ankita_mehta.tinyears.TabFragment.int_items;

/**
 * Created by ankita_mehta on 11/28/16.
 */

public class TabFragmentAdapter extends FragmentPagerAdapter {
    public TabFragmentAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 : return new DashboardFragment();
            case 1 : return new RecorderFragment();
            case 2 : return new AnalyticFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return int_items;
    }

    /**
     * This method returns the title of the tab according to the position.
     */

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 :
                return "Dashboard";
            case 1 :
                return "Recording";
            case 2 :
                return "Analytics";
        }
        return null;
    }
}