package nl.ehi2vsd5.hboict.creazapp.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.DoItYourself;
import nl.ehi2vsd5.hboict.creazapp.model.Page;
import nl.ehi2vsd5.hboict.creazapp.view.adapter.DoItYourselfDetailAdapter;

/**
 * @author Youri Tomassen 29-10-2017
 */

public class WeeklyChallengeFragment extends Fragment{

    private static final String TAG = WeeklyChallengeFragment.class.getSimpleName();

    /**
     * IMPORTANT: never call this constructor directly always call newInstance constructor
     * required empty constructor must be public so the fragment manager can re-instantiate after
     * screen rotation.
     */
    public WeeklyChallengeFragment() {
    }

    private static final String CHALLENGE_ID = "challenge_id";

    public static WeeklyChallengeFragment newInstance(String challengeId) {
        WeeklyChallengeFragment frag = new WeeklyChallengeFragment();

        Bundle args = new Bundle();
        args.putString(CHALLENGE_ID, challengeId);
        frag.setArguments(args);

        return frag;
    }

    private String diyId;

    private FirebaseDatabase mDatabase;
    private DatabaseReference weeklyChallengeRef;
    private ViewPager mViewPager;
    private DoItYourselfDetailAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_pager, container, false);

        diyId = getArguments().getString(CHALLENGE_ID);

        mDatabase = FirebaseDatabase.getInstance();
        weeklyChallengeRef = mDatabase.getReference().child("weekly_challenge");

        weeklyChallengeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");

                DoItYourself weeklyChallenge = dataSnapshot.getValue(DoItYourself.class);
                mAdapter.setDoItYourself(weeklyChallenge);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ", databaseError.toException());
            }
        });

        mAdapter = new DoItYourselfDetailAdapter(getFragmentManager(), new DoItYourself());
        mViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        mViewPager.setAdapter(mAdapter);

        return rootView;
    }
}
