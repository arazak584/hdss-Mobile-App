package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.openhds.hdsscapture.Repositories.HierarchyLevelRepository;
import org.openhds.hdsscapture.Repositories.HierarchyRepository;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.HierarchyLevel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class HierarchyLevelViewModel extends AndroidViewModel {

    private final HierarchyLevelRepository hierarchyLevelRepository;


    public HierarchyLevelViewModel(@NonNull Application application) {
        super(application);
        hierarchyLevelRepository = new HierarchyLevelRepository(application);
    }

    public List<HierarchyLevel> retrieve() throws ExecutionException, InterruptedException {
        return hierarchyLevelRepository.retrieve();
    }

    public void add(HierarchyLevel data){
        hierarchyLevelRepository.create(data);
    }

    public void add(HierarchyLevel... data){
        hierarchyLevelRepository.create(data);
    }

}
