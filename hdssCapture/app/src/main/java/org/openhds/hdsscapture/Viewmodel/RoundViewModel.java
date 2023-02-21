package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.RoundRepository;
import org.openhds.hdsscapture.entity.Round;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RoundViewModel extends AndroidViewModel {

    private final RoundRepository roundRepository;


    public RoundViewModel(@NonNull Application application) {
        super(application);
        roundRepository = new RoundRepository(application);
    }


    public List<Round> findAll() throws ExecutionException, InterruptedException {
        return roundRepository.findAll();
    }


    //public void add(Visit data){ visitRepository.create(data);}

    public void add(Round... data){roundRepository.create(data);}

}
