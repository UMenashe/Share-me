package com.example.shareme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class share_copy extends AppCompatActivity {

    TextView tvcopy;
    String txtcopy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_copy);
        tvcopy = findViewById(R.id.tvcopy);
        Intent intent=getIntent();
        txtcopy = intent.getExtras().getString("txtcopy");
        tvcopy.setText(txtcopy);
    }
}