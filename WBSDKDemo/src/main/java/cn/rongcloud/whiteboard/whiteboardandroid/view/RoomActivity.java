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
        floatingActionButton.setImageBitmap(floatingActionButton.textAsBitmap("??????", 40, Color.WHITE));
        mWebView = (WBWebView) findViewById(R.id.web_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        DWebView.setWebContentsDebuggingEnabled(true);
        mWebView.setWebChromeClient(new FixWebChromeClient());
        mWebView.setWebViewClient(new FixWebViewClient());

        client = new RCWhiteBoard();
        client.init(this, mWebView);
    }

    /**
     * ????????????
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
                        ToastUtils.showToast("??????????????????");
                    }
                });
            }

            @Override
            public void onError(IRCWBEnum.ErrorCode errorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ToastUtils.showToast("??????????????????");
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
                ToastUtils.showToast("????????????");
                RoomActivity.this.finish();
            }

            @Override
            public void onRoomException(int code, String message) {
                new AlertDialog.Builder(RoomActivity.this)
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                    //???????????????????????????????????????
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
//            ToastUtils.showToast("??????????????????????????????????????????");
            return;
        }

        ImageHandler.getInstance().savePhotoAlbum(RoomActivity.this, bitmap);
        new AlertDialog.Builder(RoomActivity.this)
                .setTitle("??????")
                .setMessage("??????????????????????????????????????????")
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
     * ????????????????????????????????????????????????????????????
     * @param acceptTypes
     */
    private void openFileChooserActivity(String[] acceptTypes) {
        mimeTypeArray = acceptTypes;
        if (ContextCompat.checkSelfPermission(RoomActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //???????????????????????????????????????
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
        // ?????????????????????????????????????????????
        String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
//        imageUri = Uri.fromFile(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName));
        File target = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, MimeType.IMAGE);
        values.put(MediaStore.Images.Media.TITLE, System.currentTimeMillis() + ".jpg");

        //??????Android Q???????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //android Q???????????????DATA???????????????RELATIVE_PATH??????
            //RELATIVE_PATH?????????????????????????????????
            //DCIM???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        } else {
            String dstPath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES
                    + File.separator + target.getName();
            values.put(MediaStore.Images.Media.DATA, dstPath);
        }

        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = getContentResolver();

        imageUri = resolver.insert(external, values);

        //??????Intent.ACTION_GET_CONTENT????????????
        Intent content = new Intent(Intent.ACTION_GET_CONTENT);
        content.addCategory(Intent.CATEGORY_OPENABLE);
        if (mimeTypes != null) {
            content.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        content.setType("*/*");
        //??????
        content.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        //??????intent
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
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            }
        } else {
            //??????uploadMessage???uploadMessageAboveL???????????????????????????????????????
            //WebView????????????????????????????????????????????????????????????onReceiveValue???null?????????
            //??????WebView???????????????????????????????????????????????????????????????????????????????????????
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
     * Android API < 21(Android 5.0)?????????????????????
     *
     * @param resultCode ?????????????????????????????????
     * @param data       ????????????????????????????????????
     */
    private void chooseBelow(int resultCode, Intent data) {
        Log.e(TAG, "??????????????????--chooseBelow");

        if (RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // ?????????????????????????????????
                Uri uri = data.getData();
                if (uri != null) {
                    Log.e(TAG, "????????????URI???" + uri.toString());
                    uploadMessage.onReceiveValue(uri);
                } else {
                    uploadMessageAboveL.onReceiveValue(null);
                }
            } else {
                // ??????????????????????????????????????????????????????????????????data??????
                Log.e(TAG, "??????????????????" + imageUri.toString());
                uploadMessage.onReceiveValue(imageUri);
            }
        } else {
            uploadMessage.onReceiveValue(null);
        }
        uploadMessage = null;
    }

    /**
     * Android API >= 21(Android 5.0) ?????????????????????
     *
     * @param resultCode ?????????????????????????????????
     * @param data       ????????????????????????????????????
     */
    private void chooseAbove(int resultCode, Intent data) {
        Log.e(TAG, "??????????????????--chooseAbove");
        if (RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // ?????????????????????????????????????????????
                Uri[] results;
                Uri uriData = data.getData();
                if (uriData != null) {
                    results = new Uri[]{uriData};
                    for (Uri uri : results) {
                        Log.e(TAG, "????????????URI???" + uri.toString());
                    }
                    uploadMessageAboveL.onReceiveValue(results);
                } else {
                    uploadMessageAboveL.onReceiveValue(null);
                }
            } else {
                Log.e(TAG, "??????????????????" + imageUri.toString());
                uploadMessageAboveL.onReceiveValue(new Uri[]{imageUri});
            }
        } else {
            uploadMessageAboveL.onReceiveValue(null);
        }
        uploadMessageAboveL = null;
    }

    private void updatePhotos() {
        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
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
                        .setMessage("?????????????????????????????????????????????")
                        .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
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
                        .setMessage("????????????????????????")
                        .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
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
            //??????????????????webview???????????????????????????????????????.
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