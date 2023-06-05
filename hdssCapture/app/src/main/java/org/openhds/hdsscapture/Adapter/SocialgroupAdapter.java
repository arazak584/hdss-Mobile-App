package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.Dialog.HouseholdDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SocialgroupAdapter extends RecyclerView.Adapter<SocialgroupAdapter.ViewHolder> {

    HouseholdDialogFragment activity;
    LayoutInflater inflater;
    private Locations locations;
    private List<Socialgroup> socialgroupList;
    private Residency residency;
    private Individual individual;

    public interface HouseholdSelectionListener {
        void onHouseholdSelected(String HouseholdId);
    }

    private SocialgroupAdapter.HouseholdSelectionListener listener;

    public void setHouseholdSelectionListener(SocialgroupAdapter.HouseholdSelectionListener listener) {
        this.listener = listener;
    }

    public SocialgroupAdapter(HouseholdDialogFragment activity, Residency residency, Locations locations, Individual individual) {
        this.activity = activity;
        this.locations = locations;
        this.residency = residency;
        this.individual = individual;
        socialgroupList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, hhid;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);
            this.name = view.findViewById(R.id.hhead_names);
            this.hhid = view.findViewById(R.id.hhid_heads);
            this.linearLayout = view.findViewById(R.id.searchedHousehold);
        }
    }

    @NonNull
    @Override
    public SocialgroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.socialgroup_itemlist, parent, false);
        SocialgroupAdapter.ViewHolder viewHolder = new SocialgroupAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SocialgroupAdapter.ViewHolder holder, int position) {
        final Socialgroup socialgroup = socialgroupList.get(position);

        holder.name.setText(socialgroup.getGroupName());
        holder.hhid.setText(socialgroup.getExtId());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text field in the IndividualFragment where you want to insert the mother's ID
                EditText householdIdField = activity.requireActivity().findViewById(R.id.socialgroup);

                // Set the mother's ID in the text field
                householdIdField.setText(socialgroup.getUuid());

                // Hide the MotherDialogFragment
                activity.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return socialgroupList.size();
    }

    public void filter(String charText, SocialgroupViewModel socialgroupViewModel) {
        socialgroupList.clear();
            if(locations != null)
                try {
                    List<Socialgroup> list = socialgroupViewModel.retrieveBySocialgroup(locations.getCompextId());

                    if (list != null) {
                        socialgroupList.addAll(list);
                    }
                    if (list.isEmpty()) {
                        Toast.makeText(activity.getActivity(), "No Adult Male/Female Found In This Compound", Toast.LENGTH_SHORT).show();
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


        notifyDataSetChanged();
    }
}
