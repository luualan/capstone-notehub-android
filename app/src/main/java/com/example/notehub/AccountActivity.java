package com.example.notehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import models.CreateUser;
import models.Token;
import models.User;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {

    private Button button;
    private ImageView icon;

    ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        apiService = MainActivity.buildHTTP();

        button = (Button) findViewById(R.id.create_account);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(v);
            }
        });

        icon = (ImageView) findViewById(R.id.back_arrow);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(v);
            }
        });
    }

    public void createAccount(View v) {
        EditText firstEdit = (EditText)findViewById(R.id.name);
        EditText lastEdit = (EditText)findViewById(R.id.school);
        EditText emailEdit = (EditText)findViewById(R.id.email);
        EditText usernameEdit = (EditText)findViewById(R.id.username);
        EditText passwordEdit = (EditText)findViewById(R.id.password);

        CreateUser user = new CreateUser();

        user.setEmail(emailEdit.getText().toString());
        user.setUsername(usernameEdit.getText().toString());
        user.setFirstName(firstEdit.getText().toString());
        user.setLastName(lastEdit.getText().toString());
        user.setPassword(passwordEdit.getText().toString());

        Call<User> call = apiService.createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                if (response.errorBody() == null) {
                    startActivity(intent);
                }
                else {
                    Toast.makeText(AccountActivity.this, "Email does not exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }

    public void goBack(View v) {
        Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
