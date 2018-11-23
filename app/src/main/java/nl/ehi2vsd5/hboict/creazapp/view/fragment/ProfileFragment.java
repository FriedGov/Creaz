package nl.ehi2vsd5.hboict.creazapp.view.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Locale;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.DoItYourself;
import nl.ehi2vsd5.hboict.creazapp.model.User;
import nl.ehi2vsd5.hboict.creazapp.view.adapter.ProfileAdapter;


/**
 * Created by Lexar on 09/19/2017.
 */

public class ProfileFragment extends android.support.v4.app.Fragment {

    private final static String TAG = ProfileFragment.class.getSimpleName();

    private ProfileAdapter profileAdapter;
    private RecyclerView mRecycler;
    private Context mContext;

    private DatabaseReference mReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mUser;

    private TextView mDisplayName;
    private TextView mUsername;
    private TextView mEmail;
    private TextView mBirthDate;
    private TextView mFavorites;
    private TextView mDiys;
    private ImageView mProfileImg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_fragment_layout, container, false);

        mContext = getContext();

        // define views
        mDisplayName = (TextView) rootView.findViewById(R.id.display_name);
        mBirthDate = (TextView) rootView.findViewById(R.id.birth_date);
        mFavorites = (TextView) rootView.findViewById(R.id.favorites);
        mDiys = (TextView) rootView.findViewById(R.id.diys);
        mProfileImg = (ImageView) rootView.findViewById(R.id.profile_img);

        firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();

        mReference.child(User.CHILD).child(mUser.getUid()).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: " + dataSnapshot.getKey());
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL,
                                    Locale.getDefault());
                            mBirthDate.setText(user.getBirthDate() != 0 ? formatter.format(
                                    new Timestamp(user.getBirthDate())) : "n/a");
                            mDisplayName.setText(user.getDisplayName() != null ? user.getDisplayName() : "n/a");

                            mDiys.setText(String.valueOf(-1 * user.getRank()));

                            final String photoUrl = user.getPhotoUrl();

                            if (photoUrl != null) {
                                StorageReference storageReference = FirebaseStorage.getInstance()
                                        .getReferenceFromUrl(photoUrl);
                                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = task.getResult().toString();
                                            Glide.with(mContext).load(downloadUrl)
                                                    .thumbnail(0.5f)
                                                    .into(mProfileImg);
                                        } else {
                                            Log.e(TAG, "onComplete: Getting download url was not success full.",
                                                    task.getException());
                                        }
                                    }
                                });
                            }
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled: ", databaseError.toException());
                    }
                });

        mReference.child(User.CHILD).child(mUser.getUid()).child("favorites").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long count = dataSnapshot.getChildrenCount();
                        mFavorites.setText(String.valueOf(count));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled: ", databaseError.toException());
                    }
                }
        );

        mRecycler = (RecyclerView) rootView.findViewById(R.id.profile_recycler_view);

        profileAdapter = new ProfileAdapter(
                DoItYourself.class,
                R.layout.row_do_it_yourself,
                ProfileAdapter.ViewHolder.class,
                mReference.child(DoItYourself.CHILD).orderByChild("uid").equalTo(mUser.getUid()),
                mContext);

        mRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecycler.setAdapter(profileAdapter);

        return rootView;
    }
}

