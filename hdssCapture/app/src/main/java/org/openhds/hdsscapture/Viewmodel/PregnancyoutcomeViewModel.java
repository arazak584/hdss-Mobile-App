package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.PregnancyoutcomeRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.subentity.OutcomeUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PregnancyoutcomeViewModel extends AndroidViewModel {

    private final PregnancyoutcomeRepository pregnancyoutcomeRepository;


    public PregnancyoutcomeViewModel(@NonNull Application application) {
        super(application);
        pregnancyoutcomeRepository = new PregnancyoutcomeRepository(application);
    }

    public List<Pregnancyoutcome> findToSync() throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.findToSync();
    }

    public List<Pregnancyoutcome> error(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.error(id);
    }

    public Pregnancyoutcome find(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.find(id);
    }

    public Pregnancyoutcome preg(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.preg(id);
    }

    public Pregnancyoutcome find1(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.find1(id);
    }
    public Pregnancyoutcome findloc(String id,String locid) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.findloc(id,locid);
    }

    public Pregnancyoutcome findMother(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.findMother(id);
    }

    public Pregnancyoutcome findedit(String id,String locid) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.findedit(id,locid);
    }

    public Pregnancyoutcome finds(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.finds(id);
    }

    public Pregnancyoutcome finds3(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.finds3(id);
    }

    public Pregnancyoutcome findsloc(String id,String locid) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.findsloc(id,locid);
    }

    public Pregnancyoutcome lastpregs(String id, Date recordedDate) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.lastpregs(id,recordedDate);
    }
    public Pregnancyoutcome find3(String id,String locid) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.find3(id,locid);
    }

    public Pregnancyoutcome findout(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.findout(id);
    }

    public Pregnancyoutcome findout3(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.findout3(id);
    }

    public Pregnancyoutcome ins(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.ins(id);
    }

    public Pregnancyoutcome findpreg(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.findpreg(id);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.count(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.rej(uuid);
    }

    public long cnt(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.cnt(id);
    }

    public List<Pregnancyoutcome> reject(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.reject(id);
    }

    public List<Pregnancyoutcome> retrieveOutcome(String id) throws ExecutionException, InterruptedException {
        return pregnancyoutcomeRepository.retrieveOutcome(id);
    }

    public LiveData<Pregnancyoutcome> getView(String id) {
        return pregnancyoutcomeRepository.view(id);
    }

    public void update(OutcomeUpdate s, Consumer<Integer> callback) {
        pregnancyoutcomeRepository.update(s, callback);
    }

    public void add(Pregnancyoutcome data){ pregnancyoutcomeRepository.create(data);}

    public void add(Pregnancyoutcome... data){ pregnancyoutcomeRepository.create(data);  }

    public LiveData<Long> sync() {
        return pregnancyoutcomeRepository.sync();
    }
}
