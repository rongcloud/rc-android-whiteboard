package cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.listener;

import android.content.Context;

import androidx.annotation.NonNull;

import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.api.RefreshFooter;
import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.api.RefreshLayout;

/**
 * 默认Footer创建器
 * Created by scwang on 2018/1/26.
 */
public interface DefaultRefreshFooterCreator {
    @NonNull
    RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout);
}
