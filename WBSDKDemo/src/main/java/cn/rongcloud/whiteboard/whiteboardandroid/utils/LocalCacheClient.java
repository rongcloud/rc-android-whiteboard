package cn.rongcloud.whiteboard.whiteboardandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import cn.rongcloud.whiteboard.whiteboardandroid.model.UserInfo;
import cn.rongcloud.whiteboard.whiteboardandroid.view.LoginActivity;
import cn.rongcloud.whiteboard.whiteboardandroid.view.SettingActivity;


/**
 * Created by yanke on 2021/8/3
 */
public class LocalCacheClient {
    private static final LocalCacheClient ourInstance = new LocalCacheClient();

    private static final String TAG = "LocalCacheClient";
    private static final String DEFAULT_CACHE = "demo_cache";
    private static final String DEFAULT_USER_INFO_CACHE = "demo_cache_user";
    private static final String USER_KEY_IN_USER_CACHE = "user";
    private static final String PHONE_KEY_IN_USER_CACHE = "phone";


    public static LocalCacheClient getInstance() {
        return ourInstance;
    }

    private LocalCacheClient() {
    }

    private SharedPreferences get(Context context, String name, int mode) {
        if (context == null) {
            return null;
        }
        return context.getSharedPreferences(TextUtils.isEmpty(name) ? DEFAULT_CACHE : name, mode);
    }

    public SharedPreferences get(Context context) {
        return get(context, null, Context.MODE_PRIVATE);
    }

    public SharedPreferences getUserCache(Context context) {
        return get(context, DEFAULT_USER_INFO_CACHE, Context.MODE_PRIVATE);
    }

    public void saveUser(Context context, String userJson) {
        SharedPreferences userCache = getUserCache(context);
        userCache.edit().putString(USER_KEY_IN_USER_CACHE, userJson).apply();
    }

    public String getUser(Context context) {
        SharedPreferences userCache = getUserCache(context);
        return userCache.getString(USER_KEY_IN_USER_CACHE, null);
    }

    public UserInfo getUserInfo(Context context) {
        SharedPreferences userCache = getUserCache(context);
        String userCacheJson = userCache.getString(USER_KEY_IN_USER_CACHE, null);
        return new Gson().fromJson(userCacheJson, UserInfo.class);
    }

    public void savePhone(Context context, String phone) {
        SharedPreferences phoneCache = get(context);
        phoneCache.edit().putString(PHONE_KEY_IN_USER_CACHE, phone).apply();
    }

    public String getPhone(Context context) {
        SharedPreferences phoneCache = get(context);
        return phoneCache.getString(PHONE_KEY_IN_USER_CACHE, null);
    }

    public void clearUserCache(Context context) {
        SharedPreferences userCache = getUserCache(context);
        userCache.edit().clear().apply();
    }
}
