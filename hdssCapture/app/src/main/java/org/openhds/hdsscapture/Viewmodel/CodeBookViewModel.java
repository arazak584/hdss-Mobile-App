package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.CodeBookRepository;
import org.openhds.hdsscapture.entity.CodeBook;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CodeBookViewModel extends AndroidViewModel {

    private final CodeBookRepository repo;


    public CodeBookViewModel(@NonNull Application application) {
        super(application);
        repo = new CodeBookRepository(application);
    }

    public List<CodeBook> findAll() throws ExecutionException, InterruptedException {
        return repo.findAll();
    }

    public List<KeyValuePair> findCodesOfFeature(String codeFeature) throws ExecutionException, InterruptedException {
        return repo.findCodesOfFeature(codeFeature);
    }

    public void add(CodeBook... data){
        repo.create(data);
    }

}
