package cn.rongcloud.whiteboard.whiteboardandroid.view;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import cn.rongcloud.whiteboard.sdk.IRCWBCallback;
import cn.rongcloud.whiteboard.sdk.RCWhiteBoard;
import cn.rongcloud.whiteboard.sdk.model.PermissionType;
import cn.rongcloud.whiteboard.sdk.model.Role;
import cn.rongcloud.whiteboard.sdk.widget.WBWebView;
import cn.rongcloud.whiteboard.sdk.model.IRCWBEnum;
import cn.rongcloud.whiteboard.sdk.model.RoomConfig;
import cn.rongcloud.whiteboard.sdk.dsbridge.DWebView;
import cn.rongcloud.whiteboard.whiteboardandroid.BuildConfig;
import cn.rongcloud.whiteboard.whiteboardandroid.R;
import cn.rongcloud.whiteboard.whiteboardandroid.model.UserInfo;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.ImageHandler;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.LocalCacheClient;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.MimeType;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.ToastUtils;
import cn.rongcloud.whiteboard.whiteboardandroid.widget.statusbar.AttachButton;

public class RoomActivity extends BaseFullScreenActivity {

    private static final String TAG = RoomActivity.class.getCanonicalName();
    private WBWebView mWebView;
    private static String mRoomId;
    private static Role mRole;
    private RCWhiteBoard client;
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private final static int REQUEST_CODE_STORAGE_PERMISSION = 100;
    private final static int REQUEST_CODE_STORAGE_PERMISSION_FILE = 1001;
    private ProgressBar progressBar;
    private Bitmap screenShotBitmap;
    private Uri imageUri;
    private String[] mimeTypeArray;
    private UserInfo userInfo;
    private AttachButton floatingActionButton;

    public static void start(Context context, String roomId, Role role) {
        mRoomId = roomId;
        mRole = role;
        Intent intent = new Intent(context, RoomActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Log.e(TAG, "onCreate");
        initView();
        joinRoom();
        initListener();
    }

    private void initView() {
        floatingActionButton = findViewById(R.id.quite_room);
        floatingActionButton.setImageBitmap(floatingActionButton.textAsBitmap("退出", 40, Color.WHITE));
        mWebView = (WBWebView) findViewById(R.id.web_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        DWebView.setWebContentsDebuggingEnabled(true);
        mWebView.setWebChromeClient(new FixWebChromeClient());
        mWebView.setWebViewClient(new FixWebViewClient());

        client = new RCWhiteBoard();
        client.init(this, mWebView);
    }

    /**
     * 加入房间
     */
    private void joinRoom() {

        userInfo = LocalCacheClient.getInstance().getUserInfo(this);

        RoomConfig param = RoomConfig.newBuilder(BuildConfig.APP_KEY,
                LocalCacheClient.getInstance().getUserInfo(this).getToken(),
                mRoomId,
                mRole,
                userInfo.getNickName(),
                userInfo.getUserId())
                .minutes(60 * 24)
                .build();

        client.joinRoom(param, new IRCWBCallback.ResultCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("成功加入房间");
                    }
                });
            }

            @Override
            public void onError(IRCWBEnum.ErrorCode errorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ToastUtils.showToast("加入房间失败");
                    }
                });
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }
        });
    }

    public void quitRoom(View view) {
        client.quitRoom(mRoomId, userInfo.getUserId(), new IRCWBCallback.ResultCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(IRCWBEnum.ErrorCode errorCode) {

            }
        });
    }

    private void initListener() {
        client.setPermissionChangeListener(new IRCWBCallback.PermissionChangeListener() {
            @Override
            public void onPermissionChange(PermissionType type) {
            }
        });

        client.setRecordStatusListener(new IRCWBCallback.RecordStatusListener() {
            @Override
            public void onRecordStart() {
            }

            @Override
            public void onRecordEnd(String url) {
            }
        });


        client.setRoomStatusListener(new IRCWBCallback.RoomStatusListener() {
            @Override
            public void onRoomQuit() {
                ToastUtils.showToast("退出房间");
                RoomActivity.this.finish();
            }

            @Override
            public void onRoomException(int code, String message) {
                new AlertDialog.Builder(RoomActivity.this)
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                RoomActivity.this.finish();
                            }
                        }).create().show();
            }

            @Override
            public void onCaptureEnd(Bitmap bitmap) {
                screenShotBitmap = bitmap;
                if (ContextCompat.checkSelfPermission(RoomActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有授权，编写申请权限代码
                    ActivityCompat.requestPermissions(RoomActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION);
                } else {
                    saveBitmapToAlbum(bitmap);
                }
            }
        });
    }

    private void saveBitmapToAlbum(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
//            ToastUtils.showToast("当前页面内容已保存至系统相册");
            return;
        }

        ImageHandler.getInstance().savePhotoAlbum(RoomActivity.this, bitmap);
        new AlertDialog.Builder(RoomActivity.this)
                .setTitle("提示")
                .setMessage("当前页面内容已保存至系统相册")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    class FixWebChromeClient extends WebChromeClient implements DWebView.FileChooser {
        @Override
        public Bitmap getDefaultVideoPoster() {
            try {
                int width = 100;
                int height = 50;
                // fix https://bugs.chromium.org/p/chromium/issues/detail?id=521753#c8
                return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            } catch (Exception e) {
                return super.getDefaultVideoPoster();
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            openFileChooserActivity(fileChooserParams.getAcceptTypes());
            return true;
        }

        @Override
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            uploadMessage = valueCallback;
            openFileChooserActivity(acceptType);
        }

        @Override
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            uploadMessage = valueCallback;
            openFileChooserActivity(acceptType);

        }
    }

    /**
     * 白板使用上传文件功能时需要打开文件浏览器
     * @param acceptTypes
     */
    private void openFileChooserActivity(String[] acceptTypes) {
        mimeTypeArray = acceptTypes;
        if (ContextCompat.checkSelfPermission(RoomActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(RoomActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE_PERMISSION_FILE);
            return;
        } else {
            openFileBrower(acceptTypes);
        }
    }

    private void openFileChooserActivity(String acceptType) {
        String[] mimeTypes = null;
        if (!TextUtils.isEmpty(acceptType)) {
            mimeTypes = acceptType.split(",");
        }
        openFileChooserActivity(mimeTypes);
    }

    private void openFileBrower(String[] mimeTypes) {
        // 指定拍照存储位置的方式调起相机
        String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
//        imageUri = Uri.fromFile(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName));
        File target = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, MimeType.IMAGE);
        values.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis() + ".jpg");

        //兼容Android Q和以下版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //RELATIVE_PATH是相对路径不是绝对路径
            //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        } else {
            String dstPath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES
                    + File.separator + target.getName();
            values.put(MediaStore.Images.Media.DATA, dstPath);
        }

        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = getContentResolver();

        imageUri = resolver.insert(external, values);

        //使用Intent.ACTION_GET_CONTENT选择文件
        Intent content = new Intent(Intent.ACTION_GET_CONTENT);
        content.addCategory(Intent.CATEGORY_OPENABLE);
        if (mimeTypes != null) {
            content.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        content.setType("*/*");
        //多选
        content.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        //拍照intent
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        Intent chooserIntent = Intent.createChooser(content, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

        startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (uploadMessage != null) {
                chooseBelow(resultCode, data);
            } else if (uploadMessageAboveL != null) {
                chooseAbove(resultCode, data);
            } else {
                Toast.makeText(this, "发生错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            //这里uploadMessage跟uploadMessageAboveL在不同系统版本下分别持有了
            //WebView对象，在用户取消文件选择器的情况下，需给onReceiveValue传null返回值
            //否则WebView在未收到返回值的情况下，无法进行任何操作，文件选择器会失效
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            } else if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(null);
                uploadMessageAboveL = null;
            }
        }
    }

    /**
     * Android API < 21(Android 5.0)版本的回调处理
     *
     * @param resultCode 选取文件或拍照的返回码
     * @param data       选取文件或拍照的返回结果
     */
    private void chooseBelow(int resultCode, Intent data) {
        Log.e(TAG, "返回调用方法--chooseBelow");

        if (RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // 这里是针对文件路径处理
                Uri uri = data.getData();
                if (uri != null) {
                    Log.e(TAG, "系统返回URI：" + uri.toString());
                    uploadMessage.onReceiveValue(uri);
                } else {
                    uploadMessageAboveL.onReceiveValue(null);
                }
            } else {
                // 以指定图像存储路径的方式调起相机，成功后返回data为空
                Log.e(TAG, "自定义结果：" + imageUri.toString());
                uploadMessage.onReceiveValue(imageUri);
            }
        } else {
            uploadMessage.onReceiveValue(null);
        }
        uploadMessage = null;
    }

    /**
     * Android API >= 21(Android 5.0) 版本的回调处理
     *
     * @param resultCode 选取文件或拍照的返回码
     * @param data       选取文件或拍照的返回结果
     */
    private void chooseAbove(int resultCode, Intent data) {
        Log.e(TAG, "返回调用方法--chooseAbove");
        if (RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // 这里是针对从文件中选图片的处理
                Uri[] results;
                Uri uriData = data.getData();
                if (uriData != null) {
                    results = new Uri[]{uriData};
                    for (Uri uri : results) {
                        Log.e(TAG, "系统返回URI：" + uri.toString());
                    }
                    uploadMessageAboveL.onReceiveValue(results);
                } else {
                    uploadMessageAboveL.onReceiveValue(null);
                }
            } else {
                Log.e(TAG, "自定义结果：" + imageUri.toString());
                uploadMessageAboveL.onReceiveValue(new Uri[]{imageUri});
            }
        } else {
            uploadMessageAboveL.onReceiveValue(null);
        }
        uploadMessageAboveL = null;
    }

    private void updatePhotos() {
        // 该广播即使多发（即选取照片成功时也发送）也没有关系，只是唤醒系统刷新媒体文件
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imageUri);
        sendBroadcast(intent);
    }

    private class FixWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!TextUtils.isEmpty(url)) {
                view.loadUrl(url);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (!TextUtils.isEmpty(url)) {
                view.loadUrl(url);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveBitmapToAlbum(screenShotBitmap);
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("保存截图至相册需要开启存储权限")
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent();
                                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + RoomActivity.this.getPackageName()));
                                startActivity(intent);
                            }
                        }).show();
            }
        } else if (requestCode == REQUEST_CODE_STORAGE_PERMISSION_FILE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooserActivity(mimeTypeArray);
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("需要开启存储权限")
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent();
                                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + RoomActivity.this.getPackageName()));
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
        }
        Log.e(TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mWebView != null) {
            mWebView.onResume();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mWebView != null) {
            //解决个别手机webview在后台不暂停音频播放的问题.
            mWebView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.e(TAG, "onSaveInstanceState");
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG, "onRestoreInstanceState");
    }
}