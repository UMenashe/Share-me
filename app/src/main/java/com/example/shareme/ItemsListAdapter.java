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

public class ItemsListAdapter extends ArrayAdapter<ListItemTarget> {
    private static final String TAG = "ItemsListAdapter";
    private Context mContext;
    int mResource;
    Button btnplus,btnminus,btninfo;

    public ItemsListAdapter(Context context, int resource, ArrayList<ListItemTarget> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = String.valueOf(getItem(position).getName());
        String targetCount = String.valueOf(getItem(position).getTargetCount());
        String currentCount = String.valueOf(getItem(position).getCurrentCount());
        boolean isComplete =getItem(position).isTargetComplete();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView textname = convertView.findViewById(R.id.textname);
        TextView textcount = convertView.findViewById(R.id.textcount);
        btnplus = convertView.findViewById(R.id.btnplus);
        btnminus = convertView.findViewById(R.id.btnminus);
        btninfo = convertView.findViewById(R.id.btninfo);
        setClickListener(btninfo,position,parent);
        setClickListener(btnminus,position,parent);
        setClickListener(btnplus,position,parent);

        if(isComplete)
            convertView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_item_complete));
        else
            convertView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_item));

        textname.setText(name);
        textcount.setText(targetCount +"/" +currentCount);
        return convertView;
    }

    private void setClickListener(View view, final int position, final ViewGroup parent){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
            }
        });
    }

}
