package org.openhds.hdsscapture.Utilities;

import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;

import java.util.concurrent.ExecutionException;

public class UniqueIDGen {

    public static String generateUniqueId(IndividualViewModel individualViewModel, String baseId) {
        int sequenceNumber = 1;
        String id = baseId + String.format("%03d", sequenceNumber);

        while (true) {
            try {
                if (individualViewModel.findAll(id) == null) break;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            sequenceNumber++;
            id = baseId + String.format("%03d", sequenceNumber);
        }

        return id;
    }

    public static String generateHouseholdId(SocialgroupViewModel socialgroupViewModel, String baseId){
        int sequenceNumber = 1;
        String id = baseId + String.format("%02d", sequenceNumber);
        while (true) {
            try {
                if (socialgroupViewModel.createhse(id) == null) break;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } // check if ID already exists in ViewModel
            sequenceNumber++; // increment sequence number if ID exists
            id = baseId + String.format("%02d", sequenceNumber); // generate new ID with updated sequence number
        }
        return id;
    }

}
