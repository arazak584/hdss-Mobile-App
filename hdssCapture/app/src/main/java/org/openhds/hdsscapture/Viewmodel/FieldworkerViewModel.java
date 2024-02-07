package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.FieldworkerRepository;
import org.openhds.hdsscapture.entity.Fieldworker;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class FieldworkerViewModel extends AndroidViewModel {

    private final FieldworkerRepository userTableRepository;


    public FieldworkerViewModel(@NonNull Application application) {
        super(application);
        userTableRepository = new FieldworkerRepository(application);
    }

    public Fieldworker find(String id, String password) throws ExecutionException, InterruptedException {
        return userTableRepository.find(id, password);
    }
    public Fieldworker finds(String id) throws ExecutionException, InterruptedException {
        return userTableRepository.finds(id);
    }

    public List<Fieldworker> fw() throws ExecutionException, InterruptedException {
        return userTableRepository.fw();
    }

    public void add(Fieldworker data){
        userTableRepository.create(data);
    }

    public void add(Fieldworker... data){
        userTableRepository.create(data);
    }

}
