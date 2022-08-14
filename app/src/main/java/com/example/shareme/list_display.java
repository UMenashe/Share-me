package com.example.shareme;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class list_display extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, Toolbar.OnMenuItemClickListener{
MaterialToolbar topAppBar,topAppBarDialog;
FirebaseDatabase database;
DatabaseReference myRef;
FirebaseAuth mAuth;
FirebaseUser currentUser;
String id ,owner;
Dialog addialog;
FloatingActionButton addbtn;
TextView alertTv,extxt;
ListView lv;
ListView lvusers;
ListItemTarget lit;
private ArrayList<ListItemTarget> listItems;
private ArrayList<Participant> listParticipant;
private ItemsListAdapter adap;
private ItemsParticipantsAdp adapParticipant;
BottomSheetDialog bottomSheetDialog,bottomSheetDetails;
Button btnadditem;
TextInputEditText itemname,countarget,itemnamedetails,countdetails;
TextInputLayout layoutname,layoutcount,layoutnamedet,layoutcountdet;
Dialog shareDialog,imgDialog;
EditText useremail;
Button btnshare,btnsave,btncancel;
Docinfo di;
LinearLayout btnpic,btndeleteitem;
Bitmap bitmap,bmFromCloud;
ImageView imgV,imageIndialog;
CardView Cvimg;
FirebaseStorage storage;
StorageReference docSRef;
static Map<String, String> namesmap = new HashMap<>();
private final int PICK_IMAGE_REQUEST = 22;
private Uri filePath;
ProgressBar pb;



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
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        alertTv.setVisibility(View.INVISIBLE);
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
        storage = FirebaseStorage.getInstance();
        listItems = new ArrayList<ListItemTarget>();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Intent intent=getIntent();
       id = intent.getExtras().getString("id");
        DatabaseReference itemRef =  myRef.child("allDocs").child(id);
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

    public void lastUpdateDoc(){
        String timeObj = String.valueOf(LocalDateTime.now());
        myRef.child("allDocs").child(id).child("lastUpdate").setValue(timeObj);
    }

    private void loadItems() {
        adap = new ItemsListAdapter(this, R.layout.list_item, listItems);
        lv.setAdapter(adap);
        pb.setVisibility(View.GONE);
        if(listItems.isEmpty())
            alertTv.setVisibility(View.VISIBLE);
        else
            alertTv.setVisibility(View.GONE);
    }
    public void addItemTolist(String name,String targetCount){
        DatabaseReference itemRef = myRef.child("Itemsofdocs").child(id).push();
        Map<String, Integer> m = new HashMap<>();
        Map<String,String> p = di.getParticipants();
        for (Map.Entry<String,String> entry : p.entrySet())
            m.put(entry.getKey(),0);
        ListItemTarget li = new ListItemTarget(name,Integer.parseInt(targetCount),itemRef.getKey(),m);
        listItems.add(li);
        itemRef.setValue(li);
        lastUpdateDoc();
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
        Cvimg = bottomSheetView.findViewById(R.id.cvimg);
        extxt  = bottomSheetView.findViewById(R.id.extxt);
        lvusers = bottomSheetView.findViewById(R.id.lvusers);
        btndeleteitem = bottomSheetView.findViewById(R.id.btndeleteitem);
        btncancel.setOnClickListener(this);
        btnsave.setOnClickListener(this);
        btnpic.setOnClickListener(this);
        btndeleteitem.setOnClickListener(this);
        imgV.setOnClickListener(this);
        loadImages(docSRef.child(lit.getId() + "/pic.jpeg"));
        Map<String, Integer> m = lit.getCountPerUser();
        listParticipant = new ArrayList<>();
        Participant p1 = null;
        String rolestr = "";
        Map<String, Integer> m2 = buildNamesList(m);
        for (Map.Entry<String,Integer> entry : m2.entrySet()){
            rolestr =  namesmap.get(di.getOwner()) == entry.getKey() ? "מנהל":"עורך";
            p1 = new Participant(entry.getKey(),"",rolestr,entry.getValue());
            listParticipant.add(p1);
        }
        itemnamedetails.setText(lit.getName());
        countdetails.setText(String.valueOf(lit.getTargetCount()));
        adapParticipant = new ItemsParticipantsAdp(this, R.layout.users_item, listParticipant);
        lvusers.setAdapter(adapParticipant);
        bottomSheetDetails.setContentView(bottomSheetView);
        bottomSheetDetails.setCancelable(true);
        bottomSheetDetails.setCanceledOnTouchOutside(true);
        bottomSheetDetails.show();
    }

    public void loadNamesList(){
        DatabaseReference namesRef =  myRef.child("usernames");
        namesRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                     namesmap = (HashMap) task.getResult().getValue();
                }
            }
        });
    }

    public Map<String,Integer> buildNamesList(Map<String, Integer> listuid){
        Map<String, Integer> namesList = new HashMap<>();
        for (Map.Entry<String,Integer> entry : listuid.entrySet()){
            namesList.put(namesmap.get(entry.getKey()),entry.getValue());
        }
        return namesList;
    }


    public void loadImages(StorageReference itemRef){
        final long ONE_MEGABYTE = 1024 * 1024;
        try{
        itemRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bmFromCloud = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    final Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                            bmFromCloud,
                            (int) (bmFromCloud.getWidth() * 0.1),
                            (int) (bmFromCloud.getHeight() * 0.1),
                            true
                    );
                    imgV.setImageBitmap(scaledBitmap);
                extxt.setVisibility(View.GONE);
                Cvimg.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("imageFailure",exception.getMessage());
            }
        });
        } catch (Exception e){
        }
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

    public void createFullDialog(Bitmap bm)
    {
        imgDialog= new Dialog(this,android.R.style.ThemeOverlay_Material);
        imgDialog.setContentView(R.layout.imgdisplay_dialog);
        Window window = imgDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imgDialog.setCancelable(true);
        imageIndialog = imgDialog.findViewById(R.id.imageindialog);
        topAppBarDialog = imgDialog.findViewById(R.id.topAppBarDialog);
        topAppBarDialog.setTitle(lit.getName());
        topAppBarDialog.setOnMenuItemClickListener(this);
        topAppBarDialog.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgDialog.dismiss();
            }
        });

        imageIndialog.setImageBitmap(bm);
        imgDialog.show();
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(getContentResolver(), filePath);
                    final Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                            bitmap,
                            (int) (bitmap.getWidth() * 0.1),
                            (int) (bitmap.getHeight() * 0.1),
                            true
                    );
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                    imgV.setImageBitmap(rotatedBitmap);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 25 , bos);
                byte[] bitmapdata = bos.toByteArray();
                extxt.setVisibility(View.GONE);
                Cvimg.setVisibility(View.VISIBLE);
                StorageReference itemRef = docSRef.child(lit.getId() + "/pic.jpeg");
                UploadTask  uploadTask = itemRef.putBytes(bitmapdata);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                    }
                });
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    public String buildTxtCopy(){
        String txtcopy = di.getTitle() + "\n";
        for(int i = 0 ;i < listItems.size();i++){
            txtcopy += listItems.get(i).itemToCopyTxt(namesmap) + "\n \n";
        }
        return txtcopy;
    }



    public void loadInfo(Docinfo docinfo){
        if(docinfo == null){
            return;
        }
        String datetime = docinfo.getLastUpdate();
        String date = datetime.split("T")[0];
        LocalDate dateObj = LocalDate.parse(date);
        LocalDate dateObj2 = LocalDate.now();
        if(dateObj.isEqual(dateObj2)){
            LocalDateTime datetimeObj = LocalDateTime.parse(datetime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            datetime = datetimeObj.format(formatter);
        }else {
            datetime = date;
        }



        topAppBar.setTitle(docinfo.getTitle());
        topAppBar.setSubtitle("עודכן לאחרונה "+datetime);
        StorageReference storageRef = storage.getReference();
        docSRef = storageRef.child(di.getId());
        loadNamesList();
    }
    @Override
    public void onClick(View v) {
      if(v == addbtn){
          showBottomSheet();
      }
      if(v == btncancel){
          bottomSheetDetails.dismiss();
      }

      if(v == btnsave){
          String nameitemtext = itemnamedetails.getText().toString();
          String counttargettext = countdetails.getText().toString();
          if(nameitemtext.length() == 0){
              layoutnamedet.setError("הכנס שם");
              return;
          }
          if(counttargettext.length() == 0){
              layoutcountdet.setError("הכנס יעד");
              return;
          }

          if(Integer.parseInt(counttargettext) < lit.getTargetCount()){
              layoutcountdet.setError("לא ניתן להנמיך את היעד");
              return;
          }
          DatabaseReference item= myRef.child("Itemsofdocs").child(id).child(lit.getId());
          onDetailsChange(item,nameitemtext, Integer.parseInt(counttargettext));
          bottomSheetDetails.dismiss();
      }

      if(v == btnpic){
          Intent intent = new Intent();
          intent.setType("image/*");
          intent.setAction(Intent.ACTION_GET_CONTENT);
          startActivityForResult(
                  Intent.createChooser(
                          intent,
                          "Select Image from here..."), PICK_IMAGE_REQUEST);
      }

      if(v == imgV){
          Bitmap bitm = bitmap != null ? bitmap : bmFromCloud;
          createFullDialog(bitm);
      }

      if(v == btndeleteitem){
          DatabaseReference item= myRef.child("Itemsofdocs").child(id).child(lit.getId());
          listItems.remove(lit);
          item.removeValue();
          lastUpdateDoc();
          bottomSheetDetails.dismiss();
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
          String finalEmailstr = emailstr;
          itemRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<DataSnapshot> task) {
                  if (!task.getResult().exists()) {
                      useremail.setError("המשתמש לא נמצא");
                      useremail.requestFocus();
                  }
                  else {
                      String struser = (String) task.getResult().getValue();
                      DatabaseReference userRef =  myRef.child("users").child(struser).child("pending").child(id);
                      DatabaseReference allDRef =  myRef.child("allDocs").child(id).child("participants").child(struser);
                      userRef.setValue(di);
                      allDRef.setValue("editor");
                      Toast.makeText(getApplicationContext(),"share success",Toast.LENGTH_LONG).show();
                  }
              }
          });
      }

    }

    private void onStarClicked(DatabaseReference postRef,String type) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ListItemTarget p = mutableData.getValue(ListItemTarget.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                Map<String, Integer> m1 = p.getCountPerUser();

                if(m1.get(currentUser.getUid()) == null){
                    m1.put(currentUser.getUid(),0);
                    p.setCountPerUser(m1);
                }

                if(type.equals("plus") && !p.isTargetComplete()){
                    p.addCount();
                    m1.put(currentUser.getUid(), m1.get(currentUser.getUid())  + 1);
                }
                if(type.equals("minus") && !p.checkUserCount(currentUser.getUid())){
                    p.subtractCount();
                    m1.put(currentUser.getUid(), m1.get(currentUser.getUid()) - 1);
                }

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

    private void onDetailsChange(DatabaseReference postRef,String name, int count) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ListItemTarget p = mutableData.getValue(ListItemTarget.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }
                p.setName(name);
                p.setTargetCount(count);
                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Toast.makeText(getApplicationContext(), "השינויים נשמרו", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void shareText(String txt){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, txt);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void shareImage(){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmFromCloud.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpeg");
        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri imageUri = FileProvider.getUriForFile(
                getApplicationContext(),
                BuildConfig.APPLICATION_ID+".provider",f);
        share.putExtra(Intent.EXTRA_STREAM,imageUri);
        Intent chooser = Intent.createChooser(share, "Share File");
        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        startActivity(chooser);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long viewId = view.getId();
        lit = listItems.get(position);
        if (viewId == R.id.btnplus) {
            DatabaseReference item= myRef.child("Itemsofdocs").child(this.id).child(lit.getId());
            onStarClicked(item,"plus");
            loadItems();
        }

        if(viewId == R.id.btnminus){
            DatabaseReference item= myRef.child("Itemsofdocs").child(this.id).child(lit.getId());
            onStarClicked(item,"minus");
            loadItems();
        }

        if(viewId == R.id.btninfo){
            show_Details_Sheet();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.share){
            createShareDialog();
        }

        if(item.getItemId() == R.id.send_text){
            shareText(buildTxtCopy());
        }

        if(item.getItemId() == R.id.shareimg){
            Toast.makeText(getApplicationContext(),"share img", Toast.LENGTH_SHORT).show();
            shareImage();
        }

        if(item.getItemId() == R.id.deleteimg){
            docSRef.child(lit.getId() + "/pic.jpeg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"המחיקה הושלמה", Toast.LENGTH_SHORT).show();
                    Cvimg.setVisibility(View.INVISIBLE);
                    extxt.setVisibility(View.VISIBLE);
                    imgDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(),"אופס, המחיקה נכשלה", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return false;
    }
}