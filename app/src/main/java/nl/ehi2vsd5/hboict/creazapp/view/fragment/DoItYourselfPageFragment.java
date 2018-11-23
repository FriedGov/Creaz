package nl.ehi2vsd5.hboict.creazapp.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.helper.ResizeAnimation;

/**
 * @author Youri Tomassen on 27-9-2017.
 */

public class DoItYourselfPageFragment extends Fragment {

    private static final String TAG = DoItYourselfPageFragment.class.getSimpleName();


    private CallbackListener mListener;

    //Request codes
    private static final int REQUEST_IMAGE = 10;
    private static final int REQUEST_IMAGE_CAPTURE = 20;
    private static final int REQUEST_DIALOG = 9999;

    private Context mContext;

    private EditText mEditDescription;
    private TextView mDescription, mLabel;
    private ImageView mImage;
    private FloatingActionButton mFab;
    private View mDescriptionContainer;

    private boolean editorOpen = false;

    private Uri photoUri;
    private String description;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private static final String KEY_TITLE = "title";

    public static DoItYourselfPageFragment newInstance(String title) {

        DoItYourselfPageFragment fragment = new DoItYourselfPageFragment();

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        fragment.setArguments(args);

        return fragment;
    }

    public String getTitle() {
        return getArguments().getString(KEY_TITLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof CallbackListener) {
            mListener = (CallbackListener) context;
            return;
        }
        throw new RuntimeException("Parent Activity should implement CallbackListener interface.");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_create_do_it_yourself, container, false);

        mContext = getContext();

        mDescription = (TextView) rootView.findViewById(R.id.description);
        mEditDescription = (EditText) rootView.findViewById(R.id.edit_description);
        mImage = (ImageView) rootView.findViewById(R.id.image);
        mDescriptionContainer = rootView.findViewById(R.id.description_container);
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mLabel = (TextView) rootView.findViewById(R.id.label_description);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
                // TODO: make camera capture work
                /*
                CharSequence[] dialogItems = {"camera", "gallery"};
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.dialog_choose_image_source))
                        .setItems(dialogItems,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int position) {
                                        if (position == 0) {
                                            openCamera();
                                        } else {
                                            openGallery();
                                        }
                                    }
                                })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int selected) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();
                */
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editorOpen) {
                    mFab.hide();

                    ResizeAnimation animation = new ResizeAnimation(mDescriptionContainer,
                            mDescriptionContainer.getWidth(), mDescriptionContainer.getHeight(),
                            rootView.getWidth(), rootView.getHeight());

                    mDescriptionContainer.startAnimation(animation);

                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    mEditDescription.setText(description != null ? description : "");
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEditDescription, InputMethodManager.SHOW_IMPLICIT);

                    mEditDescription.requestFocus();

                    transitionVisibility(mEditDescription, mDescription);
                    mLabel.setVisibility(View.GONE);

                    editorOpen = true;
                    mDescription.setVisibility(View.GONE);


                    mListener.openTextEditor();
                }
            }
        });

        return rootView;
    }

    /**
     * Start a open image from gallery intent
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    /**
     * start a capture image intent
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e(TAG, "openCamera: ", e);
            }

            if (photoFile != null) {
                Uri photoUri = Uri.fromFile(photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void hideEditor() {
        if (editorOpen) {
            mFab.show();
            description = mEditDescription.getText().toString();
            mDescription.setText(description);

            ResizeAnimation animation = new ResizeAnimation(mDescriptionContainer,
                    mDescriptionContainer.getWidth(),
                    mDescriptionContainer.getHeight(),
                    mDescriptionContainer.getWidth(),
                    0 /* because height is calculated by layout_weight property */);

            mDescriptionContainer.startAnimation(animation);

            mLabel.setVisibility(View.VISIBLE);
            transitionVisibility(mEditDescription, mDescription);

            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditDescription.getWindowToken(), 0);

            editorOpen = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // needed because the fab may start anchoring to random points
        // tells the layout something has changed so it needs to redraw
        // TODO: very hacky fix find better solution
        final View v = getView();
        if (v != null) {
            v.post(new Runnable() {
                @Override
                public void run() {
                    v.requestLayout();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // load photo if is set
        if (photoUri != null) {
            mImage.setMinimumWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mImage.setMinimumHeight(WindowManager.LayoutParams.MATCH_PARENT);

            Glide.with(mContext).load(photoUri)
                    .thumbnail(0.5f)
                    .into(mImage);
        }

        mDescription.setText(description != null
                ? description : getString(R.string.hint_diy_description));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {

            if (resultCode == Activity.RESULT_OK && data != null) {
                Log.i(TAG, "onActivityResult: image selected!");
                photoUri = data.getData();

                //let the image take full width
                mImage.setMinimumWidth(WindowManager.LayoutParams.MATCH_PARENT);
                mImage.setMinimumHeight(WindowManager.LayoutParams.MATCH_PARENT);

                Glide.with(mContext).load(photoUri)
                        .thumbnail(0.5f)

                        .into(mImage);
            } else {
                Toast.makeText(mContext, R.string.error_no_image_selected, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {

            if (resultCode == Activity.RESULT_OK && data != null) {
                Log.i(TAG, "onActivityResult: image captured!");
                photoUri = data.getData();

                mImage.setMinimumWidth(WindowManager.LayoutParams.MATCH_PARENT);
                mImage.setMinimumHeight(WindowManager.LayoutParams.MATCH_PARENT);

                Glide.with(mContext).load(photoUri)
                        .thumbnail(0.5f)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target,
                                                        boolean isFirstResource) {
                                if (e != null) {
                                    Log.e(TAG, "GlideException ", e);
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target,
                                                           DataSource dataSource,
                                                           boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(mImage);
            } else {
                Toast.makeText(mContext, R.string.error_no_image_selected, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "JPG_" + timeStamp + "_";
        File storageDir = mContext.getFilesDir();
        File image = File.createTempFile(
                imageFileName,
                "jpg",
                storageDir);

        return image;
    }

    /**
     * make transition views to visibility that take the same space
     *
     * @param v1 becomes visible or invisible depending on it's current visibility
     * @param v2 becomes visible or invisible depending on it's current visibility
     */
    private void transitionVisibility(View v1, View v2) {
        if (v1.getVisibility() == View.VISIBLE) {
            v1.setVisibility(View.GONE);
            v2.setVisibility(View.VISIBLE);
        } else {
            v1.setVisibility(View.VISIBLE);
            v2.setVisibility(View.GONE);
        }
    }

    public interface CallbackListener {
        void openTextEditor();
    }

    public String getDescription() {
        return description;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }
}
