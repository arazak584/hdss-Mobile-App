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
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;
import org.openhds.hdsscapture.fragment.MotherDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MotherAdapter extends RecyclerView.Adapter<MotherAdapter.ViewHolder> {

    MotherDialogFragment activity;
    LayoutInflater inflater;
    private Location location;
    private Socialgroup socialgroup;
    private Residency residency;
    private List<Individual> individualList;
    private CaseItem caseItem;

    public MotherAdapter(MotherDialogFragment activity, Residency residency, Location location, Socialgroup socialgroup) {
        this.activity = activity;
        this.location = location;
        this.residency = residency;
        this.socialgroup = socialgroup;
        individualList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, permid,dob;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);
            this.permid = view.findViewById(R.id.mother_permid);
            this.dob = view.findViewById(R.id.mother_dob);
            this.firstname = view.findViewById(R.id.mother_fname);
            this.lastname = view.findViewById(R.id.mother_lname);
            this.linearLayout = view.findViewById(R.id.searchedMother);
        }
    }

    @NonNull
    @Override
    public MotherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.mother_itemlist, parent, false);
        MotherAdapter.ViewHolder viewHolder = new MotherAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MotherAdapter.ViewHolder holder, int position) {
        final Individual individual = individualList.get(position);

        holder.permid.setText(individual.getExtId());
        holder.dob.setText(individual.getDob());
        holder.firstname.setText(individual.getFirstName());
        holder.lastname.setText(individual.getLastName());

        holder.linearLayout.setOnClickListener(v -> {
            //activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                   // IndividualFragment.newInstance(individual, residency, location, socialgroup,caseItem )).commit();
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
                List<Individual> list = individualViewModel.retrieveByMotherSearch(charText);

                if (list != null) {
                    individualList.addAll(list);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            if(location != null)
                try {
                    List<Individual> list = individualViewModel.retrieveByMother(location.getCompextId());

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
