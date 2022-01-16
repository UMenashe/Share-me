package com.example.shareme;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class GridItemsAdapter extends ArrayAdapter<Docinfo> {
    private static final String TAG = "GridItemsAdapter";
    private Context mContext;
    int mResource;

    public GridItemsAdapter(Context context, int resource, ArrayList<Docinfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String title = String.valueOf(getItem(position).getId());

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView titleDoc = convertView.findViewById(R.id.titleDoc);
        titleDoc.setText(title);
        return convertView;
    }

}
