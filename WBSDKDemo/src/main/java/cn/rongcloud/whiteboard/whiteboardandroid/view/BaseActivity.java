package cn.rongcloud.whiteboard.whiteboardandroid.view;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.LoadingDialog;

/**
 * Created by yanke on 2021/8/1
 */
public class BaseActivity extends AppCompatActivity {

    private LoadingDialog dialog;
    // 记录dialog 显示创建时间
    private long dialogCreateTime;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    /**
     * 显示加载 dialog
     *
     * @param msg
     */
    public void showLoadingDialog(String msg) {
        if (dialog == null || (dialog.getDialog() != null && !dialog.getDialog().isShowing())) {
            dialogCreateTime = System.currentTimeMillis();
            dialog = new LoadingDialog();
            dialog.setLoadingInformation(msg);
            dialog.show(getSupportFragmentManager(), "loading_dialog");
        }
    }


    public void showLoadingDialog() {
        if (dialog == null || (dialog.getDialog() != null && !dialog.getDialog().isShowing())) {
            dialogCreateTime = System.currentTimeMillis();
            dialog = new LoadingDialog();
            dialog.setLoadingInformation("");
            dialog.show(getSupportFragmentManager(), "loading_dialog");
        }
    }

    /**
     * 显示加载 dialog
     *
     * @param msgResId
     */
    public void showLoadingDialog(int msgResId) {
        showLoadingDialog(getString(msgResId));
    }

    /**
     * 取消加载dialog
     */
    public void dismissLoadingDialog() {
        dismissLoadingDialog(null);
    }

    /**
     * 取消加载dialog. 因为延迟， 所以要延时完成之后， 再在 runnable 中执行逻辑.
     * <p>
     * 延迟关闭时间是因为接口有时返回太快。
     */
    public void dismissLoadingDialog(Runnable runnable) {
        if (dialog != null && dialog.getDialog() != null && dialog.getDialog().isShowing()) {
            // 由于可能请求接口太快，则导致加载页面一闪问题， 所有再次做判断，
            // 如果时间太快（小于 500ms）， 则会延时 1s，再做关闭。
            if (System.currentTimeMillis() - dialogCreateTime < 500) {
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (runnable != null) {
                            runnable.run();
                        }
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    }
                }, 1000);

            } else {
                dialog.dismiss();
                dialog = null;
                if (runnable != null) {
                    runnable.run();
                }
            }
        }
    }
}
