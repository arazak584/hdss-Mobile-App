package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.RelationshipRepository;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Relationship;
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

    public void add(Relationship data){ relationshipRepository.create(data);}

    public void add(Relationship... data){
        relationshipRepository.create(data);
    }

    public int update(RelationshipUpdate s){
        return relationshipRepository.update(s);
    }

}
