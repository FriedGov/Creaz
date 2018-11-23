package nl.ehi2vsd5.hboict.creazapp.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.DoItYourself;
import nl.ehi2vsd5.hboict.creazapp.model.Favorite;
import nl.ehi2vsd5.hboict.creazapp.view.activity.DoItYourselfDetailActivity;
import nl.ehi2vsd5.hboict.creazapp.view.adapter.DoItYourselfCollectionAdapter;
import nl.ehi2vsd5.hboict.creazapp.view.adapter.FavoriteDoItYourSelfAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DoItYourselfCollectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DoItYourselfCollectionFragment extends Fragment implements
        DoItYourselfCollectionAdapter.CallbackListener {

    private static final String TAG = DoItYourselfCollectionFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    public static final String CATEGORY_ID = "CATEGORY_ID";

    private DoItYourselfCollectionAdapter mAdapter;
    private FavoriteDoItYourSelfAdapter mFavoritesAdapter;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public ImageButton favorite;
        public TextView title;
        public RatingBar ratingBar;
        public View container;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar_diy);
        }
    }

    /**
     * required empty constructor
     */
    public DoItYourselfCollectionFragment() {
    }

    public static Fragment newInstance(int categoryId) {
        DoItYourselfCollectionFragment fragment = new DoItYourselfCollectionFragment();

        Bundle args = new Bundle();
        args.putInt(CATEGORY_ID, categoryId);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.grid_recycler_view, container, false);

        Bundle args = getArguments();
        int categoryId = args.getInt(CATEGORY_ID, -1);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();

        mContext = getContext();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager gridManager = new GridLayoutManager
                (mContext, 2, LinearLayoutManager.VERTICAL, false);

        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();

        mRecyclerView.setLayoutManager(gridManager);
        mRecyclerView.setItemAnimator(animator);

        mDatabaseReference.child("user_data").child("62nAgteKrwh4rWB8yM22wyiWUXj1").child("favorites").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: " + dataSnapshot.getKey());
                Favorite favorite = dataSnapshot.getValue(Favorite.class);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Fill the fragment with favorites
        if (categoryId == DoItYourself.CATEGORY_FAVORITES) {
            Log.d(TAG, "onCreateView: favorites");
            mFavoritesAdapter = new FavoriteDoItYourSelfAdapter(
                    Favorite.class,
                    R.layout.grid_do_it_yourself,
                    ViewHolder.class,
                    mDatabaseReference.child("user_data").child(mUser.getUid()).child("favorites"),
                    mContext, mUser.getUid());
            mRecyclerView.setAdapter(mFavoritesAdapter);
        } else if (categoryId != -1) {
            Query queryRef = mDatabaseReference.child(DoItYourself.CHILD)
                    .orderByChild("category").equalTo(categoryId);

            mAdapter = new DoItYourselfCollectionAdapter(
                    DoItYourself.class,
                    R.layout.grid_do_it_yourself,
                    ViewHolder.class,
                    queryRef,
                    this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            Query queryRef = mDatabaseReference.child(DoItYourself.CHILD)
                    .orderByChild("createdAt");
            mAdapter = new DoItYourselfCollectionAdapter(
                    DoItYourself.class,
                    R.layout.grid_do_it_yourself,
                    ViewHolder.class,
                    queryRef,
                    this);
            mRecyclerView.setAdapter(mAdapter);
        }


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onItemClicked(String diyId) {
        mContext.startActivity(new Intent(mContext, DoItYourselfDetailActivity.class)
                .putExtra(DoItYourselfDetailActivity.DIY_ID, diyId));
    }
}
