package com.example.notehub;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
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

import models.CreateUser;
import models.Login;
import models.Token;
import models.User;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

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
                if (usernameLoginEdit.getText().toString().trim().isEmpty())
                    usernameLoginEdit.setError("Please fill out this field.");

                if (passwordLoginEdit.getText().toString().trim().isEmpty()) {
                    txtInLayoutLoginPassword.setPasswordVisibilityToggleEnabled(false);
                    passwordLoginEdit.setError("Please fill out this field.");
                } else
                    txtInLayoutLoginPassword.setPasswordVisibilityToggleEnabled(true);

                if (response.errorBody() == null) {
                    startActivity(intent);

                    //Save token here
                    Token bodyToken = response.body();
                    String token = bodyToken.getToken();
                    SharedPreferences savePreferences = getSharedPreferences("NoteHub", Context.MODE_PRIVATE);
                    savePreferences.edit().putString("TOKEN", token).apply();
                } else {
                    showAlertMessage("Incorrect username or password.");
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
            }
        });
    }

    // Pop up create account
    public void signUp() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.register, null);
        dialog.setView(dialogView);

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
                CreateUser user = new CreateUser();
                user.setFirstName(firstEdit.getText().toString());
                user.setLastName(lastEdit.getText().toString());
                user.setEmail(emailEdit.getText().toString());
                user.setUsername(usernameEdit.getText().toString());
                user.setPassword(passwordEdit.getText().toString());

                Call<User> call = apiService.createUser(user);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);

                        if (firstEdit.getText().toString().trim().isEmpty())
                            firstEdit.setError("Please fill out this field.");

                        if (lastEdit.getText().toString().trim().isEmpty())
                            lastEdit.setError("Please fill out this field.");

                        if (emailEdit.getText().toString().trim().isEmpty())
                            emailEdit.setError("Please fill out this field.");

                        if (usernameEdit.getText().toString().trim().isEmpty())
                            usernameEdit.setError("Please fill out this field.");

                        if (passwordEdit.getText().toString().trim().isEmpty())
                            passwordEdit.setError("Please fill out this field.");

                        if (response.errorBody() == null)
                            startActivity(intent);
                        else
                            showAlertMessage("Email does not exist.");
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                    }
                });
            }
        });
        dialog.show();
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
