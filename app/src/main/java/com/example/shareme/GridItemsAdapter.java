package com.example.shareme;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class GridItemsAdapter extends ArrayAdapter<Docinfo> {
    private static final String TAG = "GridItemsAdapter";
    private Context mContext;
    int mResource;

    public GridItemsAdapter(Context context, int resource, ArrayList<Docinfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    public static class ViewHolder {
        TextView titleDoc;
        TextView dateDoc;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.row_item, parent, false);

            holder = new ViewHolder();
            holder.titleDoc = (TextView) convertView.findViewById(R.id.titleDoc);
            holder.dateDoc = (TextView) convertView.findViewById(R.id.dateDoc);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        Docinfo currentDoc = getItem(position);
        holder.titleDoc.setText(currentDoc.getTitle());
        String datetime = currentDoc.getCreateTime();
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
        holder.dateDoc.setText(datetime);
        return convertView;
    }

}
