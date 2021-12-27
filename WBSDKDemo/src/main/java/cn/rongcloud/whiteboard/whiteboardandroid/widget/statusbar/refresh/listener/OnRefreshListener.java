package cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.listener;

import androidx.annotation.NonNull;

import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.api.RefreshLayout;

/**
 * 刷新监听器
 * Created by scwang on 2017/5/26.
 */
public interface OnRefreshListener {
    void onRefresh(@NonNull RefreshLayout refreshLayout);
}
