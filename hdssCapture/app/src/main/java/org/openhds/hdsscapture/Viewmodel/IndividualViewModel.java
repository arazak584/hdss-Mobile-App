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

    private final IndividualRepository individualRepository;


    public IndividualViewModel(@NonNull Application application) {
        super(application);
        individualRepository = new IndividualRepository(application);
    }


    public Individual findAll(String id) throws ExecutionException, InterruptedException {
        return individualRepository.findAll(id);
    }
    public Individual mapregistry(String id) throws ExecutionException, InterruptedException {
        return individualRepository.mapregistry(id);
    }

    public List<Individual> hoh(String comp,String id) throws ExecutionException, InterruptedException {
        return individualRepository.hoh(comp,id);
    }
    public List<Individual> retrieveByLocationId(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveByLocationId(id);
    }

    public LiveData<List<Individual>> retrieveByHouseId(String id,String ids) {
        return individualRepository.retrieveByHouseId(id,ids); // No throws, no Executor
    }

    public List<Individual> retrieveReturn(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveReturn(id);
    }

    public List<Individual> getHouseholdMembersSync(String id,String ids) throws ExecutionException, InterruptedException {
        return individualRepository.getHouseholdMembersSync(id,ids);
    }

    public List<Individual> retrieveChild(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveChild(id);
    }

    public List<Individual> morbidity(String id) throws ExecutionException, InterruptedException {
        return individualRepository.morbidity(id);
    }

    public List<Individual> retrieveBy(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveBy("%" + id + "%");
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

    public List<Individual> find() throws ExecutionException, InterruptedException {
        return individualRepository.find();
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

    public List<Individual> retrievePartner(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrievePartner(id);
    }

    public List<Individual> retrieveDup(String id,String ids) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveDup(id,ids);
    }

    public List<Individual> retrieveHOH(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveHOH(id);
    }

    public Individual find(String id) throws ExecutionException, InterruptedException {
        return individualRepository.find(id);
    }

    public Individual restore(String id) throws ExecutionException, InterruptedException {
        return individualRepository.restore(id);
    }

    public Individual unk(String id) throws ExecutionException, InterruptedException {
        return individualRepository.unk(id);
    }

    public List<Individual> retrieveDth(String id) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveDth(id);
    }

    public List<Individual> retrieveOmg(String loc) throws ExecutionException, InterruptedException {
        return individualRepository.retrieveOmg(loc);
    }

    public List<Individual> minors(String id,String ids) throws ExecutionException, InterruptedException {
        return individualRepository.minors(id,ids);
    }

    public Individual mother(String id) throws ExecutionException, InterruptedException {
        return individualRepository.mother(id);
    }

    public Individual father(String id) throws ExecutionException, InterruptedException {
        return individualRepository.father(id);
    }

    public Individual visited(String id) throws ExecutionException, InterruptedException {
        return individualRepository.visited(id);
    }

    public long countIndividuals(Date startDate, Date endDate,String username) throws ExecutionException, InterruptedException {
        return individualRepository.countIndividuals(startDate, endDate, username);
    }

    public long counts(String id) throws ExecutionException, InterruptedException {
        return individualRepository.counts(id);
    }

    public long count(String id,String ids) throws ExecutionException, InterruptedException {
        return individualRepository.count(id,ids);
    }

    public long err(String id,String ids) throws ExecutionException, InterruptedException {
        return individualRepository.err(id,ids);
    }

    public long errs(String id,String ids) throws ExecutionException, InterruptedException {
        return individualRepository.errs(id,ids);
    }
    public long cnt() throws ExecutionException, InterruptedException {
        return individualRepository.cnt();
    }
    public long cnts() throws ExecutionException, InterruptedException {
        return individualRepository.cnts();
    }
    public long cntss() throws ExecutionException, InterruptedException {
        return individualRepository.cntss();
    }

    public List<Individual> error() throws ExecutionException, InterruptedException {
        return individualRepository.error();
    }

    public List<Individual> errors() throws ExecutionException, InterruptedException {
        return individualRepository.errors();
    }

    public List<Individual> nulls(String id) throws ExecutionException, InterruptedException {
        return individualRepository.nulls(id);
    }

    public List<Individual> errz(String id) throws ExecutionException, InterruptedException {
        return individualRepository.errz(id);
    }

    public List<Individual> repo() throws ExecutionException, InterruptedException {
        return individualRepository.repo();
    }

    public List<Individual> getIndividualsForCsv(int gender, int minAge, int maxAge, int status) {
        return individualRepository.findIndividualsBatched(gender, minAge, maxAge, status);
    }

    public List<Individual> dupRegistration(String uuid,String ghcard,String phone) throws ExecutionException, InterruptedException {
        return individualRepository.dupRegistration(uuid, ghcard,phone);
    }

    public List<Individual> DuplicatesByPhone(String uuid,String ghcard) throws ExecutionException, InterruptedException {
        return individualRepository.DuplicatesByPhone(uuid, ghcard);
    }

    public LiveData<Individual> getView(String id) {
        return individualRepository.view(id);
    }

    public void add(Individual data){ individualRepository.create(data);}

    public void add(Individual... data){
        individualRepository.create(data);
    }

    public void update(IndividualAmendment s, Consumer<Integer> callback) {
        individualRepository.update(s, callback);
    }

//    public int dthupdate(IndividualEnd e){
//        return individualRepository.dthupdate(e);
//    }
//    public void visited(IndividualVisited e, Consumer<Integer> callback) {
//        individualRepository.visited(e, callback);
//    }

    public void updateCompnoForIndividuals(String oldCompno, String newCompno, Consumer<Integer> callback) {
        individualRepository.updateCompnoForIndividuals(oldCompno, newCompno, callback);
    }

    public void dthupdate(IndividualEnd e, Consumer<Integer> callback) {
        individualRepository.dthupdate(e, callback);
    }

    public int visited(IndividualVisited e){
        return individualRepository.visited(e);
    }

    public void updateres(IndividualResidency e, Consumer<Integer> callback) {
        individualRepository.updateres(e, callback);
    }

//    public void contact(IndividualPhone e, Consumer<Integer> callback) {
//        individualRepository.contact(e, callback);
//    }

    public int contact(IndividualPhone e){
        return individualRepository.contact(e);
    }

    public LiveData<Long> sync() {
        return individualRepository.sync();
    }

    public LiveData<Long> hh(String id) {
        return individualRepository.hh(id);
    }

    public LiveData<Long> isHeadInCompound(String id,String ids) {
        return individualRepository.isHeadInCompound(id,ids);
    }

    public LiveData<Long> isActiveHouseholdHead(String id) {
        return individualRepository.isActiveHouseholdHead(id);
    }
}
