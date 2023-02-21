package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.CountryRepository;
import org.openhds.hdsscapture.entity.Country;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CountryViewModel extends AndroidViewModel {

    private final CountryRepository countryRepository;


    public CountryViewModel(@NonNull Application application) {
        super(application);
        countryRepository = new CountryRepository(application);
    }
/*
    public Country find(String id) throws ExecutionException, InterruptedException {
        return countryRepository.find(id);
    }*/

    public List<Country> findAll() throws ExecutionException, InterruptedException {
        return countryRepository.findAll();
    }


    public void add(Country data){
        countryRepository.create(data);
    }

    public void add(Country... data){
        countryRepository.create(data);
    }

}
