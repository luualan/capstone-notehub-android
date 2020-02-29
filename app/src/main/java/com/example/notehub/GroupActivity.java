package com.example.notehub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import adapters.ViewPageAdapter;
import fragments.MyInvitationsFragment;
import fragments.MyGroupsFragment;

public class GroupActivity extends AppCompatActivity {
    // Tabs
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;
    private AnimationDrawable animationTab;
    private AnimationDrawable animationNavigationBar;

    // Bottom Navigation Bar
    protected BottomNavigationView bottomNavigationBar;
    protected DialogFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        // Initialize
        tabLayout = findViewById(R.id.group_tab_layout);
        viewPager = findViewById(R.id.group_view_pager);
        adapter = new ViewPageAdapter(getSupportFragmentManager());

        // Added Fragments are here
        adapter.addFragment(new MyGroupsFragment(), "Groups");
        adapter.addFragment(new MyInvitationsFragment(), "Invitations");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MyGroupsFragment groupFragment = (MyGroupsFragment)adapter.getItem(0);
                MyInvitationsFragment notificationFragment = (MyInvitationsFragment) adapter.getItem(1);
                switch (position) {
                    case 0:
                        notificationFragment.clear();
                        groupFragment.refresh();
                        break;
                    case 1:
                        groupFragment.clear();
                        notificationFragment.refresh();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Set up viewPager and tabLayout
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        //noteID = getIntent().getIntExtra("noteID", -1);
        //noteTitle = getIntent().getStringExtra("noteTitle");

        // Insert icons
        //tabLayout.getTabAt(0).setIcon(R.drawable.ic_file);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_comment);

        bottomNavigationBar = findViewById(R.id.bottom_navigation);
        bottomNavigationBar.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        // Animation
        animationTab = (AnimationDrawable) tabLayout.getBackground();
        animationNavigationBar = (AnimationDrawable) bottomNavigationBar.getBackground();

        //Time changes
        animationTab.setEnterFadeDuration(5000);
        animationTab.setExitFadeDuration(3000);

        animationTab.start();

        animationNavigationBar.setEnterFadeDuration(5000);
        animationNavigationBar.setExitFadeDuration(3000);

        animationNavigationBar.start();
    }

     @Override
    public void onPause() {
        super.onPause();
        clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

     public void clear() {
        MyGroupsFragment groupFragment = (MyGroupsFragment)adapter.getItem(0);
        MyInvitationsFragment notificationFragment = (MyInvitationsFragment) adapter.getItem(1);
        groupFragment.clear();
        notificationFragment.clear();
    }


    public void refresh() {
        MyGroupsFragment groupFragment = (MyGroupsFragment)adapter.getItem(0);
        MyInvitationsFragment notificationFragment = (MyInvitationsFragment) adapter.getItem(1);
        groupFragment.refresh();
        notificationFragment.refresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialog.onActivityResult(requestCode, requestCode, data);
    }
    
    // Bottom Navigation Select Item
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch(item.getItemId()) {
                        case R.id.nav_home:
                            startActivity(new Intent(GroupActivity.this, HomeActivity.class));
                            return true;
                        case R.id.nav_search:
                            startActivity(new Intent(GroupActivity.this, SearchActivity.class));
                            return true;
                        case R.id.nav_upload:
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            bundle.putInt("groupID", -1);
                            dialog = UploadActivity.newInstance();
                            dialog.setArguments(bundle);
                            dialog.show(ft, "dialog");
                            return true;
                        case R.id.nav_groups:
                            startActivity(new Intent(GroupActivity.this, GroupActivity.class));
                            return true;
                        case R.id.nav_settings:
                            startActivity(new Intent(GroupActivity.this, SettingsActivity.class));
                            return true;
                    }
                    return false;
                }
            };

    // Return to sign up page
    public void signOut() {
        Intent intent = new Intent(GroupActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
