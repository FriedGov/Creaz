package nl.ehi2vsd5.hboict.creazapp.view.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
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

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.User;


public class DoItYourselfDetailFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String PHOTO = "photoURL";
    public static final String DESCRIPTION = "descprition";
    public static final String TAG = DoItYourselfDetailFragment.class.getSimpleName();
    public static final String DIYID = "DIYID";
    public static final String RATING = "rating";
    public static final String TOTAL_RATING = "tRating";
    public static final String PAGES = "pages";
    public static final String CURRENT_PAGE = "cPage";

    private String photoUrl;
    private String description;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    private Context mContext;
    private ImageView diyImage;
    private TextView tvDescpription;
    private RatingBar mRatingbar;
    private String diyID;
    private Callbacks callbacks;
    private ScrollView mScrollView;
    private View mCommentsContainer;
    private ImageButton favoriteButton;
    private boolean isFavorite;
    private float mCurrentRating;
    private TextView tvTotalRatings;
    private int intTotalRating;
    private TextView tvPaginaCounter;
    private int intPages;
    private int intCurrentPage;

    public DoItYourselfDetailFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_do_it_yourself_detail, container, false);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        checkIfFavorited();
        favoriteButton = (ImageButton) rootView.findViewById(R.id.image_button_favorites);


        diyImage = (ImageView) rootView.findViewById(R.id.diy_image);
        tvDescpription = (TextView) rootView.findViewById(R.id.description);
        tvTotalRatings = (TextView) rootView.findViewById(R.id.total_rating_count);
        tvPaginaCounter = (TextView) rootView.findViewById(R.id.tv_pagina_counter);
        mRatingbar = (RatingBar) rootView.findViewById(R.id.rating_bar);
        mRatingbar.setRating(mCurrentRating);
        mRatingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                callbacks.rate(rating, mUser.getUid(), diyID);
            }
        });

        mScrollView = (ScrollView) rootView.findViewById(R.id.scroll_view);
        tvDescpription.setText(description);


        final String photoURL = photoUrl;

        if (photoURL != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(photoURL);
            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String downloadURL = task.getResult().toString();
                        Glide.with(diyImage.getContext()).load(downloadURL).into(diyImage);
                        diyImage.setMinimumHeight(WindowManager.LayoutParams.MATCH_PARENT);
                        diyImage.setMinimumWidth(WindowManager.LayoutParams.MATCH_PARENT);
                    }
                }
            });
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorite) {
                    isFavorite = true;
                    favoriteButton.setImageResource(R.drawable.ic_favorite_color_accent_30dp);
                } else {
                    isFavorite = false;
                    favoriteButton.setImageResource(R.drawable.ic_favorite_border_color_accent_30dp);
                }
                callbacks.favorite(diyID, mUser.getUid(), isFavorite);
            }
        });

        tvTotalRatings.setText("Totaal Ratings: "+intTotalRating);
        tvPaginaCounter.setText("" + intCurrentPage + "/" +  intPages);


        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        photoUrl = bundle.getString(PHOTO);
        description = bundle.getString(DESCRIPTION);
        diyID = bundle.getString(DIYID);
        mCurrentRating = bundle.getFloat(RATING);
        intTotalRating = bundle.getInt(TOTAL_RATING);
        intPages = bundle.getInt(PAGES);
        intCurrentPage = bundle.getInt(CURRENT_PAGE);
        Log.w(TAG, "onCreate: " + description);

    }

    public interface Callbacks {
        public void rate(float amount, String uid, String diyId);

        public void favorite(String diyId, String uid, Boolean favorited);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callbacks) {
            callbacks = (Callbacks) context;
        }
    }

    /**
     * Check if the current DIY already exists in the user's favorites
     */
    public void checkIfFavorited() {
        FirebaseDatabase mDatabased = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mDatabased.getReference().child(User.CHILD).child(mUser.getUid()).child(User.FAVORITES);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(diyID)) {
                    isFavorite = true;
                    favoriteButton.setImageResource(R.drawable.ic_favorite_color_accent_30dp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
