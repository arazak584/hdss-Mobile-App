package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.PregnancyoutcomeRepository;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Morbidity;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.subentity.OutcomeUpdate;
import org.openhds.hdsscapture.entity.subentity.ResidencyAmendment;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PregnancyoutcomeViewModel extends AndroidViewModel {

    private final PregnancyoutcomeRepository repo;


    public PregnancyoutcomeViewModel(@NonNull Application application) {
        super(application);
        repo = new PregnancyoutcomeRepository(application);
    }

    public List<Pregnancyoutcome> findToSync() throws ExecutionException, InterruptedException {
        return repo.findToSync();
    }

    public List<Pregnancyoutcome> error(String id) throws ExecutionException, InterruptedException {
        return repo.error(id);
    }

    public Pregnancyoutcome find(String id) throws ExecutionException, InterruptedException {
        return repo.find(id);
    }

    public Pregnancyoutcome preg(String id) throws ExecutionException, InterruptedException {
        return repo.preg(id);
    }

    public Pregnancyoutcome find1(String id) throws ExecutionException, InterruptedException {
        return repo.find1(id);
    }
    public Pregnancyoutcome findloc(String id,String locid) throws ExecutionException, InterruptedException {
        return repo.findloc(id,locid);
    }

    public Pregnancyoutcome findMother(String id) throws ExecutionException, InterruptedException {
        return repo.findMother(id);
    }

    public Pregnancyoutcome findedit(String id,String locid) throws ExecutionException, InterruptedException {
        return repo.findedit(id,locid);
    }

    public Pregnancyoutcome finds(String id) throws ExecutionException, InterruptedException {
        return repo.finds(id);
    }

    public Pregnancyoutcome finds3(String id) throws ExecutionException, InterruptedException {
        return repo.finds3(id);
    }

    public Pregnancyoutcome findsloc(String id,String locid) throws ExecutionException, InterruptedException {
        return repo.findsloc(id,locid);
    }

    public Pregnancyoutcome lastpregs(String id, Date recordedDate) throws ExecutionException, InterruptedException {
        return repo.lastpregs(id,recordedDate);
    }
    public Pregnancyoutcome find3(String id,String locid) throws ExecutionException, InterruptedException {
        return repo.find3(id,locid);
    }

    public Pregnancyoutcome findout(String id) throws ExecutionException, InterruptedException {
        return repo.findout(id);
    }

    public Pregnancyoutcome findout3(String id) throws ExecutionException, InterruptedException {
        return repo.findout3(id);
    }

    public Pregnancyoutcome ins(String id) throws ExecutionException, InterruptedException {
        return repo.ins(id);
    }

    public Pregnancyoutcome findpreg(String id) throws ExecutionException, InterruptedException {
        return repo.findpreg(id);
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return repo.count(startDate, endDate, username);
    }

    public long rej(String uuid) throws ExecutionException, InterruptedException {
        return repo.rej(uuid);
    }

    public long cnt(String id) throws ExecutionException, InterruptedException {
        return repo.cnt(id);
    }

    public List<Pregnancyoutcome> reject(String id) throws ExecutionException, InterruptedException {
        return repo.reject(id);
    }

    public List<Pregnancyoutcome> retrieveOutcome(String id) throws ExecutionException, InterruptedException {
        return repo.retrieveOutcome(id);
    }

    public Pregnancyoutcome getId(String id) throws ExecutionException, InterruptedException {
        return repo.getId(id);
    }

    public LiveData<Pregnancyoutcome> getView(String id) {
        return repo.view(id);
    }

    public void update(OutcomeUpdate s, Consumer<Integer> callback) {
        repo.update(s, callback);
    }

    public List<Pregnancyoutcome> getByUuids(List<String> uuids) {
        try {
            return repo.getByUuids(uuids);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("OutcomeViewModel", "Error fetching by UUIDs: " + e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to fetch Pregnancyoutcome records", e);
        }
    }

    public void add(Pregnancyoutcome data){ repo.create(data);}

    public void add(Pregnancyoutcome... data){ repo.create(data);  }

    public LiveData<Long> sync() {
        return repo.sync();
    }
}
