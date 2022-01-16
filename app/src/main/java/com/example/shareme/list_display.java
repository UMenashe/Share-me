package com.example.shareme;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class list_display extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
MaterialToolbar topAppBar;
FirebaseDatabase database;
DatabaseReference myRef;
String id ,owner;
Dialog addialog;
Button addbtn;
TextView alertTv;
ListView lv;
ListItemTarget lit;
private ArrayList<ListItemTarget> listItems;
private ItemsListAdapter adap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_display);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        topAppBar = findViewById(R.id.topAppBar);
        addbtn = findViewById(R.id.addbtn);
        alertTv = findViewById(R.id.alert);
        lv = findViewById(R.id.lvitems);
        addbtn.setOnClickListener(this);
        lv.setOnItemClickListener(this);
        listItems = new ArrayList<ListItemTarget>();
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
        loadItems();
    }

    private void loadItems() {
        if(listItems.isEmpty()){
            alertTv.setText("אין פריטים ברשימה");
            alertTv.setVisibility(View.VISIBLE);
            return;
        }else{
            alertTv.setVisibility(View.INVISIBLE);
        }
        adap = new ItemsListAdapter(this, R.layout.list_item, listItems);
        lv.setAdapter(adap);
    }
    public void addItemTolist(){
      ListItemTarget li = new ListItemTarget("ניסוי",10,"et");
      listItems.add(li);
      loadItems();
    }

//    public void showAddItemDialog()
//    {
//        addialog= new Dialog(this);
//        addialog.setContentView(R.layout.custom_layout);
//        addialog.setTitle("add item");
//        addialog.setCancelable(true);
//        btnCustomLogin=d.findViewById(R.id.btnDialogLogin);
//        btnCustomLogin.setOnClickListener(this);
//        addialog.show();
//    }


    public void loadInfo(Docinfo docinfo){
        topAppBar.setTitle(docinfo.getTitle());
        topAppBar.setSubtitle(docinfo.getLastUpdate());
    }
    @Override
    public void onClick(View v) {
      if(v == addbtn){
          addItemTolist();
      }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long viewId = view.getId();
        if (viewId == R.id.btnplus) {

          lit = listItems.get(position);
          lit.setCurrentCount(lit.getCurrentCount() +1);
          loadItems();
        }
    }
}