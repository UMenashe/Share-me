package com.example.shareme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView im;
    FirebaseAuth mAuth;
    FloatingActionButton addBtn;
    GridView gv;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        addBtn = findViewById(R.id.fab);
        addBtn.setOnClickListener(this);
        gv = findViewById(R.id.gv);
        im = findViewById(R.id.userImage);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(MainActivity.this, currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
            myRef.child("users").child(currentUser.getUid()).child("userdocs").setValue("null");
//            Toast.makeText(this,String.valueOf(currentUser.getPhotoUrl()),Toast.LENGTH_LONG).show();
//            Picasso.get().load(currentUser.getPhotoUrl()).into(im);
        }else{
            showSignUpage();
        }

    }
    public void showSignUpage(){
        Intent signUppage = new Intent(this,signup_page.class);
        startActivity(signUppage);
    }

    public void addDoc(String type,String title){
        DatabaseReference newRef = myRef.child("users").child(currentUser.getUid()).child("userdocs").push();
        String timeObj = String.valueOf(LocalDateTime.now());
        Docinfo newdoc = new Docinfo(type,newRef.getKey(),timeObj,timeObj,title,currentUser.getUid());
        newRef.setValue(newdoc);
        Intent displayListIntent = new Intent(this,list_display.class);
        displayListIntent.putExtra("owner",newdoc.getOwner());
        displayListIntent.putExtra("id",newdoc.getId());
        startActivity(displayListIntent);
    }

    @Override
    public void onClick(View v) {
        if(v == addBtn){
            Toast.makeText(this,"addBtn pressed",Toast.LENGTH_LONG).show();
            addDoc("targetList","הרשימה החדשה שלי");
        }
    }
}