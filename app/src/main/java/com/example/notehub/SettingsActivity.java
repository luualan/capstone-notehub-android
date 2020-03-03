package com.example.notehub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import models.Subscription;
import models.User;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    private RelativeLayout toolbar;
    private BottomNavigationView bottomNavigationBar;
    private DialogFragment dialog;
    private AnimationDrawable animationToolBar;
    private AnimationDrawable animationNavigationBar;
    private MaterialButton signOut;
    private MaterialButton subscription;

    private TextInputEditText firstName;
    private TextInputEditText lastName;
    private TextInputEditText email;
    private TextInputEditText currentPassword;
    private TextInputEditText newPassword;

    ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        apiService = MainActivity.buildHTTP();

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

        firstName = findViewById(R.id.settings_first_name);
        lastName = findViewById(R.id.settings_last_name);
        email = findViewById(R.id.settings_email);
        currentPassword = findViewById(R.id.settings_current_password);
        newPassword = findViewById(R.id.settings_new_password);
        subscription = findViewById(R.id.settings_subscription);

   /*     firstName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    Call<User> userCall = apiService.getUser(MainActivity.getToken(SettingsActivity.this));
                    userCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, final Response<User> userCallResponse) {
                            if(userCallResponse.errorBody() == null) {

                                Log.e("dsdadadad", "dadadadadaad");
                                //userCallResponse.body().setFirstName();
                                //Call<User> updateCall = apiService.

                            }
                            else {

                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {

                        }
                    });
                }

                return false;
            }
        });
*/
        subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Subscription> call = apiService.addSubscription(MainActivity.getToken(SettingsActivity.this));

                call.enqueue(new Callback<Subscription>() {
                    @Override
                    public void onResponse(Call<Subscription> call, Response<Subscription> response) {
                        if(response.errorBody() == null)
                            showAlertMessage("Your premium status was successfully added for a month!", "Done");
                        else
                            showAlertMessage("Error. You've already received your monthly subscription.", "Done");
                    }

                    @Override
                    public void onFailure(Call<Subscription> call, Throwable t) {

                    }
                });

            }
        });

        // Sign Out
        signOut = findViewById(R.id.settings_sign_out);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

   /*     toggleButton = findViewById(R.id.settings_subscription);
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
        });*/
    }

    public void getSubscription() {

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
        SharedPreferences savePreferences = getSharedPreferences("NoteHub", Context.MODE_PRIVATE);
        savePreferences.edit().putString("TOKEN", "").apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showAlertMessage(String message, String buttonText) {
        new MaterialAlertDialogBuilder(SettingsActivity.this)
                .setMessage(message)
                .setPositiveButton(buttonText, null)
                .show();
    }
}
