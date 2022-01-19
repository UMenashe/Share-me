package com.example.shareme;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    ImageView im;
    FirebaseAuth mAuth;
    FloatingActionButton addBtn;
    GridView gvdocs;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser currentUser;
    TextView alert2;
    private ArrayList<Docinfo> gridItems;
    private GridItemsAdapter adap;
    Docinfo docItem;
    Docinfo docItemSelected;
    ProgressBar pb;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout btndelete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        addBtn = findViewById(R.id.fab);
        addBtn.setOnClickListener(this);
        gvdocs = findViewById(R.id.gvdocs);
        im = findViewById(R.id.userImage);
        alert2 = findViewById(R.id.alert2);
        alert2.setVisibility(View.INVISIBLE);
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        gvdocs.setOnItemClickListener(this);
        gvdocs.setOnItemLongClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Toast.makeText(MainActivity.this, currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
            getDatabase(myRef.child("users").child(currentUser.getUid()).child("userdocs"));
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

    public void getDatabase(DatabaseReference myRef){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gridItems = new ArrayList<Docinfo>();
                if(dataSnapshot.getValue() == "null"){
                    loadGridItems();
                    return;
                }
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Docinfo d = postSnapshot.getValue(Docinfo.class);
                    gridItems.add(d);
                }
                loadGridItems();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        myRef.addValueEventListener(postListener);
    }

    public void loadGridItems() {
        adap = new GridItemsAdapter(this, R.layout.row_item, gridItems);
        gvdocs.setAdapter(adap);
        pb.setVisibility(View.INVISIBLE);
        if(gridItems.isEmpty()){
            alert2.setVisibility(View.VISIBLE);
            return;
        }else{
            alert2.setVisibility(View.INVISIBLE);
        }
    }


    public void addDoc(String type,String title){
        DatabaseReference newRef = myRef.child("users").child(currentUser.getUid()).child("userdocs").push();
        DatabaseReference Itemsofdocs = myRef.child("users").child(currentUser.getUid()).child("Itemsofdocs").child(newRef.getKey());
        Itemsofdocs.setValue("null");
        String timeObj = String.valueOf(LocalDateTime.now());
        Docinfo newdoc = new Docinfo(type,newRef.getKey(),timeObj,timeObj,title,currentUser.getUid());
        newRef.setValue(newdoc);
        openDocById(newdoc);
    }

    public void openDocById(Docinfo docItem){
        Intent displayListIntent = new Intent(this,list_display.class);
        displayListIntent.putExtra("owner",docItem.getOwner());
        displayListIntent.putExtra("id",docItem.getId());
        startActivity(displayListIntent);
    }

    public void showBottomSheet(Docinfo d){
        bottomSheetDialog = new BottomSheetDialog(
                MainActivity.this,R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.layout_bottom_sheet_action,
                (LinearLayout)findViewById(R.id.bottomsheetaction)
        );
        docItemSelected = d;
       TextView actiontitle = bottomSheetView.findViewById(R.id.actiontitle);
        actiontitle.setText(docItemSelected.getId());
        btndelete = bottomSheetView.findViewById(R.id.btndelete);
        btndelete.setOnClickListener(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();
    }

    @Override
    public void onClick(View v) {
        if(v == addBtn){
            Toast.makeText(this,"addBtn pressed",Toast.LENGTH_LONG).show();
            addDoc("targetList","הרשימה החדשה שלי");
        }
        if(v == btndelete){
            gridItems.remove(docItemSelected);
            if(gridItems.isEmpty()){
                myRef.child("users").child(currentUser.getUid()).child("userdocs").setValue("null");
                myRef.child("users").child(currentUser.getUid()).child("Itemsofdocs").setValue("null");
            }else {
                myRef.child("users").child(currentUser.getUid()).child("userdocs")
                        .child(docItemSelected.getId()).removeValue();
                myRef.child("users").child(currentUser.getUid()).child("Itemsofdocs").child(docItemSelected.getId()).removeValue();
            }
            loadGridItems();
           bottomSheetDialog.dismiss();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       docItem = gridItems.get(position);
       openDocById(docItem);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showBottomSheet(gridItems.get(position));
        return true;
    }
}