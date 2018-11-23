package nl.ehi2vsd5.hboict.creazapp.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import com.commonsware.cwac.pager.v4.ArrayPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.DoItYourself;
import nl.ehi2vsd5.hboict.creazapp.model.Page;
import nl.ehi2vsd5.hboict.creazapp.view.fragment.DoItYourselfPageFragment;
import nl.ehi2vsd5.hboict.creazapp.view.view.CustomPager;

/**
 * @author Youri Tomassen on 26-9-2017.
 */

public class CreateDoItYourselfActivity extends AppCompatActivity
        implements DoItYourselfPageFragment.CallbackListener {

    private static final String TAG = CreateDoItYourselfActivity.class.getSimpleName();

    private ArrayPagerAdapter<DoItYourselfPageFragment> mPagerAdapter = null;
    private CustomPager mViewPager;
    private TextView mTitle;
    private Menu mMenu;
    private EditText mEditTitle;
    private int pageNumber = 1;

    private FloatingActionButton mPrev, mNext;

    private String title;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private int categoryId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_do_it_yourself);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_close_white_24dp));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        if (mUser == null) {
            //no user signed in.
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            //user signed in.
            Log.d(TAG, "User signed in: " + mUser.getEmail());
            toolbar.setSubtitle(mUser.getDisplayName());
        }

        mPrev = (FloatingActionButton) findViewById(R.id.prev);
        mNext = (FloatingActionButton) findViewById(R.id.next);


        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        });


        mViewPager = (CustomPager) findViewById(R.id.view_pager);
        mTitle = (TextView) findViewById(R.id.title);
        mEditTitle = (EditText) findViewById(R.id.edit_title);
        mMenu = toolbar.getMenu();

        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cycle visibility
                cycleVisibility(mEditTitle, mTitle);

                //change menu items
                Menu menu = toolbar.getMenu();
                menu.removeItem(R.id.action_delete);
                menu.removeItem(R.id.action_add);
                menu.findItem(R.id.action_done).setIcon(getDrawable(R.drawable.ic_close_white_24dp));
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });

        mEditTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                MenuItem item = mMenu.findItem(R.id.action_done);
                if (item != null) {
                    if (editable.length() > 0) {
                        item.setIcon(R.drawable.ic_done_white_24dp);
                    } else {
                        item.setIcon(R.drawable.ic_close_white_24dp);
                    }
                }
            }
        });

        mPagerAdapter = buildAdapter();
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                showOrHideFabs();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        showOrHideFabs();
    }

    private void showOrHideFabs() {
        int itemIndex = mViewPager.getCurrentItem();
        int itemCount = mPagerAdapter.getCount();

        if (itemIndex >= 0 && itemIndex < itemCount - 1) {
            mNext.setVisibility(View.VISIBLE);
        } else {
            mNext.setVisibility(View.GONE);
        }

        if (itemIndex > 0 && itemIndex <= itemCount) {
            mPrev.setVisibility(View.VISIBLE);
        } else {
            mPrev.setVisibility(View.GONE);
        }

    }

    private boolean actionModeEnabled() {
        return mEditTitle.getVisibility() == View.VISIBLE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_do_it_yourself, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {

            if (actionModeEnabled()) {

                if (!mEditTitle.getText().toString().isEmpty()) {
                    title = mEditTitle.getText().toString();
                    mTitle.setText(title);
                    mTitle.setError(null); //remove error
                }

                cycleVisibility(mEditTitle, mTitle);
                restoreMenuTo(R.menu.menu_create_do_it_yourself);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                return true;
            } else {

                if (mTitle.getText().equals(getString(R.string.hint_edit_title))) {
                    mTitle.setError(getString(R.string.error_required_field));
                    return false;
                }

                for (int i = 0; i < mPagerAdapter.getCount(); i++) {
                    if (mPagerAdapter.getCurrentFragment().getDescription() == null) {
                        Toast.makeText(this, "missing description on page #" + (i + 1),
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    if (mPagerAdapter.getCurrentFragment().getPhotoUri() == null) {
                        Toast.makeText(this, "missing photo on page #" + (i + 1),
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                openSelectCategoryDialog();  // select which category the diy gets saved to
                return true;
            }
        } else if (id == R.id.action_add) {
            //add another page to edit
            add(false);
            showOrHideFabs();
            return true;
        } else if (id == R.id.action_delete) {
            remove();
            showOrHideFabs();
            return true;
        } else if (id == R.id.action_done_editing_description) {
            mTitle.setClickable(true);
            showOrHideFabs();
            mViewPager.setPagingEnabled(true);
            DoItYourselfPageFragment pageFragment = mPagerAdapter.getCurrentFragment();
            mTitle.setText(title != null ? title : getString(R.string.hint_edit_title));
            pageFragment.hideEditor();
            mMenu.clear();
            getMenuInflater().inflate(R.menu.menu_create_do_it_yourself, mMenu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * last step in DIY creation path: opens a Dialog which gives 5 options, cancel or one of the
     * categories. Choosing one of the categories will call saveDoItYourself(). Choosing cancel will
     * close the dialog so the user can continue creation
     */
    private void openSelectCategoryDialog() {
        CharSequence[] options = {
                getString(R.string.category_home),
                getString(R.string.category_beauty),
                getString(R.string.category_home_garden_kitchen),
                getString(R.string.category_school)
        };
        String title = getString(R.string.dialog_title_select_category);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        switch (position) {
                            case 0:
                                categoryId = DoItYourself.CATEGORY_HOME;
                                break;
                            case 1:
                                categoryId = DoItYourself.CATEGORY_BEAUTY;
                                break;
                            case 2:
                                categoryId = DoItYourself.CATEGORY_HOME_GARDEN_KITCHEN;
                                break;
                            case 3:
                                categoryId = DoItYourself.CATEGORY_SCHOOL;
                                break;
                        }
                        saveDoItYourself();
                    }
                })
                .create()
                .show();
    }

    private void restoreMenuTo(int resource) {
        mMenu.clear();
        getMenuInflater().inflate(resource, mMenu);
    }

    private void cycleVisibility(View v1, View v2) {
        if (v1.getVisibility() == View.VISIBLE) {
            v1.setClickable(false);
            v1.setVisibility(View.GONE);
            v2.setClickable(true);
            v2.setVisibility(View.VISIBLE);
        } else {
            v2.setClickable(false);
            v2.setVisibility(View.GONE);
            v1.setClickable(true);
            v1.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Creates a new DIY object with all of it's pages and saves them to the FirebaseDatabase
     */
    private void saveDoItYourself() {

        //get a reference so were we can push our diy and it's mPages to
        DatabaseReference diyRef = mDatabaseReference.child(DoItYourself.CHILD).push();

        Log.d(TAG, "saveDoItYourself: key " + diyRef.getKey());

        // create a new DIY object with a title
        DoItYourself diy = new DoItYourself(diyRef.getKey(),
                mTitle.getText().toString(),
                mUser.getUid(),
                categoryId,
                (-System.currentTimeMillis()));
        Log.d(TAG, "saveDoItYourself: key from diy " + diy.getId());
        int pageCount = mPagerAdapter.getCount();
        // loop for adding pages to DIY instance
        for (int i = 0; i < pageCount; i++) {
            DoItYourselfPageFragment pageFragment = mPagerAdapter.getExistingFragment(i);

            //create a new instance of page with photoUrl null, this will be set
            //after we upload the image and got back the photoUrl from the database
            Page page = new Page(pageFragment.getDescription(), null, i);
            diy.addPage(page);

            Uri uri = pageFragment.getPhotoUri();
            Log.d(TAG, "saveDoItYourself: photoUri: " + uri.toString());

            DatabaseReference pagesRef = diyRef.child(Page.DIY_PAGES);

            StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference()
                    .child(DoItYourself.CHILD)
                    .child(diyRef.getKey())
                    .child(uri.getLastPathSegment());
            uploadImage(storageReference, uri, pagesRef, page);

        }

        //once all pages have been set save it to db
        diyRef.setValue(diy);
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * uploads an image to our FirebaseStorage and adds url to that storage location to the page object
     * which is then saved to a DIY object in the FirebaseDatabase
     *
     * @param storageReference to store the image
     * @param uri              local reference of the image
     * @param pagesChild       location of the diy where we need to store our pages
     * @param page             the page that we want to save
     */
    private void uploadImage(StorageReference storageReference, final Uri uri, final DatabaseReference pagesChild, final Page page) {
        storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "uploadImage: success");
                    String url = task.getResult().getMetadata().getDownloadUrl().toString();
                    page.setPhotoUrl(url);
                    pagesChild.child(String.valueOf(page.getSeqNo())).setValue(page);
                } else {
                    Log.d(TAG, "uploadImage: failed");
                }
            }
        });
    }

    private String buildTag(int position) {
        return ("editor" + String.valueOf(pageNumber++));
    }

    private String buildTitle(int position) {
        String title = "hint " + (position + 1);
        return title;
    }

    private ArrayPagerAdapter<DoItYourselfPageFragment> buildAdapter() {
        List<PageDescriptor> pages = new ArrayList<>();

        pages.add(new SimplePageDescriptor(buildTag(0), buildTitle(0)));

        return (new SamplePagerAdapter(getSupportFragmentManager(), (ArrayList<PageDescriptor>) pages));
    }

    private void add(boolean before) {
        int current = mViewPager.getCurrentItem();
        SimplePageDescriptor desc =
                new SimplePageDescriptor(buildTag(mPagerAdapter.getCount()),
                        buildTitle(mPagerAdapter.getCount()));

        if (before) {
            mPagerAdapter.insert(desc, current);
        } else {
            if (current < mPagerAdapter.getCount() - 1) {
                mPagerAdapter.insert(desc, current + 1);
            } else {
                mPagerAdapter.add(desc);
            }
        }
    }

    private void remove() {
        if (mPagerAdapter.getCount() > 1) {
            mPagerAdapter.remove(mViewPager.getCurrentItem());
        }
    }

    private void swap() {
        int current = mViewPager.getCurrentItem();

        if (current < mPagerAdapter.getCount() - 1) {
            mPagerAdapter.move(current, current + 1);
        } else {
            mPagerAdapter.move(current, current - 1);
        }
    }

    @Override
    public void openTextEditor() {
        hideFabs();
        mViewPager.setPagingEnabled(false);
        mTitle.setText(R.string.edit_description);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mTitle.setClickable(false);
        mMenu.clear();
        getMenuInflater().inflate(R.menu.edit_description, mMenu);
    }

    static class SamplePagerAdapter extends
            ArrayPagerAdapter<DoItYourselfPageFragment> {
        public SamplePagerAdapter(FragmentManager fragmentManager,
                                  ArrayList<PageDescriptor> descriptors) {
            super(fragmentManager, descriptors);
        }

        @Override
        protected DoItYourselfPageFragment createFragment(PageDescriptor desc) {
            return (DoItYourselfPageFragment.newInstance(desc.getTitle()));
        }
    }

    public void hideFabs() {
        mNext.setVisibility(View.INVISIBLE);
        mPrev.setVisibility(View.INVISIBLE);
    }
}



