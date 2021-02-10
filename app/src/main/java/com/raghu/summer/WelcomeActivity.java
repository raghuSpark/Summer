package com.raghu.summer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private Button acceptButton;
    private TextView privacyText,appName,appDescription;
    private ImageView blackBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        acceptButton=findViewById(R.id.accept_button);
        privacyText=findViewById(R.id.privacyText);
        appName=findViewById(R.id.appName);
        appDescription=findViewById(R.id.app_description);
        blackBackground=findViewById(R.id.imageView);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
                finish();
            }
        });

    }
}