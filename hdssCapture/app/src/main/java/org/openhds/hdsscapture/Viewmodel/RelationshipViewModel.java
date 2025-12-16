package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.RelationshipRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;
import org.openhds.hdsscapture.entity.subentity.RelationshipUpdate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RelationshipViewModel extends AndroidViewModel {

    private final RelationshipRepository relationshipRepository;


    public RelationshipViewModel(@NonNull Application application) {
        super(application);
        relationshipRepository = new RelationshipRepository(application);
    }

    public List<Relationship> findToSync() throws ExecutionException, InterruptedException {
        return relationshipRepository.findToSync();
    }

    public Relationship find(String id) throws ExecutionException, InterruptedException {
        return relationshipRepository.find(id);
    }
    public Relationship ins(String id) throws ExecutionException, InterruptedException {
        return relationshipRepository.ins(id);
    }

    public Relationship finds(String id) throws ExecutionException, InterruptedException {
        return relationshipRepository.finds(id);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return relationshipRepository.count(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return relationshipRepository.rej(uuid);
    }

    public List<Relationship> reject(String id) throws ExecutionException, InterruptedException {
        return relationshipRepository.reject(id);
    }

    public LiveData<Relationship> getView(String id) {
        return relationshipRepository.view(id);
    }

    public void add(Relationship data){ relationshipRepository.create(data);}

    public void add(Relationship... data){
        relationshipRepository.create(data);
    }

    public void update(RelationshipUpdate s, Consumer<Integer> callback) {
        relationshipRepository.update(s, callback);
    }

    public LiveData<Long> sync() {
        return relationshipRepository.sync();
    }

}
