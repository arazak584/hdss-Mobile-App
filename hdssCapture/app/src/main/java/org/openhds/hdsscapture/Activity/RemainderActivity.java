package org.openhds.hdsscapture.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.VillageViewModel;
import org.openhds.hdsscapture.entity.Village;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RemainderActivity extends AppCompatActivity {

    private Village villageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainder);

        final VillageViewModel villageViewModel = new ViewModelProvider(this).get(VillageViewModel.class);
        final Spinner villageSpinner = findViewById(R.id.spinnerRVillage);

        int ccSize = loadVillageData(villageSpinner, villageViewModel);
        if(ccSize > 1) {
            villageSpinner.setSelection(1);
        }

        villageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){
                    villageData = null;
                }else {
                    final Village data = (Village) parent.getItemAtPosition(position);
                    villageData = data;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private <T> void callable(Spinner spinner, T[] array){

        final ArrayAdapter<T> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, array
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }

    private int loadVillageData(Spinner spinner, VillageViewModel viewModel){
        int listSize = 0;
        try {
            List<Village> list = viewModel.findAll();
            list.add(0,new Village("","Select Village"));
            if(list!=null && !list.isEmpty()){
                callable(spinner, list.toArray(new Village[0]));
                listSize = list.size();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return listSize;
    }


}