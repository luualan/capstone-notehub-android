package com.example.notehub;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import adapters.ViewPageAdapter;
import fragments.NoteCommentsFragment;
import fragments.NoteFilesFragment;

import static androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM;

public class NoteActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;

    private int noteID;
    private String noteTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Initialize
        tabLayout = findViewById(R.id.note_tab_layout);
        viewPager = findViewById(R.id.note_view_pager);
        adapter = new ViewPageAdapter(getSupportFragmentManager());

        // Added Fragments are here
        adapter.addFragment(new NoteFilesFragment(), "Files");
        adapter.addFragment(new NoteCommentsFragment(), "Comments");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                NoteFilesFragment fileFragment = (NoteFilesFragment)adapter.getItem(0);
                NoteCommentsFragment  commentFragment = (NoteCommentsFragment) adapter.getItem(1);
                switch (position) {
                    case 0:
                        commentFragment.clear();
                        fileFragment.refresh();
                        break;
                    case 1:
                        fileFragment.clear();
                        commentFragment.refresh();
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

        noteID = getIntent().getIntExtra("noteID", -1);
        noteTitle = getIntent().getStringExtra("noteTitle");
        boolean startComment = getIntent().getBooleanExtra("startComment", false);

        if (startComment)
            viewPager.setCurrentItem(1);

        // Insert icons
       //tabLayout.getTabAt(0).setIcon(R.drawable.ic_file);
       //tabLayout.getTabAt(1).setIcon(R.drawable.ic_comment);

        // Remove shadow from action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_navigation, menu);

        getSupportActionBar().setDisplayOptions(DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_title_search);

        // Style title for top app bar and hide it
        TextView actionBarTitle = findViewById(R.id.action_bar_search_title);
        actionBarTitle.setText(noteTitle);

        // Display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Hide search icon
        menu.getItem(0).setVisible(false);
        return true;
    }

    // Back Button on app bar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public int getNoteId() {
        return noteID;
    }

}
