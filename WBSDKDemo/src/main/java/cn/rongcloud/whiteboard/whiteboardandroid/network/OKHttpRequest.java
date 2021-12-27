package cn.rongcloud.whiteboard.whiteboardandroid.network;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import cn.rongcloud.whiteboard.whiteboardandroid.BuildConfig;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.ErrorResponseData;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.NetworkResponseData;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.RequestErrorCodeMap;
import cn.rongcloud.whiteboard.whiteboardandroid.network.response.ResponseData;
import cn.rongcloud.whiteboard.whiteboardandroid.utils.SHA1Util;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public abstract class OKHttpRequest<T> implements Request, Callback {
    private static final String TAG = "OKHttpRequest";

    protected RequestCallback requestCallback;

    protected String url;

    private Handler mMainHandler;

    public OKHttpRequest(String url) {
        this.url = url;
    }

    @Override
    public ResponseData syncRequest() {
        okhttp3.Request request = constructRequest();

        Headers newHeaders = constructHeaders(request);
        request = request.newBuilder().headers(newHeaders).build();

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("OKHttp", message);
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //设置连接超时时间
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .sslSocketFactory(OKHttpBuilderUtil.getSSLSocketFactory())
                .addInterceptor(logInterceptor)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                }).build();
        Call call = okHttpClient.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return new NetworkResponseData();
        }

        try {
            if (!response.isSuccessful()) {
                Log.d(TAG, "unsuccess hhtpcode:::::::" + response.code());
                String jsonResponse = response.body().string();
                ResponseData responseData = constructErrorResponse(jsonResponse);
                responseData.httpCode = response.code();
                return responseData;
            } else {
                String jsonResponse = response.body().string();
                return constructSuccessResponse(jsonResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new NetworkResponseData();
        }
    }

    @Override
    public void request(RequestCallback callback) {
        this.requestCallback = callback;
        okhttp3.Request request = constructRequest();

        Headers newHeaders = constructHeaders(request);
        request = request.newBuilder().headers(newHeaders).build();

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("OKHttp", message);
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //设置连接超时时间
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .sslSocketFactory(OKHttpBuilderUtil.getSSLSocketFactory())
                .addInterceptor(logInterceptor)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                }).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(this);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.d(TAG, "response faield:::::::::::");
        if (this.requestCallback != null) {
            this.requestCallback.onResponse(new NetworkResponseData());
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) {
            String jsonResponse = response.body().string();
            Log.d(TAG, " onResponse url:" + url + " jsonResponse:" + jsonResponse);
            ResponseData responseData = constructErrorResponse(jsonResponse);
            if (responseData == null) {
                responseData = new ResponseData();
            }
            responseData.httpCode = response.code();
            if (this.requestCallback != null) {
                this.requestCallback.onResponse(responseData);
            }
        } else {
            String jsonResponse = response.body().string();
            ResponseData responseData = constructSuccessResponse(jsonResponse);
            if (responseData == null) {
                responseData = new ResponseData();
            }
            responseData.httpCode = response.code();
            if (this.requestCallback != null) {
                this.requestCallback.onResponse(responseData);
            }
        }
    }

    protected ResponseData constructErrorResponse(String result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) {
            return new NetworkResponseData();
        } else {
            ErrorResponseData errorResponseData = new Gson().fromJson(result, ErrorResponseData.class);
            errorResponseData.errorMessage = RequestErrorCodeMap.ErrorCode.valueOf(errorResponseData.code).msg;
            return errorResponseData;
        }
    }

    private Headers constructHeaders(okhttp3.Request request) {
        String nonce = Integer.toString(new Random().nextInt(10000));
        String timestamp = Long.toString(System.currentTimeMillis());
        String signature = SHA1Util.SHA1(SHA1Util.SHA1(BuildConfig.APP_KEY) + nonce + timestamp);
        Headers headers = headers(request);
        return request.headers().newBuilder()
                .addAll(headers == null ? new Headers.Builder().build() : headers)
                .add("App-Key", BuildConfig.APP_KEY)
                .add("Nonce", nonce)
                .add("Timestamp", timestamp)
                .add("Signature", signature == null ? "" : signature)
                .build();
    }

    public Headers headers(okhttp3.Request request) {
        return null;
    }

    /**
     * 创建请求成功的结构体
     *
     * @param result
     * @return
     */
    protected abstract ResponseData constructSuccessResponse(String result);

    /**
     * 创建一个request
     *
     * @return
     */
    protected abstract okhttp3.Request constructRequest();

    public abstract OKHttpRequest params(T data);
}
