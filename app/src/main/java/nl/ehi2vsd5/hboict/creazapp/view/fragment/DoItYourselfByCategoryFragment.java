package nl.ehi2vsd5.hboict.creazapp.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.dom.DOMLocator;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.DoItYourself;

/**
 * @author Youri Tomassen
 */

public class DoItYourselfByCategoryFragment extends Fragment {

    private static final String TAG = DoItYourselfCollectionFragment.class.getSimpleName();

    /**
     * new instance constructor for creating bundles
     * @return new instance of DoItYourselfByCategoryFragment
     */
    public static DoItYourselfByCategoryFragment newInstance(int categoryId) {

        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryId);

        DoItYourselfByCategoryFragment fragment = new DoItYourselfByCategoryFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private static final String ARG_CATEGORY_ID = "category_id";

    /**
     * required empty constructor
     */
    public DoItYourselfByCategoryFragment() {

    }

    private CallbackListener mListener;

    private ViewPager mPager;
    private DoItYourselfCollectionPagerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diy_by_category, container, false);

        mPager = (ViewPager) rootView.findViewById(R.id.view_pager);

        mAdapter = new DoItYourselfCollectionPagerAdapter(getFragmentManager());
        mPager.setAdapter(mAdapter);

        mListener.setupTabs(mPager);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof CallbackListener)
            mListener = (CallbackListener) context;
        else
            throw new RuntimeException("Parent Activity must implement CallbackListener.");
    }

    private class DoItYourselfCollectionPagerAdapter extends FragmentStatePagerAdapter {

        String[] pageTitles = { getString(R.string.category_all),
                getString(R.string.category_home),
                getString(R.string.category_beauty),
                getString(R.string.category_home_garden_kitchen),
                getString(R.string.category_school)};

        public DoItYourselfCollectionPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1 :
                    return DoItYourselfCollectionFragment.newInstance(
                            DoItYourself.CATEGORY_HOME);
                case 2 :
                    return DoItYourselfCollectionFragment.newInstance
                            (DoItYourself.CATEGORY_BEAUTY);
                case 3 :
                    return DoItYourselfCollectionFragment.newInstance(
                            DoItYourself.CATEGORY_HOME_GARDEN_KITCHEN);
                case 4 :
                    return DoItYourselfCollectionFragment.newInstance(
                            DoItYourself.CATEGORY_SCHOOL);
                default:
                    return DoItYourselfCollectionFragment.newInstance(
                            DoItYourself.CATEGORY_ALL);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
        }

        @Override
        public int getCount() {
            return pageTitles.length;
        }

    }

    public interface CallbackListener {
        void setupTabs(ViewPager viewPager);
    }
}
