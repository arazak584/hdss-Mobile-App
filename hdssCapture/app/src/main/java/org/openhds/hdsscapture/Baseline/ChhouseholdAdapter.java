package org.openhds.hdsscapture.Baseline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Dialog.HouseholdDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.ClusterFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChhouseholdAdapter extends RecyclerView.Adapter<ChhouseholdAdapter.ViewHolder> {

    ChangeDialogFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final List<Socialgroup> socialgroupList;
    private final Individual individual;

    public interface HouseholdSelectionListener {
        void onHouseholdSelected(String HouseholdId);
    }

    private ChhouseholdAdapter.HouseholdSelectionListener listener;

    public void setHouseholdSelectionListener(ChhouseholdAdapter.HouseholdSelectionListener listener) {
        this.listener = listener;
    }

    public ChhouseholdAdapter(ChangeDialogFragment activity, Locations locations, Individual individual) {
        this.activity = activity;
        this.locations = locations;
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
    public ChhouseholdAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.socialgroup_itemlist, parent, false);
        ChhouseholdAdapter.ViewHolder viewHolder = new ChhouseholdAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChhouseholdAdapter.ViewHolder holder, int position) {
        final Socialgroup socialgroup = socialgroupList.get(position);

        holder.name.setText(socialgroup.getGroupName());
        holder.hhid.setText(socialgroup.getExtId());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text field in the IndividualFragment where you want to insert the mother's ID
                EditText householdIdField = activity.requireActivity().findViewById(R.id.socialgroup_extid);
                EditText hohID = activity.requireActivity().findViewById(R.id.socialgroup_hohID);
                //Spinner compSpinner = activity.requireActivity().findViewById(R.id.membership_complete);

                // Set the household ID in the text field
                householdIdField.setText(socialgroup.getUuid());
                hohID.setText(socialgroup.getExtId());
                //compSpinner.setVisibility(View.VISIBLE);
               // compSpinner.setEnabled(false);
                //compSpinner.setSelection(1);

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
            if(BaseFragment.selectedLocation != null)
                try {
                    List<Socialgroup> list = socialgroupViewModel.changehousehold(BaseFragment.selectedLocation.getCompno());

                    if (list != null) {
                        socialgroupList.addAll(list);
                    }
                    if (list.isEmpty()) {
                        Toast.makeText(activity.getActivity(), "No Household Found", Toast.LENGTH_SHORT).show();
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


        notifyDataSetChanged();
    }
}
