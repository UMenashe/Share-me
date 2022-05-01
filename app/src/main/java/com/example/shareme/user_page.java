package com.example.shareme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class user_page extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TextView username,useremail;
    ImageView userImage;
    LinearLayout btnedit,btnlogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        username = findViewById(R.id.username);
        useremail = findViewById(R.id.useremail);
        userImage = findViewById(R.id.userImage);
        btnlogout = findViewById(R.id.btnlogout);
        btnedit = findViewById(R.id.btnedit);
        btnedit.setOnClickListener(this);
        btnlogout.setOnClickListener(this);
        username.setText(currentUser.getDisplayName());
        useremail.setText(currentUser.getEmail());
        Picasso.get().load(currentUser.getPhotoUrl()).into(userImage);
    }

    public void userLogout(){
        mAuth.signOut();
    }

    @Override
    public void onClick(View v) {
        if(v == btnedit){
            Toast.makeText(this,"btnedit", Toast.LENGTH_SHORT).show();
        }
        if (v == btnlogout){
            userLogout();
        }
    }
}