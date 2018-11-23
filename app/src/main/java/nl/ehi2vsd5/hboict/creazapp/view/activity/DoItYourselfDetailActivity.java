package nl.ehi2vsd5.hboict.creazapp.view.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.Comment;
import nl.ehi2vsd5.hboict.creazapp.model.DoItYourself;
import nl.ehi2vsd5.hboict.creazapp.model.Favorite;
import nl.ehi2vsd5.hboict.creazapp.model.User;
import nl.ehi2vsd5.hboict.creazapp.view.adapter.DoItYourselfDetailAdapter;
import nl.ehi2vsd5.hboict.creazapp.view.fragment.DoItYourselfCommentsFragment;
import nl.ehi2vsd5.hboict.creazapp.view.fragment.DoItYourselfDetailFragment;

public class DoItYourselfDetailActivity extends AppCompatActivity
        implements DoItYourselfDetailFragment.Callbacks, DoItYourselfCommentsFragment.commentsCallbacks {

    private static final String TAG = DoItYourselfDetailActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference = mDatabase.getReference();
    private FirebaseUser mUser;
    public static final String DIY_ID = "diy_id";

    private DoItYourself diy;
    private String diyId;
    private Button showComments;
    private Toolbar toolbar;

    private boolean commentsOpen = false;


    private ViewPager mPager;

    private DoItYourselfDetailAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy_detail);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showComments = (Button) findViewById(R.id.comments_button);
        Intent intent = getIntent();
        diyId = intent.getStringExtra(DIY_ID);
        //Comments button
        showComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!commentsOpen){
                    openCommentsFragment();
                    showComments.setText("Reacties verbergen");
                    commentsOpen = true;
                }else {
                    closeCommentFragment();
                    showComments.setText("Reacties weergeven");
                    commentsOpen = false;
                }

            }
        });


        mDatabaseReference.child(MainActivity.DIY_CHILD).child(diyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: changes received");
                diy = dataSnapshot.getValue(DoItYourself.class);
                if (diy != null) {
                    Log.d(TAG, "onDataChange: diyID = " + diy.getId());
                    mPagerAdapter.setDoItYourself(diy);
                    mPagerAdapter.notifyDataSetChanged();
                    toolbar.setTitle(diy.getTitle());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ", databaseError.toException());
            }
        });

        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new DoItYourselfDetailAdapter(getSupportFragmentManager(), diy);
        mPager.setAdapter(mPagerAdapter);
    }

    private void openCommentsFragment(){
        Log.d(TAG, "changeFragment: ");
        //Open commments fragment
        DoItYourselfCommentsFragment doItYourselfCommentsFragment = new DoItYourselfCommentsFragment();
        //Pass the diy id in a bundle to get it in comments

        Bundle args = new Bundle();
        args.putString(DoItYourselfCommentsFragment.DIY_ID, diyId);
        doItYourselfCommentsFragment.setArguments(args);

        //Initiate the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, doItYourselfCommentsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void closeCommentFragment(){
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public void setDiy(DoItYourself diy) {
        this.diy = diy;
    }

    /**
     * Add a rating to the the DIY
     * @param stars
     * @param uid
     * @param diyId
     */
    @Override
    public void rate(float stars, String uid, String diyId) {
        mDatabaseReference.child(DoItYourself.CHILD).child(diyId).child(DoItYourself.RATINGS).child(uid).setValue(stars);
    }

    /**
     * Add a diy to or remove it from favorites
     * @param diyId
     * @param uid
     * @param favorited
     */
    @Override
    public void favorite(String diyId, String uid, Boolean favorited) {
        //Add the diy to favorites if the person added it as favorite for the first time
        if (favorited) {
            mDatabaseReference.child(User.CHILD).child(uid).child(User.FAVORITES)
                    .child(diyId).setValue(new Favorite(diyId));
        }
        //Remove the favorite if the user pressed the heart for a second time
        else{
            mDatabaseReference.child(User.CHILD).child(uid).child(User.FAVORITES)
                    .child(diyId).removeValue();
        }
    }

    /**
     * Show comments fragment to see all of the comments and post one
     * @param diyId
     * @param comment
     */
    @Override
    public void postComment(String diyId,String comment) {
        DatabaseReference commentRef = mDatabaseReference.child(DoItYourself.CHILD).child(diyId).child(DoItYourself.COMMENTS).push();
        Comment diy = new Comment(mUser.getUid(),comment,(-System.currentTimeMillis()));
        commentRef.setValue(diy);
    }
}
