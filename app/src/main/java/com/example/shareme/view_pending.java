package com.example.shareme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class view_pending extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private ArrayList<Docinfo> listItems;
    private PendingAdapter adap;
    ListView listPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pending);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        getPending();
    }

    public void loadPendingItems() {
        adap = new PendingAdapter(this, R.layout.list_pending_item, listItems);
        listPending.setAdapter(adap);
//        pb.setVisibility(View.INVISIBLE);
//        if(gridItems.isEmpty()){
//            alert2.setVisibility(View.VISIBLE);
//            return;
//        }else{
//            alert2.setVisibility(View.INVISIBLE);
//        }
    }

    public void getPending(){
        DatabaseReference pendingRef =  myRef.child("users").child(currentUser.getUid()).child("pending");
        pendingRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()) {
                    for (DataSnapshot postSnapshot: task.getResult().getChildren()) {
                        Docinfo d = postSnapshot.getValue(Docinfo.class);
                        listItems.add(d);
                    }
                    loadPendingItems();
                }
                else {
                    Toast.makeText(getApplicationContext(),"you dont have pending", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}