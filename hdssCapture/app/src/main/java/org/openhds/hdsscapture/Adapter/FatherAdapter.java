package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.fragment.FatherDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class FatherAdapter extends RecyclerView.Adapter<FatherAdapter.ViewHolder> {


    FatherDialogFragment activity;
    LayoutInflater inflater;
    private Locations locations;
    private Socialgroup socialgroup;
    private Residency residency;
    private List<Individual> individualList;
    private CaseItem caseItem;

    public FatherAdapter(FatherDialogFragment activity, Residency residency, Locations locations, Socialgroup socialgroup, CaseItem caseItem) {
        this.activity = activity;
        this.locations = locations;
        this.residency = residency;
        this.socialgroup = socialgroup;
        this.caseItem = caseItem;
        individualList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, permid, dob;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);
            this.permid = view.findViewById(R.id.father_permid);
            this.dob = view.findViewById(R.id.father_dob);
            this.firstname = view.findViewById(R.id.father_fname);
            this.lastname = view.findViewById(R.id.father_lname);
            this.linearLayout = view.findViewById(R.id.searchedFather);
        }
    }

    @NonNull
    @Override
    public FatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.father_itemlist, parent, false);
        FatherAdapter.ViewHolder viewHolder = new FatherAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FatherAdapter.ViewHolder holder, int position) {
        final Individual individual = individualList.get(position);

        holder.permid.setText(individual.getExtId());
        holder.dob.setText(individual.getDob());
        holder.firstname.setText(individual.getFirstName());
        holder.lastname.setText(individual.getLastName());

        holder.linearLayout.setOnClickListener(v -> {


        });
    }




    @Override
    public int getItemCount() {
        return individualList.size();
    }

    public void filter(String charText, IndividualViewModel individualViewModel) {
        individualList.clear();
        if (charText != null && charText.length() > 4) {
            charText = charText.toLowerCase(Locale.getDefault());

            try {
                List<Individual> list = individualViewModel.retrieveByFatherSearch(charText);

                if (list != null) {
                    individualList.addAll(list);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            if(locations != null)
                try {
                    List<Individual> list = individualViewModel.retrieveByFather(locations.getCompextId());

                    if (list != null) {
                        individualList.addAll(list);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        }
        notifyDataSetChanged();
    }
}
