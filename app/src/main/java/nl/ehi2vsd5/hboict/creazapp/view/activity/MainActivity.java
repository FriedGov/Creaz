package nl.ehi2vsd5.hboict.creazapp.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import nl.ehi2vsd5.hboict.creazapp.R;
import nl.ehi2vsd5.hboict.creazapp.model.DoItYourself;
import nl.ehi2vsd5.hboict.creazapp.view.fragment.DoItYourselfByCategoryFragment;
import nl.ehi2vsd5.hboict.creazapp.view.fragment.DoItYourselfCollectionFragment;
import nl.ehi2vsd5.hboict.creazapp.view.fragment.DoItYourselfDetailFragment;
import nl.ehi2vsd5.hboict.creazapp.view.fragment.ProfileFragment;
import nl.ehi2vsd5.hboict.creazapp.view.fragment.RanksFragment;
import nl.ehi2vsd5.hboict.creazapp.view.fragment.WeeklyChallengeFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DoItYourselfCollectionFragment.OnFragmentInteractionListener,
        DoItYourselfByCategoryFragment.CallbackListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;

    public static final String DIY_CHILD = "diys";

    private TabLayout mTabs;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        mTabs = (TabLayout) findViewById(R.id.tabs);

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
            Log.i(TAG, "User signed in: " + mUser.getEmail());
            toolbar.setSubtitle(mUser.getDisplayName());

            View header = navigationView.getHeaderView(0);

            ImageView userPhoto =(ImageView) header.findViewById(R.id.user_photo);
            if (mUser.getPhotoUrl() != null) {
                // set user photo
            }

            TextView username = (TextView) header.findViewById(R.id.username);

            if (mUser.getDisplayName() != null)
                username.setText(mUser.getDisplayName());

            TextView userEmail = (TextView) header.findViewById(R.id.user_email);
            userEmail.setText(mUser.getEmail());

        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Intent createDoItYourselfIntent = new Intent(this, CreateDoItYourselfActivity.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(createDoItYourselfIntent);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, DoItYourselfByCategoryFragment.newInstance(
                        DoItYourself.CATEGORY_ALL))
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_register) {

            startActivity(new Intent(this, RegisterActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_contact) {
            Uri mUri = Uri.parse("smsto:+31681285266");
            Intent intent = new Intent(Intent.ACTION_SENDTO, mUri);
            intent.setPackage("com.whatsapp");
            intent.putExtra("chat", true);
            startActivity(intent);

            return true;
        } else if (id == R.id.action_account) {
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // actionbar for changing titles
        ActionBar actionBar = getSupportActionBar();

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_challenge) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, WeeklyChallengeFragment.newInstance("noId"))
                    .commit();
            mTabs.setVisibility(View.GONE);
            setToolbarTitle(getString(R.string.title_weekly_challenge));
        } else  if (id == R.id.nav_ranks) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, RanksFragment.newInstance())
                    .commit();
            mTabs.setVisibility(View.GONE);
            setToolbarTitle(getString(R.string.title_ranks));
        } else if (id == R.id.nav_diy) {
            // Handle diy fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, DoItYourselfByCategoryFragment.newInstance(
                            DoItYourself.CATEGORY_ALL))
                    .commit();
            mTabs.setVisibility(View.VISIBLE);
            setToolbarTitle(getString(R.string.title_diys));
        } else if (id == R.id.nav_favorites) {
            // Handle favorites fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, DoItYourselfCollectionFragment
                            .newInstance(DoItYourself.CATEGORY_FAVORITES))
                    .commit();
            mTabs.setVisibility(View.GONE);
            setToolbarTitle(getString(R.string.title_favorites));
        } else if (id == R.id.nav_subscribers) {
            // Handle subscribers fragment
        } else if (id == R.id.nav_profile) {
            // Handle profile fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
            mTabs.setVisibility(View.GONE);
            setToolbarTitle(getString(R.string.title_profile));
        } else if (id == R.id.nav_dev) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setToolbarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(title);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void setupTabs(ViewPager viewPager) {
        mTabs.setupWithViewPager(viewPager);
    }
}
