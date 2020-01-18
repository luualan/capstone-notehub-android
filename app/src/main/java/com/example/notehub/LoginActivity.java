package com.example.notehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import models.Login;
import models.Token;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button button;
    private TextView signUpText;
    ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        apiService = MainActivity.buildHTTP();

         button = (Button) findViewById(R.id.sign_in);
        signUpText = (TextView) findViewById(R.id.sign_up);
         button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(v);
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(v);
            }
        });
    }

    public void signIn(View v) {
        EditText usernameEdit = (EditText)findViewById(R.id.username);
        EditText passwordEdit = (EditText)findViewById(R.id.password);
        Login login = new Login();

        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        login.setUsername(username);
        login.setPassword(password);
        Call<Token> call = apiService.loginUser(login);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                TextView output = (TextView)findViewById(R.id.output_error);
                if (response.errorBody() == null) {
                    startActivity(intent);
                    output.setText("");
                }
                else {
                    output.setText("Error, wrong information.");
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });
    }

    public void signUp(View v) {
        TextView output = (TextView)findViewById(R.id.output_error);
        output.setText("");
        Intent intent = new Intent(LoginActivity.this, AccountActivity.class);
        startActivity(intent);
    }
}
