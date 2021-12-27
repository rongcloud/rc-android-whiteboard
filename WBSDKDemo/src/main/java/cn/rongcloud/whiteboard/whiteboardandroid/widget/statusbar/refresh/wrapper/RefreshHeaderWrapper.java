package cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.wrapper;

import android.annotation.SuppressLint;
import android.view.View;

import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.api.RefreshHeader;
import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.simple.SimpleComponent;


/**
 * 刷新头部包装
 * Created by scwang on 2017/5/26.
 */
@SuppressLint("ViewConstructor")
public class RefreshHeaderWrapper extends SimpleComponent implements RefreshHeader {

    public RefreshHeaderWrapper(View wrapper) {
        super(wrapper);
    }

}
