package itu.android.csc519_earthquake_92689;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import itu.android.csc519_earthquake_92689.adapter.TabAdapter;
import itu.android.csc519_earthquake_92689.callback.DataPrepareCallback;
import itu.android.csc519_earthquake_92689.model.Earthquake;

import static android.Manifest.permission.CALL_PHONE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        DataPrepareCallback {
    public static final String TAG = "MainActivity";
    public static final String PERSONAL_POHNE_NUMBER = "626-316-3241";
    public static final String PERSONAL_EMAIL = "tofywing@gmail";
    public static final String KEY_EARTHQUAKES = "keysForTheEarthquakesArrayList";
    public static final String KEY_COUNT = "count";
    public static final int CALL_PHONE_REQUEST = 0;

    TabLayout mTab;
    ViewPager mViewPager;
    TabAdapter mTabAdapter;
    ArrayList<Earthquake> mEarthquakes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(2);
        mTabAdapter = new TabAdapter(getSupportFragmentManager());
        mTabAdapter.addFragment(new ListTab(), "LASTED");
        int count;
        if (savedInstanceState != null) {
            count = savedInstanceState.getInt(KEY_COUNT);
            mEarthquakes = savedInstanceState.getParcelableArrayList(KEY_EARTHQUAKES);
            if (count == 2) {
                mTabAdapter.addFragment(MapTab.newInstance(mEarthquakes), "MAP");
            }
        }
        mViewPager.setAdapter(mTabAdapter);
        mTab = (TabLayout) findViewById(R.id.tabs);
        mTab.setupWithViewPager(mViewPager);
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
        int id = item.getItemId();
        if (id == R.id.action_reload) {
            sendBroadcast(new Intent(Intent.ACTION_ATTACH_DATA));
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_send) {
            performEmailAction();
        } else if (id == R.id.nav_call) {
            phoneCallAction();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDataPrepared(ArrayList<Earthquake> earthquakes) {
        mEarthquakes = earthquakes;
        if (mTabAdapter.getCount() < 2) {
            mTabAdapter.addFragment(MapTab.newInstance(earthquakes), "MAP");
        } else {
            MapTab fragment = (MapTab) mTabAdapter.getItem(1);
            fragment.reload(earthquakes);
        }
        mTabAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_COUNT, mTabAdapter.getCount());
        outState.putParcelableArrayList(KEY_EARTHQUAKES, mEarthquakes);
    }

    private void phoneCallAction() {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse("tel:" + PERSONAL_POHNE_NUMBER));
        if (ActivityCompat.checkSelfPermission(this, CALL_PHONE) != PackageManager
                .PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST);
            return;
        }
        startActivity(phoneIntent);
    }

    private void performEmailAction() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, PERSONAL_EMAIL);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Hi");
        intent.putExtra(Intent.EXTRA_TEXT, "Hi, this is");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }
}
