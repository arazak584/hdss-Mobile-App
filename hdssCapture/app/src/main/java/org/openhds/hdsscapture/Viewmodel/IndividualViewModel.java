package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.IndividualRepository;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class IndividualViewModel extends AndroidViewModel {

    private final IndividualRepository individualRepository;


    public IndividualViewModel(@NonNull Application application) {
        super(application);
        individualRepository = new IndividualRepository(application);
    }


    public Individual findAll(String id) throws ExecutionException, InterruptedException {
        return individualRepository.findAll(id);
    }

    public List<Individual> retrieveByLocationId(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveByLocationId(id);
    }

    public List<Individual> retrieveChild(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveChild(id);
    }

    public List<Individual> retrieveBy(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveBy(id);
    }

    public List<Individual> retrieveByMother(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveByMother(id);
    }


    public List<Individual> retrieveByMotherSearch(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveByMotherSearch("%" + id + "%");
    }

    public List<Individual> findToSync() throws ExecutionException, InterruptedException {
        return individualRepository.findToSync();
    }

    public List<Individual> retrieveBySearch(String id, String searchText) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveBySearch( "%" + id + "%", "%" + searchText + "%");
    }


    public List<Individual> retrieveByFatherSearch(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveByFatherSearch("%" + id + "%");
    }

    public List<Individual> retrieveByFather(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveByFather(id);
    }

    public List<Individual> retrieveDup(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveDup(id);
    }

    public List<Individual> retrieveHOH(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveHOH(id);
    }

    public Individual find(String id) throws ExecutionException, InterruptedException {
        return individualRepository.find(id);
    }

    public long countIndividuals(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return individualRepository.countIndividuals(startDate, endDate, username);
    }

    public List<Individual> error() throws ExecutionException, InterruptedException {
        return individualRepository.error();
    }

    public List<Individual> errors() throws ExecutionException, InterruptedException {
        return individualRepository.errors();
    }

    public void add(Individual data){ individualRepository.create(data);}

    public void add(Individual... data){
        individualRepository.create(data);
    }

    public int update(IndividualAmendment s){
        return individualRepository.update(s);
    }
}
