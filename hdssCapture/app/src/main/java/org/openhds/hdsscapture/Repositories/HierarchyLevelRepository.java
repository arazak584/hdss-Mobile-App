package org.openhds.hdsscapture.Repositories;

import android.app.Application;

import org.openhds.hdsscapture.AppDatabase;
import org.openhds.hdsscapture.Dao.HierarchyDao;
import org.openhds.hdsscapture.Dao.HierarchyLevelDao;
import org.openhds.hdsscapture.entity.Hierarchy;
import org.openhds.hdsscapture.entity.HierarchyLevel;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HierarchyLevelRepository {

    private final HierarchyLevelDao dao;

    public HierarchyLevelRepository(Application application){
        AppDatabase db = AppDatabase.getDatabase(application);
        dao = db.hierarchyLevelDao();
    }

    public void create(HierarchyLevel data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public void create(HierarchyLevel... data) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            dao.create(data);
        });
    }

    public List<HierarchyLevel> retrieve() throws ExecutionException, InterruptedException {

        Callable<List<HierarchyLevel>> callable = () -> dao.retrieve();

        Future<List<HierarchyLevel>> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
