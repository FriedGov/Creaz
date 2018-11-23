package nl.ehi2vsd5.hboict.creazapp.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import nl.ehi2vsd5.hboict.creazapp.model.DoItYourself;
import nl.ehi2vsd5.hboict.creazapp.model.Page;
import nl.ehi2vsd5.hboict.creazapp.model.Favorite;
import nl.ehi2vsd5.hboict.creazapp.model.User;
import nl.ehi2vsd5.hboict.creazapp.view.activity.DoItYourselfDetailActivity;
import nl.ehi2vsd5.hboict.creazapp.view.fragment.DoItYourselfCollectionFragment;

/**
 * Created by Govert on 24-10-2017.
 */

public class FavoriteDoItYourSelfAdapter extends FirebaseRecyclerAdapter<Favorite, DoItYourselfCollectionFragment.ViewHolder> {

    private FirebaseDatabase mDatabased;
    private DatabaseReference mDatabaseReference;
    private Context mContext;
    private String uid;


    private static final String TAG = DoItYourselfCollectionAdapter.class.getSimpleName();


    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
     *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public FavoriteDoItYourSelfAdapter(Class<Favorite> modelClass, int modelLayout, Class<DoItYourselfCollectionFragment.ViewHolder> viewHolderClass, Query ref, Context context, String uid) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mDatabased = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabased.getReference();
        this.mContext = context;
        this.uid = uid;
    }

    @Override
    protected Favorite parseSnapshot(DataSnapshot snapshot) {
        return super.parseSnapshot(snapshot);
    }


    @Override
    protected void populateViewHolder(final DoItYourselfCollectionFragment.ViewHolder viewHolder, Favorite favorite, int position) {
        final String diyId = favorite.getDiyId();
        Log.d(TAG, "populateViewHolder: favoritedDiy: " + diyId);
        mDatabaseReference.child(DoItYourself.CHILD).child(diyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: diy found");
                final DoItYourself diy = dataSnapshot.getValue(DoItYourself.class);
                try {
                    viewHolder.title.setText(diy.getTitle());
                    viewHolder.ratingBar.setRating(diy.averageRating());
                    //TODO: make this mo purty
                    if (diy.hasPages()) {
                        Page page = diy.getPage(0);
                        final String photoUrl = page.getPhotoUrl();

                        if (photoUrl != null) {
                            StorageReference storageReference = FirebaseStorage.getInstance()
                                    .getReferenceFromUrl(photoUrl);
                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        String downloadUrl = task.getResult().toString();
                                        Glide.with(viewHolder.image.getContext()).load(downloadUrl)
                                                .thumbnail(0.5f)
                                                .into(viewHolder.image);
                                    } else {
                                        Log.e(TAG, "onComplete: Getting download url was not successfull.", task.getException());
                                    }
                                }
                            });
                        }

                    }
                }catch (NullPointerException ex){
                    //remove the favorite if the original diy doesnt exist anymore
                    mDatabaseReference.child(User.CHILD).child(uid).child(User.FAVORITES)
                            .child(diyId).removeValue();
                }
                viewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = viewHolder.image.getContext();
                        Intent intent = new Intent(context, DoItYourselfDetailActivity.class);
                        intent.putExtra(DoItYourselfDetailActivity.DIY_ID, diy.getId());
                        context.startActivity(intent);

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ", databaseError.toException());
            }
        });
    }
}
