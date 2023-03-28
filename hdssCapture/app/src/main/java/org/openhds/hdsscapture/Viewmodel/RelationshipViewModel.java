package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.RelationshipRepository;
import org.openhds.hdsscapture.entity.Relationship;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RelationshipViewModel extends AndroidViewModel {

    private final RelationshipRepository relationshipRepository;


    public RelationshipViewModel(@NonNull Application application) {
        super(application);
        relationshipRepository = new RelationshipRepository(application);
    }


    public List<Relationship> findAll() throws ExecutionException, InterruptedException {
        return relationshipRepository.findAll();
    }

    public List<Relationship> findToSync() throws ExecutionException, InterruptedException {
        return relationshipRepository.findToSync();
    }

    public Relationship find(String id) throws ExecutionException, InterruptedException {
        return relationshipRepository.find(id);
    }

    public void add(Relationship data){ relationshipRepository.create(data);}

    public void add(Relationship... data){
        relationshipRepository.create(data);
    }
}
