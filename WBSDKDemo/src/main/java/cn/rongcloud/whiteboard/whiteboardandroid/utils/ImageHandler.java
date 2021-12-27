package cn.rongcloud.whiteboard.whiteboardandroid.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.rongcloud.whiteboard.whiteboardandroid.WBApplication;

/**
 * Created by yanke on 2021/8/11
 */
public class ImageHandler {
    private static final ImageHandler ourInstance = new ImageHandler();
    private static final String TAG = ImageHandler.class.getCanonicalName();

    public static ImageHandler getInstance() {
        return ourInstance;
    }

    private ImageHandler() {
    }

    /**
     * 添加水印并保存到系统相册
     */
    private void imgMerge() {
        new Thread(() -> {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 保存到相册
     *
     * @param src 源图片
     */
    public void savePhotoAlbum(Context context, Bitmap src) {
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        if (!isEmptyBitmap(src)) {
            return;
        }
        //要保存到的文件
        File target = createFile(context);
        //先保存到文件
        OutputStream outputStream;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(target));
            src.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            if (!src.isRecycled()) {
                src.recycle();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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
        ContentResolver resolver = context.getContentResolver();

        Uri insertUri = resolver.insert(external, values);
        try {
            FileInputStream fileInputStream = new FileInputStream(target);
            write2File(context, insertUri, fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * uri 关联着待写入的文件
     * inputStream 表示原始的文件流
     *
     * @param context
     * @param uri
     * @param inputStream
     */
    private void write2File(Context context, Uri uri, InputStream inputStream) {
        if (uri == null || inputStream == null) {
            return;
        }

        try {
            //从Uri构造输出流
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);

            byte[] in = new byte[1024];
            int len = 0;

            do {
                //从输入流里读取数据
                len = inputStream.read(in);
                if (len != -1) {
                    outputStream.write(in, 0, len);
                    outputStream.flush();
                }
            } while (len != -1);

            inputStream.close();
            outputStream.close();

        } catch (Exception e) {
            Log.d("test", e.getLocalizedMessage());
        }
    }

    private File createFile(Context context) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 判断Bitmap是否为空且是否调用过recycle()
     *
     * @return
     */
    private boolean isEmptyBitmap(Bitmap bitmap) {
        return bitmap != null && !bitmap.isRecycled();
    }
}
