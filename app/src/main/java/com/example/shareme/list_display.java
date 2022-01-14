package com.example.shareme;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class list_display extends AppCompatActivity implements View.OnClickListener {
MaterialToolbar topAppBar;
FirebaseDatabase database;
DatabaseReference myRef;
String id ,owner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_display);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        topAppBar = findViewById(R.id.topAppBar);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        Intent intent=getIntent();
       owner = intent.getExtras().getString("owner");
       id = intent.getExtras().getString("id");
        DatabaseReference itemRef =  myRef.child("users").child(owner).child("userdocs").child(id);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Docinfo d = dataSnapshot.getValue(Docinfo.class);
                loadInfo(d);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        itemRef.addValueEventListener(postListener);

    }

    public void loadInfo(Docinfo docinfo){
        topAppBar.setTitle(docinfo.getTitle());
        topAppBar.setSubtitle(docinfo.getLastUpdate());
    }
    @Override
    public void onClick(View v) {

    }
}