package nl.ehi2vsd5.hboict.creazapp.view.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

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

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.DoItYourself;
import nl.ehi2vsd5.hboict.creazapp.model.Page;
import nl.ehi2vsd5.hboict.creazapp.view.fragment.DoItYourselfCollectionFragment;

/**
 * @author Youri Tomassen
 */

public class DoItYourselfCollectionAdapter extends FirebaseRecyclerAdapter
        <DoItYourself, DoItYourselfCollectionFragment.ViewHolder> {

    private CallbackListener mListener;
    private DatabaseReference mReference;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;

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
    public DoItYourselfCollectionAdapter(Class<DoItYourself> modelClass,
                                         int modelLayout,
                                         Class<DoItYourselfCollectionFragment.ViewHolder> viewHolderClass,
                                         Query ref,
                                         CallbackListener listener) {

        super(modelClass, modelLayout, viewHolderClass, ref);
        mListener = listener;
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected DoItYourself parseSnapshot(DataSnapshot snapshot) {
        return super.parseSnapshot(snapshot);
    }

    @Override
    protected void populateViewHolder(final DoItYourselfCollectionFragment.ViewHolder holder,
                                      final DoItYourself diy,
                                      int position) {
//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
//        holder.title.setText(df.format(new Timestamp(-1 * diy.getCreatedAt())));

        holder.title.setText(diy.getTitle());
        holder.ratingBar.setRating(diy.averageRating());

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
                        Glide.with(holder.image.getContext()).load(downloadUrl)
                                .thumbnail(0.5f)
                                .into(holder.image);
                    } else {
                        Log.e(TAG, "onComplete: Getting download url was not successfull.", task.getException());
                    }
                }
            });
        }


        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClicked(diy.getId());
            }
        });
    }

    public interface CallbackListener {
        public void onItemClicked(String diyId);
    }
}
