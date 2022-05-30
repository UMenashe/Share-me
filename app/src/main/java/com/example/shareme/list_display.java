package com.example.shareme;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarMenu;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class list_display extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, Toolbar.OnMenuItemClickListener {
MaterialToolbar topAppBar;
FirebaseDatabase database;
DatabaseReference myRef;
FirebaseAuth mAuth;
FirebaseUser currentUser;
String id ,owner;
Dialog addialog;
FloatingActionButton addbtn;
TextView alertTv;
ListView lv;
ListItemTarget lit;
private ArrayList<ListItemTarget> listItems;
private ItemsListAdapter adap;
BottomSheetDialog bottomSheetDialog,bottomSheetDetails;
Button btnadditem;
TextInputEditText itemname,countarget,itemnamedetails,countdetails;
TextInputLayout layoutname,layoutcount,layoutnamedet,layoutcountdet;
Dialog shareDialog;
EditText useremail;
Button btnshare,btnsave,btncancel;
Docinfo di;
LinearLayout btnpic;
Bitmap bitmap;
ImageView imgV;



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
        topAppBar.setOnMenuItemClickListener(this);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainpage = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mainpage);
            }
        });
        listItems = new ArrayList<ListItemTarget>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Intent intent=getIntent();
       owner = intent.getExtras().getString("owner");
       id = intent.getExtras().getString("id");
        DatabaseReference itemRef =  myRef.child("users").child(owner).child("userdocs").child(id);
        DatabaseReference Itemsofdocs = myRef.child("Itemsofdocs").child(id);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                di = dataSnapshot.getValue(Docinfo.class);
                loadInfo(di);
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
        DatabaseReference itemRef = myRef.child("Itemsofdocs").child(id).push();
        Map<String, Integer> m = new HashMap<>();
        m.put(owner,0);
        ListItemTarget li = new ListItemTarget(name,Integer.parseInt(targetCount),itemRef.getKey(),m);
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

    public void show_Details_Sheet(){
        bottomSheetDetails = new BottomSheetDialog(
                list_display.this,R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.list_item_details,
                (LinearLayout)findViewById(R.id.bottomsheetdetails)
        );

        itemnamedetails = bottomSheetView.findViewById(R.id.itemnamedetails);
        countdetails = bottomSheetView.findViewById(R.id.countdetails);
        btnpic = bottomSheetView.findViewById(R.id.btnpic);
        layoutnamedet = bottomSheetView.findViewById(R.id.layoutnamedet);
        layoutcountdet = bottomSheetView.findViewById(R.id.layoutcountdet);
        btncancel = bottomSheetView.findViewById(R.id.btncancel);
        btnsave = bottomSheetView.findViewById(R.id.btnsave);
        imgV = bottomSheetView.findViewById(R.id.imgv);
        btncancel.setOnClickListener(this);
        btnsave.setOnClickListener(this);
        btnpic.setOnClickListener(this);
        lit = listItems.get(0);
        itemnamedetails.setText(lit.getName());
        countdetails.setText(String.valueOf(lit.getTargetCount()));
        bottomSheetDetails.setContentView(bottomSheetView);
        bottomSheetDetails.setCancelable(true);
        bottomSheetDetails.setCanceledOnTouchOutside(true);
        bottomSheetDetails.show();
    }

    public void createShareDialog()
    {
        shareDialog= new Dialog(this);
        shareDialog.setContentView(R.layout.share_dialog);
        Window window = shareDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        shareDialog.setCancelable(true);
        useremail = shareDialog.findViewById(R.id.emailuser);
        btnshare = shareDialog.findViewById(R.id.btnshare);
        btnshare.setOnClickListener(this);
        shareDialog.show();
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==0)

        {
            if(resultCode==RESULT_OK)

            {
                bitmap= (Bitmap) data.getExtras().get("data");
                imgV.setImageBitmap(bitmap);

            }
        }
    }



    public void loadInfo(Docinfo docinfo){
        topAppBar.setTitle(docinfo.getTitle());
        topAppBar.setSubtitle(docinfo.getLastUpdate());
    }
    @Override
    public void onClick(View v) {
      if(v == addbtn){
          show_Details_Sheet();
      }
      if(v == btncancel){
          bottomSheetDetails.dismiss();
      }

      if(v == btnpic){
          Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          startActivityForResult(intent,0);
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

      if(v == btnshare){
          String emailstr = useremail.getText().toString();
          if(useremail.length() == 0){
              useremail.setError("הכנס אימייל");
              useremail.requestFocus();
              return;
          }
          emailstr = emailstr.split("@")[0];
          DatabaseReference itemRef =  myRef.child("usersuid").child(emailstr);
          itemRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<DataSnapshot> task) {
                  if (!task.getResult().exists()) {
                      useremail.setError("המשתמש לא נמצא");
                      useremail.requestFocus();
                  }
                  else {
                      DatabaseReference userRef =  myRef.child("users").child((String) task.getResult().getValue()).child("userdocs").child(id);
                      userRef.setValue(di);
                      Toast.makeText(getApplicationContext(),"share success",Toast.LENGTH_LONG).show();
                  }
              }
          });
      }

    }

    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ListItemTarget p = mutableData.getValue(ListItemTarget.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                p.addCount();
                Map<String, Integer> m1 = p.getCountPerUser();
                m1.put(currentUser.getUid(), m1.get(currentUser.getUid()) + 1);
                p.setCountPerUser(m1);
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long viewId = view.getId();
        Toast.makeText(this,"test0", Toast.LENGTH_SHORT).show();
        if (viewId == R.id.btnplus) {
          lit = listItems.get(position);
            DatabaseReference item= myRef.child("Itemsofdocs").child(this.id).child(lit.getId());
            onStarClicked(item);
            loadItems();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.share){
            createShareDialog();
        }
        return false;
    }

}