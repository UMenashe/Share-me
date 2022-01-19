package com.example.shareme;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
FloatingActionButton addbtn;
TextView alertTv;
ListView lv;
ListItemTarget lit;
private ArrayList<ListItemTarget> listItems;
private ItemsListAdapter adap;
BottomSheetDialog bottomSheetDialog;
Button btnadditem;
TextInputEditText itemname,countarget;
TextInputLayout layoutname,layoutcount;

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
        DatabaseReference Itemsofdocs = myRef.child("users").child(owner).child("Itemsofdocs").child(id);

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
        ValueEventListener itemsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == "null"){
                    return;
                }
                listItems = new ArrayList<ListItemTarget>();
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()) {
                    ListItemTarget itemt = itemSnapshot.getValue(ListItemTarget.class);
                    listItems.add(itemt);
                }
                loadItems();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        Itemsofdocs.addValueEventListener(itemsListener);
    }

    private void loadItems() {
        if(listItems.isEmpty()){
            alertTv.setText("אין פריטים ברשימה");
            alertTv.setVisibility(View.VISIBLE);
            return;
        }else{
            alertTv.setVisibility(View.GONE);
        }
        adap = new ItemsListAdapter(this, R.layout.list_item, listItems);
        lv.setAdapter(adap);
    }
    public void addItemTolist(String name,String targetCount){
        DatabaseReference itemRef = myRef.child("users").child(owner).child("Itemsofdocs").child(id).push();
        ListItemTarget li = new ListItemTarget(name,Integer.parseInt(targetCount),itemRef.getKey());
        listItems.add(li);
        itemRef.setValue(li);
        loadItems();
    }

    public void showBottomSheet(){
        bottomSheetDialog = new BottomSheetDialog(
                list_display.this,R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.layout_bottom_additem,
                (LinearLayout)findViewById(R.id.bottomsheetadditem)
        );

        itemname = bottomSheetView.findViewById(R.id.itemname);
        countarget = bottomSheetView.findViewById(R.id.countarget);
        btnadditem = bottomSheetView.findViewById(R.id.btnadditem);
        layoutname = bottomSheetView.findViewById(R.id.layoutname);
        layoutcount = bottomSheetView.findViewById(R.id.layoutcount);
        btnadditem.setOnClickListener(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();
    }


    public void loadInfo(Docinfo docinfo){
        topAppBar.setTitle(docinfo.getTitle());
        topAppBar.setSubtitle(docinfo.getLastUpdate());
    }
    @Override
    public void onClick(View v) {
      if(v == addbtn){
          showBottomSheet();
      }
      if(v == btnadditem){
          String nameitemtext = itemname.getText().toString();
          String counttargettext = countarget.getText().toString();
          if(nameitemtext.length() == 0){
              layoutname.setError("הכנס שם");
              return;
          }
          if(counttargettext.length() == 0){
              layoutcount.setError("הכנס יעד");
              return;
          }
          addItemTolist(nameitemtext,counttargettext);
          bottomSheetDialog.dismiss();
      }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long viewId = view.getId();
        if (viewId == R.id.btnplus) {
          lit = listItems.get(position);
            lit.addCount();
            DatabaseReference item= myRef.child("users").child(owner).child("Itemsofdocs").child(this.id).child(lit.getId());
            item.setValue(lit);
            loadItems();
        }
    }
}