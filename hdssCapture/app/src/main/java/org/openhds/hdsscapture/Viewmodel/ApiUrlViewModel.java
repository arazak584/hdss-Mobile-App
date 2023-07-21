package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.ApiUrlRepository;
import org.openhds.hdsscapture.Repositories.CodeBookRepository;
import org.openhds.hdsscapture.entity.ApiUrl;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ApiUrlViewModel extends AndroidViewModel {

    private final ApiUrlRepository repo;

    public ApiUrlViewModel(@NonNull Application application) {
        super(application);
        repo = new ApiUrlRepository(application);
    }

    public ApiUrl retrieve() throws ExecutionException, InterruptedException {
        return repo.retrieve();
    }


    public void add(ApiUrl data) {
        repo.create(data);
    }

    public void updateBaseUrl(String newBaseUrl) {
        ApiUrl apiUrl = new ApiUrl();
        apiUrl.setCodeUrl(newBaseUrl);
        add(apiUrl);
    }
}
