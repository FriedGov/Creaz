package nl.ehi2vsd5.hboict.creazapp.view.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.helper.Util;
import nl.ehi2vsd5.hboict.creazapp.model.User;

/**
 * @author Youri Tomassen
 * @author Creaz
 */

public class RanksFragment extends Fragment {

    private static final String TAG = RanksFragment.class.getSimpleName();

    /**
     * required empty constructor
     */
    public RanksFragment() {
    }

    /**
     *
     * @return RanksFragment with a bundle containing arguments
     */
    public static RanksFragment newInstance() {
        RanksFragment frag = new RanksFragment();

        Bundle args = new Bundle();

        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, rank, position;
        ImageView profilePicture, iconBackground;

        public ViewHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.username);
            rank = (TextView) itemView.findViewById(R.id.rank);
            position = (TextView) itemView.findViewById(R.id.position);
            profilePicture = (ImageView) itemView.findViewById(R.id.profile_pic);
            iconBackground = (ImageView) itemView.findViewById(R.id.bg_profile_pic);
        }
    }
    private Context mContext;

    // Firebase
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;

    // Views
    private RecyclerView mRecycler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        mContext = getContext();

        Bundle args = getArguments();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();

        Query query = mReference.child(User.CHILD).orderByChild("rank");

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User, ViewHolder>(
                User.class, R.layout.row_rank, ViewHolder.class, query) {

            @Override
            protected void populateViewHolder(final ViewHolder holder, User user, int position) {
                final String photoUrl = user.getPhotoUrl();
                if (photoUrl != null) {
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReferenceFromUrl(photoUrl);
                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String downloadUrl = task.getResult().toString();
                                Glide.with(holder.profilePicture.getContext()).load(downloadUrl)
                                        .thumbnail(0.5f)
                                        .into(holder.profilePicture);
                            } else {
                                Log.e(TAG, "onComplete: Getting download url was not successfull.",
                                        task.getException());
                            }
                        }
                    });
                }

                String pos = "#" + (position + 1);
                holder.position.setText(pos);
                holder.username.setText(user.getDisplayName() != null ?
                        user.getDisplayName() : "n/a");
                holder.rank.setText(String.valueOf(user.getRank()));
                int color = Util.getRandomMaterialColor(holder.iconBackground.getContext(),
                        R.array.mdcolor_500,
                        user.getCreatedAt() != 0 ? user.getCreatedAt() : System.currentTimeMillis());
                holder.iconBackground.setColorFilter(color);
                holder.rank.setText(String.valueOf(-1 * user.getRank()));
            }
        };
        mRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecycler.setAdapter(adapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        mRecycler.addItemDecoration(new DividerItemDecoration(mContext,
                DividerItemDecoration.HORIZONTAL));

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
