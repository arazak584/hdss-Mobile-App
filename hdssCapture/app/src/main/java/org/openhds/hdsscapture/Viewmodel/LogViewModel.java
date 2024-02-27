package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.DeathRepository;
import org.openhds.hdsscapture.Repositories.LogRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.LogBook;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LogViewModel extends AndroidViewModel {

    private final LogRepository logRepository;


    public LogViewModel(@NonNull Application application) {
        super(application);
        logRepository = new LogRepository(application);
    }


    public List<LogBook> retrieveToSync() throws ExecutionException, InterruptedException {
        return logRepository.retrieveToSync();
    }

    public void add(LogBook data){ logRepository.create(data);}

    public void add(LogBook... data){logRepository.create(data); }
}
