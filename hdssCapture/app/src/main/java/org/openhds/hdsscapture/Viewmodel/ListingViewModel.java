package org.openhds.hdsscapture.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.openhds.hdsscapture.Repositories.ListingRepository;
import org.openhds.hdsscapture.entity.Listing;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ListingViewModel extends AndroidViewModel {

    private final ListingRepository listingRepository;

       public ListingViewModel(@NonNull Application application) {
        super(application);
           listingRepository = new ListingRepository(application);
    }

    public Listing find(String id) throws ExecutionException, InterruptedException {
        return listingRepository.find(id);
    }

    public List<Listing> findToSync() throws ExecutionException, InterruptedException {
        return listingRepository.findToSync();
    }

    public long count(Date startDate, Date endDate, String username) throws ExecutionException, InterruptedException {
        return listingRepository.count(startDate, endDate, username);
    }

    public List<Listing> error() throws ExecutionException, InterruptedException {
        return listingRepository.error();
    }



    public void add(Listing data){
        listingRepository.create(data);
    }

    public void add(Listing... data){
        listingRepository.create(data);
    }

}
