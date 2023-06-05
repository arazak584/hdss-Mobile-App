package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Dialog.ChangeHohFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subentity.CaseItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChangeHOH extends RecyclerView.Adapter<ChangeHOH.ViewHolder>{

    ChangeHohFragment activity;
    LayoutInflater inflater;
    private Locations locations;
    private Socialgroup socialgroup;
    private Residency residency;
    private List<Individual> individualList;
    private CaseItem caseItem;

    public interface ChangehohSelectionListener {
        void onChangehohSelected(String headId);
    }

    private ChangeHOH.ChangehohSelectionListener listener;

    public void setChangehohSelectionListener(ChangeHOH.ChangehohSelectionListener listener) {
        this.listener = listener;
    }

    public ChangeHOH(ChangeHohFragment activity, Residency residency, Locations locations, Socialgroup socialgroup, CaseItem caseItem) {
        this.activity = activity;
        this.locations = locations;
        this.residency = residency;
        this.socialgroup = socialgroup;
        this.caseItem = caseItem;
        individualList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, permid;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);
            this.permid = view.findViewById(R.id.permid_head);
            this.firstname = view.findViewById(R.id.newhead_fname);
            this.lastname = view.findViewById(R.id.newhead_lname);
            this.linearLayout = view.findViewById(R.id.changeHousehold);
        }
    }

    @NonNull
    @Override
    public ChangeHOH.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.changehoh_itemlist, parent, false);
        ChangeHOH.ViewHolder viewHolder = new ChangeHOH.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeHOH.ViewHolder holder, int position) {
        final Individual individual = individualList.get(position);

        holder.permid.setText(individual.getExtId());
        holder.firstname.setText(individual.getFirstName());
        holder.lastname.setText(individual.getLastName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text field in the IndividualFragment where you want to insert the mother's ID
                EditText headIdField = activity.requireActivity().findViewById(R.id.head_extid);
                EditText headname = activity.requireActivity().findViewById(R.id.groupName);

                // Set the mother's ID in the text field
                headIdField.setText(individual.getUuid());
                headname.setText(individual.getFirstName() +' '+individual.getLastName());

                // Hide the MotherDialogFragment
                activity.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return individualList.size();
    }

    public void filter(String charText, IndividualViewModel individualViewModel) {
        individualList.clear();

            if(locations != null)
                try {
                    List<Individual> list = individualViewModel.retrieveHOH(locations.getCompextId());

                    if (list != null) {
                        individualList.addAll(list);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        notifyDataSetChanged();
    }
}
