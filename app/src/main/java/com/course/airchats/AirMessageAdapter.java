package com.course.airchats;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AirMessageAdapter extends ArrayAdapter<AirChatMessage> {

    List<AirChatMessage> messages;
    private Activity activity;

    public AirMessageAdapter(Activity context, int resource, List<AirChatMessage> messages) {
        super(context, resource, messages);
        this.messages = messages;
        this.activity = context;
    }

    //ConvertView - it is view of element - AirchatMessage
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater)
                activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        //code bellow implements switching between your_message_item and my_message_item
        AirChatMessage airChatMessage = getItem(position);
        int layoutResources = 0;
        int viewType = getItemViewType(position);

        if (viewType == 0) {
            layoutResources = R.layout.my_message_item;
        } else {
            layoutResources = R.layout.your_message_item;
        }

        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(layoutResources, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        //if getImageUrl == null,then isText is text
        boolean isText = airChatMessage.getImageUrl() == null;

        //if user is going to send a message
        if (isText) {
            viewHolder.messageTextView.setVisibility(View.VISIBLE);
            viewHolder.photoImageView.setVisibility(View.GONE);
            viewHolder.messageTextView.setText(airChatMessage.getText());
            //if user is going to send an image
        } else {
            viewHolder.messageTextView.setVisibility(View.GONE);
            viewHolder.photoImageView.setVisibility(View.VISIBLE);
            Glide.with(viewHolder.photoImageView.getContext())
                    .load(airChatMessage.getImageUrl())
                    .into(viewHolder.photoImageView);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {

        int flag;
        AirChatMessage airChatMessage = messages.get(position);
        if (airChatMessage.isMine()) {
            flag = 0;
        } else {
            flag = 1;
        }
        return flag;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class ViewHolder {

        private TextView messageTextView;
        private ImageView photoImageView;

        public ViewHolder(View view) {
            photoImageView = view.findViewById(R.id.photoImageView);
            messageTextView = view.findViewById(R.id.messageTextView);
        }
    }
}
