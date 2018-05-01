package com.example.awesomeness.designatedride._RiderActivities;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.awesomeness.designatedride.R;
import com.example.awesomeness.designatedride.util.HandleFileReadWrite;
import com.example.awesomeness.designatedride.util.MonthInterpreter;

import java.util.ArrayList;

public class RiderViewAppointmentItemDetail extends ArrayAdapter<String> {

    private ViewHolder viewHolder = new ViewHolder();

    public RiderViewAppointmentItemDetail(Context context, ArrayList<String> fileList) {
        super(context, R.layout.fragment_rider_appointmentview_item_detail, fileList);

    }

    static class ViewHolder {
        TextView circleTextView;
        TextView name;
        TextView location;
        TextView note;
        ImageView arrowImage;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_rider_appointmentview_item_detail, parent, false);

            viewHolder.circleTextView = (TextView) convertView.findViewById(R.id.imageView8);
            viewHolder.name = (TextView) convertView.findViewById(R.id.appointmentNameTextView);
            viewHolder.location = (TextView) convertView.findViewById(R.id.appointmentLocationTextView);
            viewHolder.note = (TextView) convertView.findViewById(R.id.apointmentNoteTextView);
            viewHolder.arrowImage = (ImageView) convertView.findViewById(R.id.imageView9);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String fileName = getItem(position);

        String name = "";
        String address = "";
        String location = "";
        String addresstwo = "";
        String time = "";
        String date = "";
        String status = "";
        String notes = "";
        String dateMonth;
        String dateDate;


        HandleFileReadWrite reader = new HandleFileReadWrite();
        reader.open(getContext(), fileName);
        if (reader.isExist()) {
            name = reader.readLine();
            location = reader.readLine();
            address = reader.readLine();
            addresstwo = reader.readLine();
            time = reader.readLine();
            date = reader.readLine();
            //status = reader.readLine();
            notes = reader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            while (notes != null) {
                stringBuilder.append(notes);
                stringBuilder.append("\n");
                notes = reader.readLine();
            }
            notes = stringBuilder.toString();
        }

        String token[] = date.split("-");
        dateDate = token[0];
        dateMonth = MonthInterpreter.shortName(token[1]);

        //
        viewHolder.circleTextView.setText(getContext().getResources().getString(R.string.appt_list_rider_date, dateMonth, dateDate));
        viewHolder.name.setText(name);
        viewHolder.location.setText(address);
        viewHolder.note.setText(notes);
        viewHolder.arrowImage.setImageResource(R.mipmap.rider_appointments_icon_arrow);

        return convertView;
    }


}
