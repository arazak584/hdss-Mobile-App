package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.QueriesRepository;
import org.openhds.hdsscapture.entity.ServerQueries;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class QueriesViewModel extends AndroidViewModel {

    private final QueriesRepository repo;


    public QueriesViewModel(@NonNull Application application) {
        super(application);
        repo = new QueriesRepository(application);
    }

    public void deleteAll() {
        repo.deleteAll();
    }

    public List<ServerQueries> findAll() throws ExecutionException, InterruptedException {
        return repo.findAll();
    }

    public List<ServerQueries> findByFw(String fw) throws ExecutionException, InterruptedException {
        return repo.findByFw(fw);
    }

    public void add(ServerQueries... data){
        repo.create(data);
    }

}
