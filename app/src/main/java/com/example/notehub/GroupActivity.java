package com.example.notehub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import adapters.ViewPageAdapter;
import fragments.GroupsNotificationFragment;
import fragments.MyGroupsFragment;
import fragments.NoteCommentsFragment;
import fragments.NoteFilesFragment;

public class GroupActivity extends AppCompatActivity {
    // Tabs
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;

    // Bottom Navigation Bar
    private FloatingActionButton floatingActionButton;
    private BottomAppBar bottomAppBar;
    private DialogFragment dialog;


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
        adapter.addFragment(new GroupsNotificationFragment(), "Notification");

        // Set up viewPager and tabLayout
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        //noteID = getIntent().getIntExtra("noteID", -1);
        //noteTitle = getIntent().getStringExtra("noteTitle");

        // Insert icons
        //tabLayout.getTabAt(0).setIcon(R.drawable.ic_file);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_comment);

        // Bottom App bar
        bottomAppBar = findViewById(R.id.bottomAppBar);
        // main line for setting menu in bottom app bar
        setSupportActionBar(bottomAppBar);

        // Click home icon to go to home screen
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home(v);
            }
        });

        floatingActionButton = findViewById(R.id.fab);

        // Click floating action button opens full screen dialog screen
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                dialog = UploadActivity.newInstance();
                dialog.show(ft, "dialog");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialog.onActivityResult(requestCode, requestCode, data);
    }

    // Adds the tool bar to bottom navigation bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_navigation, menu);
        return true;
    }

    // Clicks for icons located on toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_search:
                Intent homeIntent = new Intent(GroupActivity.this, SearchActivity.class);
                startActivity(homeIntent);
                //floatingActionButton.hide();
                // bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                // item.expandActionView();
                return true;
            case R.id.nav_groups:
                bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                Intent groupIntent = new Intent(GroupActivity.this, GroupActivity.class);
                startActivity(groupIntent);
                return true;
            case R.id.nav_settings:
                bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                Toast.makeText(this, "settings selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_sign_out:
                bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Return to home page
    public void home(View v) {
        Intent intent = new Intent(GroupActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    // Return to sign up page
    public void signOut() {
        Intent intent = new Intent(GroupActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
