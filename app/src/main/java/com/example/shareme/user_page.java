package com.example.shareme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

public class user_page extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TextView username,useremail;
    ImageView userImage;
    LinearLayout btnedit,btnlogout;
    Dialog editDialog;
    EditText listName;
    Button btnfinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        username = findViewById(R.id.username);
        useremail = findViewById(R.id.useremail);
        userImage = findViewById(R.id.userImage);
        btnlogout = findViewById(R.id.btnlogout);
        btnedit = findViewById(R.id.btnedit);
        btnedit.setOnClickListener(this);
        btnlogout.setOnClickListener(this);
        username.setText(currentUser.getDisplayName());
        useremail.setText(currentUser.getEmail());
        Picasso.get().load(currentUser.getPhotoUrl()).into(userImage);
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
        listName.setText(currentUser.getDisplayName());
        editDialog.show();
    }

    public void userLogout(){
        mAuth.signOut();
    }

    @Override
    public void onClick(View v) {
        if(v == btnedit){
            createEditDialog();
        }
        if (v == btnlogout){
            userLogout();
            Intent intent = new Intent(this, signup_page.class);
            startActivity(intent);
        }
        if(v == btnfinish){
            String namestr = listName.getText().toString();
            if(namestr.length() == 0){
                listName.setError("הכנס שם");
                listName.requestFocus();
                return;
            }

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(namestr)
                    .build();

            currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                username.setText(currentUser.getDisplayName());
                                editDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "השם עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}