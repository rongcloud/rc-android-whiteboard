package cn.rongcloud.whiteboard.whiteboardandroid.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.rongcloud.whiteboard.whiteboardandroid.R;
import cn.rongcloud.whiteboard.whiteboardandroid.model.ItemVideoInfo;
import cn.rongcloud.whiteboard.whiteboardandroid.network.RequestCallback;
import cn.rongcloud.whiteboard.whiteboardandroid.network.request.GetVideoListRequest;
import cn.rongcloud.whiteboard.whiteboardandroid.network.request.GetVideoListRequestData;
import cn.rongcloud.whiteboard.whiteboardandroid.network.request.RemoveVideoRequest;
import cn.rongcloud.whiteboard.whiteboardandroid.network.request.RemoveVideoRequestData;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.GetVideoListResponseData;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.ResponseData;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.ToastUtils;
import cn.rongcloud.whiteboard.whiteboardandroid.view.adapter.VideoRecordAdapter;
import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.SmartRefreshLayout;
import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.api.RefreshLayout;
import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.footer.ClassicsFooter;
import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.header.ClassicsHeader;
import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.listener.OnLoadMoreListener;
import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.refresh.listener.OnRefreshListener;

public class VideoListActivity extends BaseActivity {

    final static long DAY_TIME_SECOND = 60 * 60 * 24;

    private ImageView mReturnIv;
    private ListView mLvVideoRecord;
    private GetVideoListRequestData requestData = new GetVideoListRequestData();
    private SmartRefreshLayout mRefreshLayout;
    private ListView mLvVideoList;
    private SmartRefreshLayout refreshLayout;
    private int nextPage;
    private List<ItemVideoInfo> list;
    private VideoRecordAdapter adapter;

    public static void start(Context context) {
        Intent intent = new Intent(context, VideoListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);
        initView();
    }

    private void initView() {
        mReturnIv = (ImageView) findViewById(R.id.return_videos_iv);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mLvVideoList = (ListView) findViewById(R.id.lv_video_list);

        mReturnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list = new ArrayList<>();
        adapter = new VideoRecordAdapter(this, null);
        mLvVideoList.setAdapter(adapter);

        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                requestData = new GetVideoListRequestData();
                requestData.offset = 0;
                requestData.reTime = System.currentTimeMillis() / 1000;
                requestData.rsTime = requestData.reTime - DAY_TIME_SECOND;
                nextPage = 0;

                requestVideoList(requestData, 0);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                requestData = new GetVideoListRequestData();
                requestData.reTime = System.currentTimeMillis() / 1000;
                requestData.rsTime = requestData.reTime - DAY_TIME_SECOND;
                requestData.offset = nextPage + 1;

                requestVideoList(requestData, 1);
            }
        });

        mLvVideoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Dialog confirmDialog = new AlertDialog.Builder(VideoListActivity.this)
                        .setMessage("确定删除此录像吗?")
                        .setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                showLoadingDialog();
                                ItemVideoInfo item = list.get(position);
                                requestRemoveVideo(item, position);
                            }
                        }).create();
                confirmDialog.show();
                return true;
            }
        });

        mLvVideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemVideoInfo itemVideoInfo = list.get(position);
                VideoRecordPlayWebActivity.start(VideoListActivity.this, itemVideoInfo.getUrl(), itemVideoInfo.getHubId());
            }
        });

        refreshLayout.autoRefresh();
    }

    /**
     * 删除录像接口
     *
     * @param item
     * @param position
     */
    private void requestRemoveVideo(ItemVideoInfo item, int position) {
        RemoveVideoRequestData data = new RemoveVideoRequestData();
        data.id = item.getId();
        RemoveVideoRequest.create().params(data).request(new RequestCallback() {
            @Override
            public void onResponse(ResponseData responseData) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        if (responseData.code == 0) {
                            list.remove(position);
                            adapter.update(list);
                            ToastUtils.showToast("删除成功");
                        } else {
                            ToastUtils.showToast("删除失败");
                        }
                    }
                });
            }
        });
    }

    /**
     * @param data
     * @param type 0标识刷新，1标识上拉加载更多
     */
    private void requestVideoList(GetVideoListRequestData data, int type) {
        GetVideoListRequest.create().params(data).request(new RequestCallback() {
            @Override
            public void onResponse(ResponseData responseData) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadMore();
                        if (responseData.code == 10000 && responseData instanceof GetVideoListResponseData) {
                            GetVideoListResponseData data = (GetVideoListResponseData) responseData;
                            List<ItemVideoInfo> videoInfoList = data.getVideoInfoList();
                            if (videoInfoList != null && !videoInfoList.isEmpty()) {
                                if (type == 0) {
                                    list.clear();
                                }
                                list.addAll(videoInfoList);
                                adapter.update(list);
                            }
                        } else {
                            ToastUtils.showToast(responseData.errorMessage);
                        }
                    }
                });
            }
        });
    }
}