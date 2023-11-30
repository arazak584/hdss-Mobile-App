package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.ConfigRepository;
import org.openhds.hdsscapture.Repositories.RoundRepository;
import org.openhds.hdsscapture.entity.Configsettings;
import org.openhds.hdsscapture.entity.Round;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ConfigViewModel extends AndroidViewModel {

    private final ConfigRepository configRepository;


    public ConfigViewModel(@NonNull Application application) {
        super(application);
        configRepository = new ConfigRepository(application);
    }


    public List<Configsettings> findAll() throws ExecutionException, InterruptedException {
        return configRepository.findAll();
    }


    public void add(Configsettings... data){configRepository.create(data);}

}
