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

import org.openhds.hdsscapture.Dialog.MotherDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.ClusterFragment;
import org.openhds.hdsscapture.fragment.HouseMembersFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MotherAdapter extends RecyclerView.Adapter<MotherAdapter.ViewHolder> {

    MotherDialogFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final List<Individual> individualList;
    private Individual individual;

    public interface MotherSelectionListener {
        void onMotherSelected(String motherId);
    }

    private MotherSelectionListener listener;

    public void setMotherSelectionListener(MotherSelectionListener listener) {
        this.listener = listener;
    }


    public MotherAdapter(MotherDialogFragment activity, Locations locations, Socialgroup socialgroup) {
        this.activity = activity;
        this.locations = locations;
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

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text field in the IndividualFragment where you want to insert the mother's ID
                EditText motherIdField = activity.requireActivity().findViewById(R.id.individual_mother);
                EditText motherAge = activity.requireActivity().findViewById(R.id.mothers_age);
                EditText mothername = activity.requireActivity().findViewById(R.id.mother_name);
                EditText motherDob = activity.requireActivity().findViewById(R.id.mother_dob);
                // Set the mother's ID in the text field
                motherIdField.setText(individual.getUuid());
                motherAge.setText(String.valueOf(individual.getAge()));
                mothername.setText(individual.getFirstName() + " " + individual.getLastName());
                motherDob.setText(individual.getDob());

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
        if (charText != null && charText.length() > 3) {
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

            if(ClusterFragment.selectedLocation != null)
                try {
                    List<Individual> list = individualViewModel.retrieveByMother(ClusterFragment.selectedLocation.getCompno());

                    if (list != null) {
                        individualList.addAll(list);
                    }
                    if (list.isEmpty()) {
                        Toast.makeText(activity.getActivity(), "No Adult Female Found In This Compound", Toast.LENGTH_SHORT).show();
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
