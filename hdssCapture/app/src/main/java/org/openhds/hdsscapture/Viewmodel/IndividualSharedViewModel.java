package org.openhds.hdsscapture.Viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;

public class IndividualSharedViewModel extends ViewModel {
    private MutableLiveData<Individual> selectedIndividual = new MutableLiveData<>();

    public LiveData<Individual> getSelectedIndividual() {
        return selectedIndividual;
    }

    public void setSelectedIndividual(Individual individual) {selectedIndividual.setValue(individual);}

    public Individual getCurrentSelectedIndividual() {return selectedIndividual.getValue();}

    public boolean hasSelectedLocation() {
        return selectedIndividual.getValue() != null;
    }
}