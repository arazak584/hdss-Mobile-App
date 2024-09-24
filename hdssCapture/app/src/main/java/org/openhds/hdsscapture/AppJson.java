package org.openhds.hdsscapture;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.openhds.hdsscapture.Dao.ApiDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppJson {

    private static volatile AppJson INSTANCE;

    private static final String DEFAULT_BASE_URL = "http://localhost.org:8080";
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService jsonWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private final Context context;
    private Retrofit retrofit;

    private AppJson(Context context) {
        this.context = context.getApplicationContext();
        refreshRetrofit();
    }

    public static AppJson getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppJson.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppJson(context);
                }
            }
        }

        return INSTANCE;
    }

    public String getBaseUrl() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("BASE_URL", DEFAULT_BASE_URL);
    }

    public ApiDao getJsonApi() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLenient().create();
        if (retrofit == null) {
            refreshRetrofit();
        }
        return retrofit.create(ApiDao.class);
    }

    public void setBaseUrl(String baseUrl) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("BASE_URL", baseUrl);
        editor.apply();
        refreshRetrofit();
    }

    public static void resetInstance() {
        INSTANCE = null;
    }

    public void updateBaseUrl(String newBaseUrl) {
        setBaseUrl(newBaseUrl);
        resetInstance();
        INSTANCE = new AppJson(context); // Create a new instance with the updated configuration
        INSTANCE.refreshRetrofit();
    }

    public String getCurrentBaseUrl() {
        return getBaseUrl();
    }

    public void refreshRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").setLenient().create()))
                .client(new OkHttpClient.Builder()
                        .cache(null)
                        .build())
                .build();
    }

    public Context getAppContext() {
        return context;
    }

}

