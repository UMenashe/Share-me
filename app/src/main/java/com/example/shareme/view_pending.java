package com.example.shareme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class view_pending extends AppCompatActivity implements AdapterView.OnItemClickListener {

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    static ArrayList<Docinfo> listItems;
    private PendingAdapter adap;
    ListView listPending;
    Docinfo di;
    DatabaseReference pendingRef;
    TextView alert2;
    ProgressBar pb;
    static Map<String, String> namesmap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pending);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        pendingRef =  myRef.child("users").child(currentUser.getUid());
        listItems = new ArrayList<>();
        listPending = findViewById(R.id.lvpending);
        alert2 = findViewById(R.id.alert2);
        alert2.setVisibility(View.INVISIBLE);
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        listPending.setOnItemClickListener(this);
        this.setTitle("רשימת בקשות");
        loadNamesList();
        getPending();
    }

    public void loadPendingItems() {
        adap = new PendingAdapter(this, R.layout.list_pending_item, listItems,namesmap);
        listPending.setAdapter(adap);
        pb.setVisibility(View.INVISIBLE);
        if(listItems.isEmpty())
            alert2.setVisibility(View.VISIBLE);
        else
            alert2.setVisibility(View.INVISIBLE);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long viewId = view.getId();
        di = listItems.get(position);
        if (viewId == R.id.btnconfirm){
            pendingRef.child("pending").child(di.getId()).removeValue();
            pendingRef.child("userdocs").child(di.getId()).setValue(di);
        }

        if(viewId == R.id.btnreject){
            pendingRef.child("pending").child(di.getId()).removeValue();
            myRef.child("allDocs").child(di.getId()).child("participants").child(currentUser.getUid()).removeValue();
        }
        listItems.remove(position);
        loadPendingItems();
    }

    public void getPending(){
        pendingRef.child("pending").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()) {
                    for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                        Docinfo d = userSnapshot.getValue(Docinfo.class);
                        listItems.add(d);
                    }
                    loadPendingItems();
                }
                else {
                    pb.setVisibility(View.INVISIBLE);
                    alert2.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}