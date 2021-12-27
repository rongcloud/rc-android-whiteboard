package cn.rongcloud.whiteboard.whiteboardandroid.view.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.rongcloud.whiteboard.whiteboardandroid.R;
import cn.rongcloud.whiteboard.whiteboardandroid.model.ItemVideoInfo;

/**
 * Created by yanke on 2021/8/1
 */
public class VideoRecordAdapter extends BaseAdapter {

    private static final String TAG = VideoRecordAdapter.class.getCanonicalName();
    private List<ItemVideoInfo> videoRecords;
    private Context context;

    public VideoRecordAdapter(Context context, List<ItemVideoInfo> list) {
        videoRecords = list == null ? new ArrayList<>() : list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return videoRecords.size();
    }

    @Override
    public Object getItem(int position) {
        return videoRecords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_video_record, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTxRoomNumber = convertView.findViewById(R.id.tx_room_number);
            viewHolder.mTxVideoRecordStartTime = convertView.findViewById(R.id.tx_video_record_start_time);
            viewHolder.mTxVideoRecordDuration = convertView.findViewById(R.id.tx_video_record_duration);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ItemVideoInfo itemVideoInfo = videoRecords.get(position);

        viewHolder.mTxRoomNumber.setText(itemVideoInfo.getHubId());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        viewHolder.mTxVideoRecordStartTime.setText(sdf.format(new Date(itemVideoInfo.getRecordedTime())));

        long milliseconds = Long.parseLong(itemVideoInfo.getDuration());
//        long totalSeconds = Double.valueOf(Math.ceil((double) milliseconds / 1000)).longValue();
//        long minutes = Double.valueOf(Math.floor((double) totalSeconds / 60)).longValue();
//        long seconds = totalSeconds % 60;
        viewHolder.mTxVideoRecordDuration.setText(stringForTime(milliseconds));
        return convertView;
    }

    @SuppressLint("DefaultLocale")
    public String stringForTime(long timeMs) {
        long totalSeconds = Double.valueOf(Math.ceil((double) timeMs / 1000)).longValue();
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
    }

    public void update(List<ItemVideoInfo> list) {
        if (list != null && !list.isEmpty()) {
            this.videoRecords.clear();
            this.videoRecords.addAll(list);
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder {
        TextView mTxRoomNumber;
        TextView mTxVideoRecordStartTime;
        TextView mTxVideoRecordDuration;
    }
}
