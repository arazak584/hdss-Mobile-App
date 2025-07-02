package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.DuplicateRepository;
import org.openhds.hdsscapture.entity.Duplicate;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DuplicateViewModel extends AndroidViewModel {

    private final DuplicateRepository duplicateRepository;

       public DuplicateViewModel(@NonNull Application application) {
        super(application);
           duplicateRepository = new DuplicateRepository(application);
    }

    public Duplicate find(String id) throws ExecutionException, InterruptedException {
        return duplicateRepository.find(id);
    }

    public Duplicate finds(String id) throws ExecutionException, InterruptedException {
        return duplicateRepository.finds(id);
    }

    public List<Duplicate> findToSync() throws ExecutionException, InterruptedException {
        return duplicateRepository.findToSync();
    }

    public List<Duplicate> repo() throws ExecutionException, InterruptedException {
        return duplicateRepository.repo();
    }

    public void add(Duplicate data){
        duplicateRepository.create(data);
    }

    public void add(Duplicate... data){
        duplicateRepository.create(data);
    }

}
