package com.example.notehub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import models.Subscription;
import models.UpdatePasswordRequest;
import models.UpdatePasswordResponse;
import models.UploadAvatarResponse;
import models.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    ApiInterface apiService;
    private Uri imageUri;
    private View alertDialog;
    private BottomNavigationView bottomNavigationBar;
    private DialogFragment dialog;
    private
    // Bottom Navigation Select Item
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
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
    private ConstraintLayout background;
    private AnimationDrawable animationNavigationBar;
    private AnimationDrawable animationBackground;
    private TextView username;
    private TextView premium;
    private TextView totalNotes;
    private TextView avgRating;
    private TextView totalFavorite;
    private CircleImageView avatar;
    private Button updateProfile;
    private Button uploadPicture;
    private Button changePassword;
    private Button signOut;
    private Button subscription;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        apiService = MainActivity.buildHTTP();

        username = findViewById(R.id.settings_username);
        totalNotes = findViewById(R.id.settings_uploads_value);
        avgRating = findViewById(R.id.settings_rating_value);
        totalFavorite = findViewById(R.id.settings_favorite_value);
        premium = findViewById(R.id.settings_member);
        avatar = findViewById(R.id.settings_avatar);


        bottomNavigationBar = findViewById(R.id.bottom_navigation);
        bottomNavigationBar.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


        background = findViewById(R.id.settings_card);

        // Animation
        animationNavigationBar = (AnimationDrawable) bottomNavigationBar.getBackground();
        animationBackground = (AnimationDrawable) background.getBackground();

        //Time changes
        animationNavigationBar.setEnterFadeDuration(5000);
        animationNavigationBar.setExitFadeDuration(3000);

        animationNavigationBar.start();

        animationBackground.setEnterFadeDuration(5000);
        animationBackground.setExitFadeDuration(3000);
        animationBackground.start();

        subscription = findViewById(R.id.settings_button_subscription);
        subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Subscription> call = apiService.addSubscription(MainActivity.getToken(SettingsActivity.this));

                call.enqueue(new Callback<Subscription>() {
                    @Override
                    public void onResponse(Call<Subscription> call, Response<Subscription> response) {
                        if (response.errorBody() == null) {
                            loadUser();
                            showAlertMessage("Your premium status was successfully added for a month!", "Done");
                        }
                        else
                            showAlertMessage("Error. You've already received your monthly subscription.", "Done");
                    }

                    @Override
                    public void onFailure(Call<Subscription> call, Throwable t) {
                        showAlertMessage("Unable to add subscription.", "Done");
                    }
                });

            }
        });

        // Sign Out
        signOut = findViewById(R.id.settings_button_signout);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        updateProfile = findViewById(R.id.settings_button_profile);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        uploadPicture = findViewById(R.id.settings_button_upload);
        uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAvatar();
            }
        });

        changePassword = findViewById(R.id.settings_button_password);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
            }
        });

        loadUser();
    }

    public void loadUser() {
        String token = MainActivity.getToken(SettingsActivity.this);
        Call<User> call = apiService.getUser(token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.errorBody() == null) {
                    user = response.body();
                }
                updatePage();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                updatePage();
            }
        });
    }

    public void updatePage() {
        if (user == null) {
            username.setText("Unable to load");
            premium.setText("Unable to load");
            totalNotes.setText("?");
            avgRating.setText("?");
            totalFavorite.setText("?");
            avatar.setImageResource(R.drawable.blank);
        } else {
            username.setText(user.getUsername());
            if (user.isPremium())
                premium.setText("Premium Member");
            else
                premium.setText("Free Member");
            totalNotes.setText(Integer.toString(user.getTotalUploads()));
            totalFavorite.setText(Integer.toString(user.getTotalFavorites()));
            DecimalFormat round = new DecimalFormat("0.0");
            if (user.getAvgRating() == null)
                avgRating.setText("--");
            else
                avgRating.setText(round.format(Float.valueOf(user.getAvgRating())));
            if (user.getAvatar() == null)
                avatar.setImageResource(R.drawable.blank);
            else
                Picasso.with(SettingsActivity.this).load(MainActivity.getBaseUrl() + user.getAvatar()).fit().placeholder(R.drawable.blank).centerCrop().noFade().into(avatar);
        }
        username.setVisibility(View.VISIBLE);
        premium.setVisibility(View.VISIBLE);
        totalNotes.setVisibility(View.VISIBLE);
        avgRating.setVisibility(View.VISIBLE);
        totalFavorite.setVisibility(View.VISIBLE);
        avatar.setVisibility(View.VISIBLE);
    }

    // Add Group
    public void updateProfile() {
        final AlertDialog.Builder createDialog = new AlertDialog.Builder(SettingsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_profile, null);
        createDialog.setView(dialogView);

        MaterialButton updateProfileButton = dialogView.findViewById(R.id.dialog_profile_update);
        final EditText firstEdit = dialogView.findViewById(R.id.dialog_profile_first);
        firstEdit.setText(user.getFirstName());
        final EditText lastEdit = dialogView.findViewById(R.id.dialog_profile_last);
        lastEdit.setText(user.getLastName());
        final EditText emailEdit = dialogView.findViewById(R.id.dialog_profile_email);
        emailEdit.setText(user.getEmail());

        final AlertDialog showDialog = createDialog.show();

        // Click create button
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = MainActivity.getToken(SettingsActivity.this);
                User updateUser = new User();

                if (firstEdit.getText().toString().equals(""))
                    updateUser.setFirstName(user.getFirstName());
                else
                    updateUser.setFirstName(firstEdit.getText().toString());


                if (lastEdit.getText().toString().equals(""))
                    updateUser.setLastName(user.getLastName());
                else
                    updateUser.setLastName(lastEdit.getText().toString());


                if (emailEdit.getText().toString().equals(""))
                    updateUser.setEmail(user.getEmail());
                else
                    updateUser.setEmail(emailEdit.getText().toString());

                Call<User> callUser = apiService.updateUser(token, updateUser);

                callUser.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, final Response<User> userResponse) {
                        // Successfully get user
                        if (userResponse.errorBody() == null) {
                            // Store group name input into group object
                            new MaterialAlertDialogBuilder(SettingsActivity.this)
                                    .setMessage("Profile was successfully updated!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        } else {
                            showAlertMessage("Failed to update user.", "Ok");
                        }
                        loadUser();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        showAlertMessage("Failed to update user.", "Ok");
                        loadUser();
                    }
                });
                showDialog.dismiss();
            }
        });
    }

    // Add Group
    public void uploadAvatar() {
        final AlertDialog.Builder createDialog = new AlertDialog.Builder(SettingsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_avatar, null);
        this.alertDialog = dialogView;
        createDialog.setView(dialogView);

        MaterialButton choose = dialogView.findViewById(R.id.dialog_avatar_choose);
        MaterialButton upload = dialogView.findViewById(R.id.dialog_avatar_upload);
        CircleImageView avatar = dialogView.findViewById(R.id.dialog_avatar_picture);

        Picasso.with(SettingsActivity.this).load(MainActivity.getBaseUrl() + user.getAvatar()).fit().placeholder(R.drawable.blank).centerCrop().noFade().into(avatar);

        final AlertDialog showDialog = createDialog.show();

        // Click create button
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri != null)
                    verifyStoragePermissions();
                showDialog.dismiss();
            }
        });
    }

    private void openFileChooser() {
        String[] mimeTypes = {"image/jpeg", "image/png"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/jpeg|image/png")
                .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Checks if the app has permission to write to device storage
    // If the app does not has permission then the user will be prompted to grant permissions
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = SettingsActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            this.requestPermissions(
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            uploadFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty. Permission was granted.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadFile();
                }
                // permission denied
                else {

                }
                return;
            }
        }
    }

    private File createTempFile(String name) {
        File file = null;
        try {
            file = File.createTempFile(name, null, this.getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor = resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private void uploadFile() {
        final File tempFile = createTempFile("temp file");
        try {
            InputStream in = this.getContentResolver().openInputStream(imageUri);
            OutputStream out = new FileOutputStream(tempFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse(this.getContentResolver().getType(imageUri)), tempFile);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("new_avatar", queryName(this.getContentResolver(), imageUri), requestFile);

        Call<UploadAvatarResponse> callNoteFile = apiService.uploadAvatar(MainActivity.getToken(this), builder.build());
        // call note file
        callNoteFile.enqueue(new Callback<UploadAvatarResponse>() {
            @Override
            public void onResponse(Call<UploadAvatarResponse> call, Response<UploadAvatarResponse> response) {
                if (response.errorBody() == null) {
                    loadUser();
                    new MaterialAlertDialogBuilder(SettingsActivity.this)
                                    .setMessage("Profile picture was successfully uploaded!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                } else {
                    showAlertMessage("Upload failed.", "OK");
                }
                tempFile.delete();
            }

            @Override
            public void onFailure(Call<UploadAvatarResponse> call, Throwable t) {
                showAlertMessage("Upload failed.", "OK");
                tempFile.delete();
            }
        });
        imageUri = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (dialog != null) {
            dialog.onActivityResult(requestCode, requestCode, data);
            dialog = null;
        } else {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                // contains the uri of the image picked; will later upload to server
                imageUri = data.getData();
                CircleImageView avatar = alertDialog.findViewById(R.id.dialog_avatar_picture);
                Picasso.with(SettingsActivity.this).load(imageUri).fit().placeholder(R.drawable.blank).centerCrop().noFade().into(avatar);
            }
        }
    }

    public void updatePassword() {
        final AlertDialog.Builder createDialog = new AlertDialog.Builder(SettingsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_password, null);
        createDialog.setView(dialogView);

        MaterialButton updatePasswordButton = dialogView.findViewById(R.id.dialog_password_update);
        final EditText oldEdit = dialogView.findViewById(R.id.dialog_password_old);
        final EditText newEdit = dialogView.findViewById(R.id.dialog_password_new);

        final AlertDialog showDialog = createDialog.show();

        // Click create button
        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = MainActivity.getToken(SettingsActivity.this);

                if (oldEdit.getText().toString().equals("")) {
                    showAlertMessage("Enter old password.", "OK");
                    return;
                }
                if (newEdit.getText().toString().equals("")) {
                    showAlertMessage("Enter new password.", "OK");
                    return;
                }

                UpdatePasswordRequest updateRequest = new UpdatePasswordRequest(oldEdit.getText().toString(), newEdit.getText().toString());


                Call<UpdatePasswordResponse> call = apiService.updatePassword(token, updateRequest);

                call.enqueue(new Callback<UpdatePasswordResponse>() {
                    @Override
                    public void onResponse(Call<UpdatePasswordResponse> call, final Response<UpdatePasswordResponse> userResponse) {
                        // Successfully get user
                        if (userResponse.errorBody() == null) {
                            UpdatePasswordResponse response = userResponse.body();
                            SharedPreferences savePreferences = getSharedPreferences("NoteHub", Context.MODE_PRIVATE);
                            savePreferences.edit().putString("TOKEN", response.getToken()).apply();
                            // Store group name input into group object
                            new MaterialAlertDialogBuilder(SettingsActivity.this)
                                    .setMessage("Password was successfully updated!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                            loadUser();
                            showDialog.dismiss();
                        } else {
                            showAlertMessage("Incorrect password.", "Ok");
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdatePasswordResponse> call, Throwable t) {
                        showAlertMessage("Failed to update password.", "Ok");
                    }
                });
            }
        });
    }

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
