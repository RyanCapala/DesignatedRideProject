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

public class RiderViewAppointmentItemDetail extends ArrayAdapter<String> {

    private ViewHolder viewHolder = new ViewHolder();

    public RiderViewAppointmentItemDetail(Context context, String[] fileList) {
        super(context, R.layout.fragment_rider_appointmentview_item_detail, fileList);

    }

    static class ViewHolder {
        ImageView circleImage;
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

            viewHolder.circleImage = (ImageView) convertView.findViewById(R.id.imageView8);
            viewHolder.name = (TextView) convertView.findViewById(R.id.appointmentNameTextView);
            viewHolder.location = (TextView) convertView.findViewById(R.id.appointmentLocationTextView);
            viewHolder.note = (TextView) convertView.findViewById(R.id.viewAppointmentRiderNotesText_tv);
            viewHolder.arrowImage = (ImageView) convertView.findViewById(R.id.imageView9);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //
        viewHolder.circleImage.setImageResource(R.drawable.appointment_date_circle);
        viewHolder.name.setText("TESTING");
        viewHolder.location.setText("HOSPITAL LOCATION");
        //viewHolder.note.setText("Some Notes");
        viewHolder.arrowImage.setImageResource(R.mipmap.rider_appointments_icon_arrow);

        return convertView;
    }


}
