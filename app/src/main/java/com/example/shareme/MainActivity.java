package com.example.shareme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    ImageView im;
    FirebaseAuth mAuth;
    FloatingActionButton addBtn;
    GridView gvdocs;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser currentUser;
    TextView alert2,cart_badge;
    private ArrayList<Docinfo> gridItems;
    private GridItemsAdapter adap;
    Docinfo docItem;
    Docinfo docItemSelected;
    ProgressBar pb;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout btndelete, btnedit;
    CardView viewprofile;
    InternetBroadCast internetbroadcast;
    Dialog editDialog;
    EditText listName,editsearch;
    Button btnfinish;
    FrameLayout mainlayout;
    static int count;
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
        viewprofile = findViewById(R.id.viewprofile);
        mainlayout = findViewById(R.id.mainlayout);
        cart_badge = findViewById(R.id.cart_badge);
        alert2.setVisibility(View.INVISIBLE);
        cart_badge.setVisibility(View.GONE);
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        viewprofile.setOnClickListener(this);
        gvdocs.setOnItemClickListener(this);
        gvdocs.setOnItemLongClickListener(this);
        editsearch = findViewById(R.id.editsearch);
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                ArrayList<Docinfo> itemssearch = new ArrayList<>();
                for(int i = 0; i< gridItems.size();i++){
                    if(gridItems.get(i).getTitle().contains(s)){
                        itemssearch.add(gridItems.get(i));
                    }
                }
                adap = new GridItemsAdapter(getApplicationContext(), R.layout.row_item, itemssearch);
                gvdocs.setAdapter(adap);
            }
        });
        internetbroadcast = new InternetBroadCast();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
            String email = currentUser.getEmail().split("@")[0];
            myRef.child("usersuid").child(email).setValue(currentUser.getUid());
            myRef.child("usernames").child(currentUser.getUid()).setValue(currentUser.getDisplayName());
            Picasso.get().load(currentUser.getPhotoUrl()).into(im);
            getDatabase(myRef.child("users").child(currentUser.getUid()).child("userdocs"));
            getPending();
        }else{
            showSignUpage();
        }

//        testIntent();
    }
    public void showSignUpage(){
        Intent signUppage = new Intent(this,signup_page.class);
        startActivity(signUppage);
    }

    public void getDatabase(DatabaseReference docsuserRef){
        docsuserRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                count = 0;
                gridItems = new ArrayList<Docinfo>();
                final int target = (int) snapshot.getChildrenCount();
                    for (DataSnapshot docKey: snapshot.getChildren()) {
                        myRef.child("allDocs").child(docKey.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                Docinfo d = task.getResult().getValue(Docinfo.class);
                                gridItems.add(d);
                                checkComplete(target);
                            }
                        });
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private synchronized void checkComplete(int target) {
        count++;
        if (count == target) {
            loadGridItems();
            return;
        }
    }

    public void getPending(){
        DatabaseReference pendingRef =  myRef.child("users").child(currentUser.getUid()).child("pending");
        pendingRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()) {
                    cart_badge.setText(String.valueOf(task.getResult().getChildrenCount()));
                    cart_badge.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internetbroadcast,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(internetbroadcast);
    }

    public void loadGridItems() {
        Collections.sort(gridItems, new Comparator<Docinfo>() {
            @Override
            public int compare(Docinfo o1, Docinfo o2) {
                return o1.getCreateTime().compareTo(o2.getCreateTime());
            }
        });
        Collections.reverse(gridItems);
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
        DatabaseReference allDocsRef = myRef.child("allDocs").push();
        DatabaseReference userdocsRef = myRef.child("users").child(currentUser.getUid()).child("userdocs").child(allDocsRef.getKey());
        String timeObj = String.valueOf(LocalDateTime.now());
        Docinfo newdoc = new Docinfo(type,allDocsRef.getKey(),timeObj,timeObj,title,currentUser.getUid());
        allDocsRef.setValue(newdoc);
        userdocsRef.setValue(newdoc);
        openDocById(newdoc);
    }

    public void openDocById(Docinfo docItem){
        Intent displayListIntent = new Intent(this,list_display.class);
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
        actiontitle.setText(docItemSelected.getTitle());
        btndelete = bottomSheetView.findViewById(R.id.btndelete);
        btnedit = bottomSheetView.findViewById(R.id.btnedit);
        btnedit.setOnClickListener(this);
        btndelete.setOnClickListener(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();
    }

    public void createEditDialog()
    {
        editDialog= new Dialog(this);
        editDialog.setContentView(R.layout.edit_dialog);
        Window window = editDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        editDialog.setCancelable(true);
        listName = editDialog.findViewById(R.id.namelist);
        btnfinish = editDialog.findViewById(R.id.btnfinish);
        btnfinish.setOnClickListener(this);
        editDialog.show();
    }

    @Override
    public void onClick(View v) {
        if(v == addBtn){
            addDoc("targetList","רשימה חדשה");
        }
        if(v == btndelete){
            gridItems.remove(docItemSelected);
            if(gridItems.isEmpty()){
                myRef.child("allDocs").setValue("null");
                myRef.child("users").child(currentUser.getUid()).child("userdocs").setValue("null");
                myRef.child("Itemsofdocs").setValue("null");
            }else {
                myRef.child("users").child(currentUser.getUid()).child("userdocs")
                        .child(docItemSelected.getId()).removeValue();
                myRef.child("Itemsofdocs").child(docItemSelected.getId()).removeValue();
                myRef.child("allDocs").child(docItemSelected.getId()).removeValue();
            }
            loadGridItems();
           bottomSheetDialog.dismiss();
        }

        if(v == btnedit){
            bottomSheetDialog.dismiss();
            createEditDialog();
        }

        if(v == btnfinish){
            String namestr = listName.getText().toString();
            if(namestr.length() == 0){
                listName.setError("הכנס שם");
                listName.requestFocus();
                return;
            }
            DatabaseReference itemRef =  myRef.child("allDocs").child(docItemSelected.getId()).child("title");
            DatabaseReference itemRefuser =  myRef.child("users").child(currentUser.getUid())
                    .child("userdocs").child(docItemSelected.getId()).child("title");
            itemRef.setValue(namestr);
            itemRefuser.setValue(namestr);
            editDialog.dismiss();
        }
        if(v == viewprofile){
            Intent userpage = new Intent(this,user_page.class);
            startActivity(userpage);
        }
    }

    public void testIntent(){
        if(currentUser == null)
            return;

        int icon = R.drawable.userprofile;
        long when = System.currentTimeMillis();
        String title = currentUser.getDisplayName();
        String ticker = "ticker";
        String text= currentUser.getEmail();
        Intent intent = new Intent(MainActivity.this, user_page.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "M_CH_ID");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        Notification notification = builder.setContentIntent(pendingIntent)
                .setSmallIcon(icon).setTicker(ticker).setWhen(when)
                .setAutoCancel(true).setContentTitle(title)
                .setContentText(text).build();
        notificationManager.notify(1, notification);
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

    private class InternetBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("android.net.conn.CONNECTIVITY_CHANGE")){
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()){

                } else
                    Toast.makeText(context.getApplicationContext(), "אין חיבור לאינטרנט", Toast.LENGTH_SHORT).show();

            }

        }
    }
}