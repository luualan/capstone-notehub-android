package com.example.notehub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import models.CreateUser;
import models.Login;
import models.Token;
import models.UploadAvatarResponse;
import models.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Uri imageUri;
    private View alertDialog;
    private AlertDialog openDialog;
    private Button signInButton;
    private TextView signUpText;
    private ImageView icon;
    private Button registerButton;
    ApiInterface apiService;

    AnimationDrawable animationDrawable;
    FrameLayout frameLayout;

    private EditText usernameLoginEdit;
    private EditText passwordLoginEdit;

    private EditText firstEdit;
    private EditText lastEdit;
    private EditText emailEdit;
    private EditText usernameEdit;
    private EditText passwordEdit;

    TextInputLayout txtInLayoutLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        apiService = MainActivity.buildHTTP();

        signInButton = findViewById(R.id.sign_in);
        signUpText = findViewById(R.id.sign_up);
        signUpText.setMovementMethod(LinkMovementMethod.getInstance());

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(v);
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(LoginActivity.this, "Email does not exist.", Toast.LENGTH_SHORT).show();
                signUp();
            }
        });

        // Animation
        frameLayout = findViewById(R.id.frame);
        animationDrawable = (AnimationDrawable) frameLayout.getBackground();

        //Time changes
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);

        animationDrawable.start();


        //icon = (ImageView) findViewById(R.id.back_arrow);

  /*      icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(v);
            }
        });*/
    }

    public void signIn(View v) {
        usernameLoginEdit = findViewById(R.id.username);
        passwordLoginEdit = findViewById(R.id.password);
        txtInLayoutLoginPassword = findViewById(R.id.txtInLayoutLoginPassword);

        Login login = new Login();
        String username = usernameLoginEdit.getText().toString();
        String password = passwordLoginEdit.getText().toString();

        login.setUsername(username);
        login.setPassword(password);
        Call<Token> call = apiService.loginUser(login);

        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

                // Success
                if (response.errorBody() == null) {
                    startActivity(intent);

                    //Save token here
                    Token bodyToken = response.body();
                    String token = bodyToken.getToken();
                    SharedPreferences savePreferences = getSharedPreferences("NoteHub", Context.MODE_PRIVATE);
                    savePreferences.edit().putString("TOKEN", token).apply();
                }

                // Failed to log in
                else {
                    boolean usernameEmptyCheck = usernameLoginEdit.getText().toString().trim().isEmpty();
                    boolean passwordEmptyCheck = passwordLoginEdit.getText().toString().trim().isEmpty();

                    if (usernameEmptyCheck)
                        usernameLoginEdit.setError("Please fill out this field.");

                    if (passwordEmptyCheck) {
                        txtInLayoutLoginPassword.setPasswordVisibilityToggleEnabled(false);
                        passwordLoginEdit.setError("Please fill out this field.");
                    } else
                        txtInLayoutLoginPassword.setPasswordVisibilityToggleEnabled(true);

                    if (!usernameEmptyCheck && !passwordEmptyCheck) {
                        showAlertMessage("Incorrect username or password.");
                    }
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
            }
        });
    }

    private void openFileChooser() {
        imageUri = null;
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
        int permission = LoginActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            this.requestPermissions(
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            createAccount();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty. Permission was granted.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createAccount();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            // contains the uri of the image picked; will later upload to server
            imageUri = data.getData();
            Picasso.with(this).load(imageUri).fit().placeholder(R.drawable.blank).centerCrop().noFade().into(icon);
        } else {
            icon.setImageResource(R.drawable.blank);
        }
    }

    public void createAccount() {
        CreateUser user = new CreateUser();
        user.setFirstName(firstEdit.getText().toString());
        user.setLastName(lastEdit.getText().toString());
        user.setEmail(emailEdit.getText().toString());
        user.setUsername(usernameEdit.getText().toString());
        user.setPassword(passwordEdit.getText().toString());

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("email", user.getEmail());
        builder.addFormDataPart("first_name", user.getFirstName());
        builder.addFormDataPart("last_name", user.getLastName());
        builder.addFormDataPart("username", user.getUsername());
        builder.addFormDataPart("password", user.getPassword());
        if(imageUri != null) {
            final File tempFile = createTempFile("temp file");
            try {
                InputStream in = LoginActivity.this.getContentResolver().openInputStream(imageUri);
                OutputStream out = new FileOutputStream(tempFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
                RequestBody requestFile = RequestBody.create(MediaType.parse(LoginActivity.this.getContentResolver().getType(imageUri)), tempFile);
                builder.addFormDataPart("avatar", queryName(LoginActivity.this.getContentResolver(), imageUri), requestFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Call<User> call = apiService.createUser(builder.build());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // Successfully sign up
                if (response.errorBody() == null) {
                    openDialog.dismiss();
                    new MaterialAlertDialogBuilder(LoginActivity.this)
                            .setMessage("Sign up successful!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
                // Failed to sign up
                else {
                    // boolean firstNameEmptyCheck = firstEdit.getText().toString().trim().isEmpty();
                    // boolean lastNameEmptyCheck = lastEdit.getText().toString().trim().isEmpty();
                    boolean emailEmptyCheck = emailEdit.getText().toString().trim().isEmpty();
                    boolean usernameEmptyCheck = usernameEdit.getText().toString().trim().isEmpty();
                    boolean passwordEmptyCheck = passwordEdit.getText().toString().trim().isEmpty();
                    boolean checkAllEmpty = !emailEmptyCheck && !usernameEmptyCheck && !passwordEmptyCheck;

                          /*  if (firstNameEmptyCheck)
                                firstEdit.setError("Please fill out this field.");

                            if (lastNameEmptyCheck)
                                lastEdit.setError("Please fill out this field.");*/

                    if (emailEmptyCheck)
                        emailEdit.setError("Please fill out this field.");

                    if (usernameEmptyCheck)
                        usernameEdit.setError("Please fill out this field.");

                    if (passwordEmptyCheck)
                        passwordEdit.setError("Please fill out this field.");

                    if (checkAllEmpty)
                        showAlertMessage("Username is not unique.");

                    else if (checkAllEmpty && !emailEdit.getText().toString().contains("@"))
                        showAlertMessage("Email does not exist.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showAlertMessage("Unable to create account.");
            }
        });
    }

    // Pop up create account
    public void signUp() {
        imageUri = null;
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.register, null);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        openDialog = alertDialog;

        icon = dialogView.findViewById(R.id.profile_picture);
        firstEdit = dialogView.findViewById(R.id.reg_first_name);
        lastEdit = dialogView.findViewById(R.id.reg_last_name);
        emailEdit = dialogView.findViewById(R.id.reg_email);
        usernameEdit = dialogView.findViewById(R.id.reg_username);
        passwordEdit = dialogView.findViewById(R.id.reg_password);

        //Register Section
        registerButton = dialogView.findViewById(R.id.reg_register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null)
                    verifyStoragePermissions();
                else
                    createAccount();
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        alertDialog.show();
    }

    public void goBack(View v) {
        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void showAlertMessage(String message) {
        new MaterialAlertDialogBuilder(LoginActivity.this)
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .show();
    }

}
