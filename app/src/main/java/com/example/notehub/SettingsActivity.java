package com.example.notehub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {
    private RelativeLayout toolbar;
    private BottomNavigationView bottomNavigationBar;
    private DialogFragment dialog;
    private AnimationDrawable animationToolBar;
    private AnimationDrawable animationNavigationBar;
    private MaterialButton signOut;
    private SwitchMaterial toggleButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bottomNavigationBar = findViewById(R.id.bottom_navigation);
        bottomNavigationBar.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        toolbar = findViewById(R.id.settings_tool_bar_layout);

        // Animation
        animationToolBar = (AnimationDrawable) toolbar.getBackground();
        animationNavigationBar = (AnimationDrawable) bottomNavigationBar.getBackground();

        //Time changes
        animationToolBar.setEnterFadeDuration(5000);
        animationToolBar.setExitFadeDuration(3000);

        animationToolBar.start();

        animationNavigationBar.setEnterFadeDuration(5000);
        animationNavigationBar.setExitFadeDuration(3000);

        animationNavigationBar.start();

        signOut = findViewById(R.id.settings_sign_out);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        toggleButton = findViewById(R.id.settings_subscription);
        toggleButton.setTextOff("Free");
        toggleButton.setTextOn("Premium");

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                }
                else {

                }
            }
        });
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
                            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                            return true;
                        case R.id.nav_search:
                            startActivity(new Intent(SettingsActivity.this, SearchActivity.class));
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
                            startActivity(new Intent(SettingsActivity.this, GroupActivity.class));
                            return true;
                        case R.id.nav_settings:
                            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
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
