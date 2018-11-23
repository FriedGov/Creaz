package nl.ehi2vsd5.hboict.creazapp.view.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import nl.ehi2vsd5.hboict.creazapp.model.DoItYourself;
import nl.ehi2vsd5.hboict.creazapp.model.Page;
import nl.ehi2vsd5.hboict.creazapp.view.fragment.DoItYourselfDetailFragment;
import nl.ehi2vsd5.hboict.creazapp.view.fragment.DoItYourselfPageFragment;

/**
 * Created by Koen on 10-10-2017.
 */

public class DoItYourselfDetailAdapter extends FragmentStatePagerAdapter{

    private static final String TAG = DoItYourselfDetailAdapter.class.getSimpleName();

    private DoItYourself doItYourself;
    private Page page;


    /**
     * construcor
     * @param fm the fragmentmanager
     * @param diy the Do it yourself
     */
    public DoItYourselfDetailAdapter(FragmentManager fm, DoItYourself diy){
        super(fm);
        this.doItYourself = diy;
    }


    /**
     * Returns the fragment with the right data init
     * @param position The page from the DIY
     * @return The fragment with the right data
     */
    @Override
    public Fragment getItem(int position) {
        page = doItYourself.getPage(position);
        Fragment fragment = new DoItYourselfDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DoItYourselfDetailFragment.DESCRIPTION, page.getDescription());
        bundle.putString(DoItYourselfDetailFragment.PHOTO, page.getPhotoUrl());
        bundle.putString(DoItYourselfDetailFragment.DIYID,doItYourself.getId());
        bundle.putFloat(DoItYourselfDetailFragment.RATING,doItYourself.averageRating());
        bundle.putInt(DoItYourselfDetailFragment.TOTAL_RATING,doItYourself.totalRating());
        bundle.putInt(DoItYourselfDetailFragment.PAGES,doItYourself.getCountPages());
        bundle.putInt(DoItYourselfDetailFragment.CURRENT_PAGE,position+1);
        fragment.setArguments(bundle);

        return fragment;
    }


    /**
     * Method to get the page number
     * @return the number of the page
     */
    @Override
    public int getCount() {

        if(doItYourself ==null){
            return 0;

        }else{
            return doItYourself.getCountPages() ;


        }

    }


    /**
     * sets the given doItYourSelf
     * @param doItYourself
     */

    public void setDoItYourself(DoItYourself doItYourself) {
        this.doItYourself = doItYourself;
    }
}
