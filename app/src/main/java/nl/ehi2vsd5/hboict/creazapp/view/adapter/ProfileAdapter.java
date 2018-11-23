package nl.ehi2vsd5.hboict.creazapp.view.adapter;

import android.content.Context;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.DoItYourself;
import nl.ehi2vsd5.hboict.creazapp.model.Page;

/**
 * Created by Lexar on 09/26/2017.
 */

public class ProfileAdapter extends FirebaseRecyclerAdapter<DoItYourself,ProfileAdapter.ViewHolder> {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private Context mContext;
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
    public ProfileAdapter(Class<DoItYourself> modelClass, int modelLayout, Class<ViewHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        this.mContext = context;
    }


    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_do_it_yourself,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    protected DoItYourself parseSnapshot(DataSnapshot snapshot) {
        return super.parseSnapshot(snapshot);

    }
    
    @Override
    protected void populateViewHolder(final ViewHolder viewHolder, DoItYourself diy, int position) {
        viewHolder.title.setText(diy.getTitle());
        if (diy.hasPages()) {
            Page page = diy.getPage(0);
            viewHolder.description.setText(page.getDescription());
            if (page != null && page.getPhotoUrl() != null) {
                viewHolder.title.setText(page.getDescription());
                final String photoUrl = page.getPhotoUrl();
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(photoUrl);
                storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String downloadUrl = task.getResult().toString();
                            Glide.with(viewHolder.diyImage.getContext()).load(downloadUrl)
                                    .thumbnail(0.5f)
                                    .into(viewHolder.diyImage);
                        } else {
                            Log.e(TAG, "onComplete: Getting download url was not successfull.", task.getException());
                        }
                    }
                });
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView description;
        TextView favoriteCount;

        ImageView diyImage;
        ImageView favorite;
        RatingBar ratingBar;
        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.single_diy_title);
            description = (TextView) v.findViewById(R.id.single_diy_long_text);
            favoriteCount = (TextView) v.findViewById(R.id.single_diy_favorite_count);
            diyImage = (ImageView) v.findViewById(R.id.single_diy_profile_image);
            favorite = (ImageView) v.findViewById(R.id.single_diy_favorite_heart);
            ratingBar = (RatingBar) v.findViewById(R.id.single_diy_ratingbar);
            ratingBar.setEnabled(false);

        }
    }

}

