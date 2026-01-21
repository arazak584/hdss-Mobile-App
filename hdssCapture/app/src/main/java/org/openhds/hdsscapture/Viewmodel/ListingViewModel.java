package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Repositories.ListingRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ListingViewModel extends AndroidViewModel {

    private final ListingRepository repo;

       public ListingViewModel(@NonNull Application application) {
        super(application);
           repo = new ListingRepository(application);
    }

    public Listing find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public Listing findByLocation(String id) throws ExecutionException, InterruptedException {
        return repo.findByLocation(id);
    }

    public List<Listing> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public long done(String id) throws ExecutionException, InterruptedException {
        return repo.done(id);
    }
    public long cnt() throws ExecutionException, InterruptedException {
        return repo.cnt();
    }

    public List<Listing> error() throws ExecutionException, InterruptedException {
        return repo.error();
    }

    public LiveData<Listing> getView(String id) {
        return repo.view(id);
    }

    public void add(Listing data){
        repo.create(data);
    }

    public void add(Listing... data){
        repo.create(data);
    }

    public void deleteByCompno(String compno, Runnable onComplete) {
        repo.deleteByCompno(compno, onComplete);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }


}
