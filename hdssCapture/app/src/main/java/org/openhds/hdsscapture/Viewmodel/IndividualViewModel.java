package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.IndividualRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.subentity.IndividualAmendment;
import org.openhds.hdsscapture.entity.subentity.IndividualEnd;
import org.openhds.hdsscapture.entity.subentity.IndividualPhone;
import org.openhds.hdsscapture.entity.subentity.IndividualResidency;
import org.openhds.hdsscapture.entity.subentity.IndividualVisited;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class IndividualViewModel extends AndroidViewModel {

    private final IndividualRepository repo;


    public IndividualViewModel(@NonNull Application application) {
        super(application);
        repo = new IndividualRepository(application);
    }


    public Individual findAll(String id) throws ExecutionException, InterruptedException {
        return repo.findAll(id);
    }

    public List<Individual> hoh(String comp,String id) throws ExecutionException, InterruptedException {
        return repo.hoh(comp,id);
    }
    public List<Individual> retrieveByLocationId(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveByLocationId(id);
    }

    public LiveData<List<Individual>> retrieveByHouseId(String id,String ids) {
        return repo.retrieveByHouseId(id,ids); // No throws, no Executor
    }

    public List<Individual> retrieveReturn(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveReturn(id);
    }

    public List<Individual> getHouseholdMembersSync(String id,String ids) throws ExecutionException, InterruptedException {
        return repo.getHouseholdMembersSync(id,ids);
    }

    public List<Individual> retrieveChild(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveChild(id);
    }

    public List<Individual> morbidity(String id) throws ExecutionException, InterruptedException {
        return repo.morbidity(id);
    }

    public List<Individual> retrieveBy(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveBy("%" + id + "%");
    }

    public List<Individual> retrieveByMother(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveByMother(id);
    }


    public List<Individual> retrieveByMotherSearch(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveByMotherSearch("%" + id + "%");
    }

    public List<Individual> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public List<Individual> find() throws ExecutionException, InterruptedException {
        return repo.find();
    }

    public List<Individual> retrieveBySearch(String id, String searchText) throws ExecutionException, InterruptedException {
        return repo.retrieveBySearch( "%" + id + "%", "%" + searchText + "%");
    }


    public List<Individual> retrieveByFatherSearch(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveByFatherSearch("%" + id + "%");
    }

    public List<Individual> retrieveByFather(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveByFather(id);
    }

    public List<Individual> retrievePartner(String id) throws ExecutionException, InterruptedException {
        return repo.retrievePartner(id);
    }

    public List<Individual> retrieveDup(String id,String ids) throws ExecutionException, InterruptedException {
        return repo.retrieveDup(id,ids);
    }

    public List<Individual> findDup(String id) throws ExecutionException, InterruptedException {
        return repo.findDup(id);
    }

    public List<Individual> retrieveHOH(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveHOH(id);
    }

    public Individual find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public Individual finds(String id) throws ExecutionException, InterruptedException {
        return repo.finds(id);
    }

    public Individual restore(String id) throws ExecutionException, InterruptedException {
        return repo.restore(id);
    }

    public Individual unk(String id) throws ExecutionException, InterruptedException {
        return repo.unk(id);
    }

    public List<Individual> retrieveDth(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveDth(id);
    }

    public List<Individual> retrieveOmg(String loc) throws ExecutionException, InterruptedException {
        return repo.retrieveOmg(loc);
    }

    public List<Individual> minors(String id,String ids) throws ExecutionException, InterruptedException {
        return repo.minors(id,ids);
    }

    public Individual mother(String id) throws ExecutionException, InterruptedException {
        return repo.mother(id);
    }

    public Individual father(String id) throws ExecutionException, InterruptedException {
        return repo.father(id);
    }

    public Individual visited(String id) throws ExecutionException, InterruptedException {
        return repo.visited(id);
    }

    public long countIndividuals(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return repo.countIndividuals(startDate, endDate, username);
    }

    public long counts(String id) throws ExecutionException, InterruptedException {
        return repo.counts(id);
    }

    public long count(String id,String ids) throws ExecutionException, InterruptedException {
        return repo.count(id,ids);
    }

    public long err(String id,String ids) throws ExecutionException, InterruptedException {
        return repo.err(id,ids);
    }

    public long errs(String id,String ids) throws ExecutionException, InterruptedException {
        return repo.errs(id,ids);
    }
    public long cnt() throws ExecutionException, InterruptedException {
        return repo.cnt();
    }
    public long cnts() throws ExecutionException, InterruptedException {
        return repo.cnts();
    }
    public long cntss() throws ExecutionException, InterruptedException {
        return repo.cntss();
    }

    public List<Individual> error() throws ExecutionException, InterruptedException {
        return repo.error();
    }

    public List<Individual> errors() throws ExecutionException, InterruptedException {
        return repo.errors();
    }

    public List<Individual> nulls(String id) throws ExecutionException, InterruptedException {
        return repo.nulls(id);
    }

    public List<Individual> errz(String id) throws ExecutionException, InterruptedException {
        return repo.errz(id);
    }

    public List<Individual> repo() throws ExecutionException, InterruptedException {
        return repo.repo();
    }


    public List<Individual> dupRegistration(String uuid,String ghcard,String phone) throws ExecutionException, InterruptedException {
        return repo.dupRegistration(uuid, ghcard,phone);
    }

    public LiveData<Individual> getView(String id) {
        return repo.view(id);
    }

    public void add(Individual data){ repo.create(data);}

    public void add(Individual... data){
        repo.create(data);
    }

    public void update(IndividualAmendment s, Consumer<Integer> callback) {
        repo.update(s, callback);
    }

//    public int dthupdate(IndividualEnd e){
//        return repo.dthupdate(e);
//    }
//    public void visited(IndividualVisited e, Consumer<Integer> callback) {
//        repo.visited(e, callback);
//    }

    public void updateCompnoForIndividuals(String oldCompno, String newCompno, Consumer<Integer> callback) {
        repo.updateCompnoForIndividuals(oldCompno, newCompno, callback);
    }

    public void dthupdate(IndividualEnd e, Consumer<Integer> callback) {
        repo.dthupdate(e, callback);
    }

    public int visited(IndividualVisited e){
        return repo.visited(e);
    }

    public void updateres(IndividualResidency e, Consumer<Integer> callback) {
        repo.updateres(e, callback);
    }

//    public void contact(IndividualPhone e, Consumer<Integer> callback) {
//        repo.contact(e, callback);
//    }

    public int contact(IndividualPhone e){
        return repo.contact(e);
    }

    public LiveData<Long> sync() {
        return repo.sync();
    }

    public LiveData<Long> hh(String id) {
        return repo.hh(id);
    }

    public LiveData<Long> isHeadInCompound(String id,String ids) {
        return repo.isHeadInCompound(id,ids);
    }

    public LiveData<Long> isActiveHouseholdHead(String id) {
        return repo.isActiveHouseholdHead(id);
    }
}
