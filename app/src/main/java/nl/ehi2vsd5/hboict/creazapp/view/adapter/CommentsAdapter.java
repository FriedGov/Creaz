package nl.ehi2vsd5.hboict.creazapp.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.Comment;
import nl.ehi2vsd5.hboict.creazapp.model.DoItYourself;
import nl.ehi2vsd5.hboict.creazapp.model.User;

/**
 * Created by Lexar on 10/17/2017.
 */

public class CommentsAdapter extends FirebaseRecyclerAdapter<Comment,CommentsAdapter.ViewHolder> {

    private FirebaseUser user;
    private CommentsAdapter commentsAdapter;
    private RecyclerView PrecyclerView;
    private Context mContext;

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mUser;

    private String userIdComment = "";

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
    public CommentsAdapter(Class<Comment> modelClass, int modelLayout, Class<CommentsAdapter.ViewHolder> viewHolderClass, Query ref, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.mContext = context;
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
    }


    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_comment_layout,parent,false);
        CommentsAdapter.ViewHolder viewHolder = new CommentsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    protected void populateViewHolder(final CommentsAdapter.ViewHolder viewHolder, Comment comment, int position) {
        viewHolder.description.setText(comment.getComment());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        viewHolder.date.setText(df.format(new Timestamp(-1 * comment.getCreatedAt())));

        Log.d(" jj", "populateViewHolder: " + comment.getUid());
        if (comment.getUid() != null){
            mDatabaseReference.child(User.CHILD).child(comment.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User userComment = dataSnapshot.getValue(User.class);
                    viewHolder.username.setText(userComment.getDisplayName());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            viewHolder.username.setText("Gebruiker");
        }


    }

    @Override
    protected Comment parseSnapshot(DataSnapshot snapshot) {
        Log.d("gg", "parseSnapshot: " + snapshot.toString());
        return super.parseSnapshot(snapshot);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImage;
        TextView username;
        TextView date;
        TextView description;

        public ViewHolder(View v) {
            super(v);
            profileImage = (ImageView) v.findViewById(R.id.comment_profileImage);
            username = (TextView) v.findViewById(R.id.comment_username);
            description = (TextView) v.findViewById(R.id.comment_description);
            date = (TextView) v.findViewById(R.id.comment_date);
        }
    }
}
