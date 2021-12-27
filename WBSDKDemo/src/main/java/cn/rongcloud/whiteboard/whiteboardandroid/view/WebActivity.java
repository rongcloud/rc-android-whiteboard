package cn.rongcloud.whiteboard.whiteboardandroid.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import cn.rongcloud.whiteboard.sdk.dsbridge.DWebView;
import cn.rongcloud.whiteboard.whiteboardandroid.R;

public class WebActivity extends BaseActivity {

    private static String mUrl;
    private DWebView mWebView;

    public static void start(Context context, String url) {
        mUrl = url;
        Intent intent = new Intent(context, WebActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
    }

    private void initView() {
        mWebView = (DWebView) findViewById(R.id.web_view);
        mWebView.setWebViewClient(new ConferenceWebViewClient());
        if (!TextUtils.isEmpty(mUrl)) {
            mWebView.loadUrl(mUrl);
        }
    }

    public void backClick(View view) {
        finish();
    }

    private class ConferenceWebViewClient extends WebViewClient {

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
            if (!TextUtils.isEmpty(url) && url.toLowerCase().contains("rongcloud")) {
                view.loadUrl(url);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (!TextUtils.isEmpty(url) && url.toLowerCase().contains("rongcloud")) {
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
}