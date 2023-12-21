package org.openhds.hdsscapture.Duplicate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TableRow;

import org.openhds.hdsscapture.Adapter.ViewsAdapter;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.DuplicateViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.entity.Duplicate;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subqueries.DupList;
import org.openhds.hdsscapture.entity.subqueries.Newloc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class DuplicateActivity extends AppCompatActivity {

    private DuplicateViewModel duplicateViewModel;
    private DupListAdapter dupListAdapter;
    private List<DupList> filterAll;
    TableRow tableRow;
    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate);

        duplicateViewModel = new ViewModelProvider(this).get(DuplicateViewModel.class);

        query();
    }

    private void query() {
        List<DupList> list = new ArrayList<>();

        try {

            final SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);


            int d=1;
            for (Duplicate e : duplicateViewModel.repo()) {
                DupList r1 = new DupList();
                if (e.getDob() != null){
                    String formattedDate = f.format(e.dob);
                    r1.id1 =  e.fname + " " + e.lname ;
                    r1.id2 =  formattedDate + " (" +"Keep"+")";
                }
                if (e.getDup_dob() != null){
                    String dob = f.format(e.dup_dob);
                    r1.id3 = "1" +". " + e.dup_fname + " " + e.dup_lname;
                    r1.id4 =  dob + " (" +"Delete"+")";
                }
                if (e.getDup1_dob() != null){
                    Date dob1Date = e.dup1_dob;
                    if (dob1Date != null) {
                        r1.isTab1Visible = true;
                        String dob1 = f.format(dob1Date);
                        r1.id5 = "2" +". " + e.dup1_fname + " " + e.dup1_lname;
                        r1.id6 = dob1 + " (" +"Delete"+")";
                    }
                }
                if (e.dup2_dob != null){
                    Date dob2Date = e.dup2_dob;
                    if (dob2Date != null) {
                        r1.isTab2Visible = true;
                        String dob2 = f.format(dob2Date);
                        r1.id7 = "3" +". " + e.dup2_fname + " " + e.dup2_lname;
                        r1.id8 = dob2 + " (" +"Delete"+")";
                    }
                }
                r1.title = e.dup_uuid;
                r1.index = d;

                list.add(r1);
                d++;
            }




            filterAll = new ArrayList<>(list);

            dupListAdapter = new DupListAdapter(this);
            dupListAdapter.setDupLists(list);
            RecyclerView recyclerView = findViewById(R.id.my_recycler_view_dups);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(dupListAdapter);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}