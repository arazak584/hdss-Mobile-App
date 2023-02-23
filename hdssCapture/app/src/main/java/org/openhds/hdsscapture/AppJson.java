package org.openhds.hdsscapture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.openhds.hdsscapture.Dao.ApiDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppJson {

    private static volatile AppJson INSTANCE;

    private static final String BASE_URL = "http://ksurvey.org:8080/api/";
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService jsonWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private final ApiDao jsonApi;

    private AppJson() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").setLenient().create();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        jsonApi = retrofit.create(ApiDao.class);
    }

    public static AppJson getInstance() {
        if (INSTANCE == null) {
            synchronized (AppJson.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppJson();
                }
            }
        }

        return INSTANCE;
    }

    public ApiDao getJsonApi() {
        return jsonApi;
    }
}
