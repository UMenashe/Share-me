package com.example.shareme;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class ItemsParticipantsAdp extends ArrayAdapter<Participant> {
    private static final String TAG = "ItemsParticipantsAdp";
    private Context mContext;
    int mResource;

    public ItemsParticipantsAdp(Context context, int resource, ArrayList<Participant> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = String.valueOf(getItem(position).getName());
        String role = String.valueOf(getItem(position).getRole());
        String count = String.valueOf(getItem(position).getCount());

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView username = convertView.findViewById(R.id.username);
        TextView tvrole = convertView.findViewById(R.id.tvrole);
        ImageView userImage = convertView.findViewById(R.id.userImage);
        TextView tvcount = convertView.findViewById(R.id.tvnum);
        userImage.setBackground(mContext.getResources().getDrawable(R.drawable.userprofile));
        username.setText(name);
        tvrole.setText(role);
        tvcount.setText(count);
        return convertView;
    }

}
