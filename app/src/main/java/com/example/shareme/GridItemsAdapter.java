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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        String title = String.valueOf(getItem(position).getTitle());
        String datetime = getItem(position).getLastUpdate();
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
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView titleDoc = convertView.findViewById(R.id.titleDoc);
        TextView dateDoc = convertView.findViewById(R.id.dateDoc);
        titleDoc.setText(title);
        dateDoc.setText(datetime);
        return convertView;
    }

}
