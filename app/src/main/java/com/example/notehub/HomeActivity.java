package com.example.notehub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import adapters.NoteRecyclerViewAdapter;
import adapters.ViewPageAdapter;
import fragments.FavoriteFragment;
import fragments.MyNotesFragment;
import fragments.NoteCommentsFragment;
import fragments.NoteFilesFragment;
import models.CardView;
import models.Note;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity  {
    // Tabs
    protected TabLayout tabLayout;
    protected ViewPager viewPager;
    protected ViewPageAdapter adapter;
    AnimationDrawable animationTab;
    AnimationDrawable animationNavigationBar;

    // Bottom Navigation Bar
    protected BottomNavigationView bottomNavigationBar;
    protected DialogFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize
        tabLayout = findViewById(R.id.home_tab_layout);
        viewPager = findViewById(R.id.home_view_pager);
        adapter = new ViewPageAdapter(getSupportFragmentManager());

        // Added Fragments are here
        adapter.addFragment(new MyNotesFragment(), "Home");
        adapter.addFragment(new FavoriteFragment(), "Favorite");

        // Set up viewPager and tabLayout
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

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

        // Insert icons
       //tabLayout.getTabAt(0).setIcon(R.drawable.ic_person);
        //tabLayout.getTabAt(1).setIcon(R.drawable.ic_favorite_star);
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
                            startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                            return true;
                        case R.id.nav_search:
                            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
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
                            startActivity(new Intent(HomeActivity.this, GroupActivity.class));
                            return true;
                        case R.id.nav_settings:
                            Toast.makeText(HomeActivity.this, "settings selected", Toast.LENGTH_SHORT).show();
                            return true;
                    }
                    return false;
                }
            };

    // Return to sign up page
    public void signOut() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
