package com.example.notehub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import static android.util.TypedValue.TYPE_NULL;

public class HomeActivity extends AppCompatActivity  {

    private FloatingActionButton floatingActionButton;
    private BottomAppBar bottomAppBar;
    private DialogFragment dialog;

    private ArrayList<CardView> cards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get card data
        Intent intent = getIntent();
        CardView cardData = intent.getParcelableExtra("Example item");

        // Set card data on home page
        if(cardData != null) {
            int imageRes = cardData.getimageFavorite();
            String line1 = cardData.getTitle();
            String line2 = cardData.getUniversity();

            ImageView imageView = findViewById(R.id.image_activity2);
            imageView.setImageResource(imageRes);

            TextView textView1 = findViewById(R.id.text1_activity2);
            textView1.setText(line1);

            TextView textView2 = findViewById(R.id.text2_activity2);
            textView2.setText(line2);

            cards.add(cardData);
            TextView listOfText = findViewById(R.id.list_of_text);

 /*       String concat = "";
        for(CardView card : cards) {
            concat += card.getText1() + "\n";
            concat += card.getText2() + "\n";
        }
        listOfText.setText(concat);*/
        }

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

        MenuItem searchItem = menu.findItem(R.id.nav_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.clearFocus();
        enableSearchView(searchView, false);
        return true;
    }

    // Clicks for icons located on toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_search:
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);

                //floatingActionButton.hide();
               // bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
               // item.expandActionView();
                return true;
            case R.id.nav_groups:
                bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                Toast.makeText(this, "groups selected", Toast.LENGTH_SHORT).show();
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

    private void enableSearchView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                enableSearchView(child, enabled);
            }
        }
    }

    public void home(View v) {
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void signOut() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
