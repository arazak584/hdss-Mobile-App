package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.RelationshipRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;
import org.openhds.hdsscapture.entity.subentity.RelationshipUpdate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RelationshipViewModel extends AndroidViewModel {

    private final RelationshipRepository repo;


    public RelationshipViewModel(@NonNull Application application) {
        super(application);
        repo = new RelationshipRepository(application);
    }

    public List<Relationship> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public Relationship find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }
    public Relationship ins(String id) throws ExecutionException, InterruptedException {
        return repo.ins(id);
    }

    public Relationship finds(String id) throws ExecutionException, InterruptedException {
        return repo.finds(id);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return repo.rej(uuid);
    }

    public List<Relationship> reject(String id) throws ExecutionException, InterruptedException {
        return repo.reject(id);
    }

    public List<Relationship> getByUuids(List<String> uuids) {
        try {
            return repo.getByUuids(uuids);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("RelationshipViewModel", "Error fetching by UUIDs: " + e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to fetch Relationship records", e);
        }
    }

    public LiveData<Relationship> getView(String id) {
        return repo.view(id);
    }

    public void add(Relationship data){ repo.create(data);}

    public void add(Relationship... data){
        repo.create(data);
    }

    public void update(RelationshipUpdate s, Consumer<Integer> callback) {
        repo.update(s, callback);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }

}
