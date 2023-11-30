package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.OdkRepository;
import org.openhds.hdsscapture.odk.Form;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class OdkViewModel extends AndroidViewModel {

    private final OdkRepository repo;


    public OdkViewModel(@NonNull Application application) {
        super(application);
        repo = new OdkRepository(application);
    }

    public List<Form> find() throws ExecutionException, InterruptedException {
        return repo.find();
    }


    public void add(Form... data){
        repo.create(data);
    }

}
