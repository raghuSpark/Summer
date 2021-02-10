package com.raghu.summer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private TextView welcome,loginDescription,forgotPassword,newUser;
    private EditText phoneNumber,password;
    private View loginImage;
    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton=findViewById(R.id.login_button);
        loginDescription=findViewById(R.id.login_description);
        loginImage=findViewById(R.id.login_image);
        welcome=findViewById(R.id.welcome_text);
        phoneNumber=findViewById(R.id.phone_number_editText);
        password=findViewById(R.id.password_editText);
        forgotPassword=findViewById(R.id.forgot_password);
        newUser=findViewById(R.id.new_user);
        loginProgress=findViewById(R.id.loginProgress);

        loginButton.setOnClickListener(v -> {
            loginProgress.setVisibility(View.VISIBLE);
            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
        });
    }
}