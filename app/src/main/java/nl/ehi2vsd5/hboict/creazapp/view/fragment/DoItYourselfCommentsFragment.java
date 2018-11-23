package nl.ehi2vsd5.hboict.creazapp.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.Comment;
import nl.ehi2vsd5.hboict.creazapp.model.DoItYourself;
import nl.ehi2vsd5.hboict.creazapp.view.activity.AccountActivity;
import nl.ehi2vsd5.hboict.creazapp.view.activity.DoItYourselfDetailActivity;
import nl.ehi2vsd5.hboict.creazapp.view.adapter.CommentsAdapter;
import nl.ehi2vsd5.hboict.creazapp.view.adapter.ProfileAdapter;

/**
 * Created by Lexar on 10/23/2017.
 */

public class DoItYourselfCommentsFragment extends Fragment {

    public static final String DIY_ID = "diy_id";

    private CommentsAdapter commentsAdapter;
    private RecyclerView PrecyclerView;
    private Context mContext;
    private commentsCallbacks commentsCallbacks;

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mUser;

    private String diyID;

    public DoItYourselfCommentsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diy_comments,container,false);


        Bundle bundle = getArguments();
        diyID = bundle.getString(DIY_ID);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        mUser = firebaseAuth.getCurrentUser();
        mContext = getContext();

        //Views, lists and buttons
        PrecyclerView = (RecyclerView) view.findViewById(R.id.comments_list);
        final EditText description = (EditText) view.findViewById(R.id.edit_comment);

        Button postComment = (Button) view.findViewById(R.id.btn_comment);

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(description.getText().toString().isEmpty()){
                    Toast.makeText( getContext(), "U mag geen lege comments posten", Toast.LENGTH_SHORT).show();
                }else {
                    commentsCallbacks.postComment(diyID,description.getText().toString());
                    description.setText("");
                }

            }
        });

        //Comments adapter
        commentsAdapter = new CommentsAdapter(
                Comment.class,
                R.layout.single_comment_layout,
                CommentsAdapter.ViewHolder.class,
                mDatabaseReference.child(DoItYourself.CHILD).child(diyID).child(DoItYourself.COMMENTS).orderByChild("createdAt"),
                mContext);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(mContext);
        PrecyclerView.setLayoutManager(manager);
        PrecyclerView.setAdapter(commentsAdapter);



        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof commentsCallbacks) {
            commentsCallbacks = (commentsCallbacks) context;
        }
    }

    public interface commentsCallbacks{
        void postComment(String diyId, String comment);
    }
}
