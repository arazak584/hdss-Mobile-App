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

import org.openhds.hdsscapture.Dialog.ChildDialogFragment;
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

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {

    ChildDialogFragment activity;
    LayoutInflater inflater;
    private Locations locations;
    private Socialgroup socialgroup;
    private Residency residency;
    private List<Individual> individualList;
    private CaseItem caseItem;

    boolean isFirstField = true;

    public interface ChildSelectionListener {
        void onChildSelected(String childId);
    }

    private ChildAdapter.ChildSelectionListener listener;

    public void setChildSelectionListener(ChildAdapter.ChildSelectionListener listener) {
        this.listener = listener;
    }



    public ChildAdapter(ChildDialogFragment activity, Residency residency, Locations locations,Socialgroup socialgroup, Individual individual) {
        this.activity = activity;
        this.locations = locations;
        this.residency = residency;
        this.socialgroup = socialgroup;
        individualList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lname,fname, dob;
        LinearLayout linearLayout;
        static int fieldToUpdate = 1;
        public ViewHolder(View view) {
            super(view);
            this.lname = view.findViewById(R.id.f_name);
            this.fname = view.findViewById(R.id.l_name);
            this.dob = view.findViewById(R.id.chd_dob);
            this.linearLayout = view.findViewById(R.id.searchChild);
        }

        public int getFieldToUpdate() {
            return fieldToUpdate;
        }

        public void setFieldToUpdate(int fieldToUpdate) {
            this.fieldToUpdate = fieldToUpdate;
        }

    }

    @NonNull
    @Override
    public ChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.child_itemlist, parent, false);
        ChildAdapter.ViewHolder viewHolder = new ChildAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChildAdapter.ViewHolder holder, int position) {
        final Individual individual = individualList.get(position);

        holder.dob.setText(individual.getDob());
        holder.fname.setText(individual.getFirstName());
        holder.lname.setText(individual.getLastName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text field in the IndividualFragment where you want to insert the mother's ID
                EditText childIdField = null;
                EditText childIdDOB = null;
                switch (holder.fieldToUpdate) {
                    case 1:
                        childIdField = activity.requireActivity().findViewById(R.id.out1_child_extid);
                        childIdDOB = activity.requireActivity().findViewById(R.id.out1_child_dob);
                        break;
                    case 2:
                        childIdField = activity.requireActivity().findViewById(R.id.out2_child_extid);
                        childIdDOB = activity.requireActivity().findViewById(R.id.out2_child_dob);
                        break;
                    case 3:
                        childIdField = activity.requireActivity().findViewById(R.id.out3_child_extid);
                        childIdDOB = activity.requireActivity().findViewById(R.id.out3_child_dob);
                        break;
                    case 4:
                        childIdField = activity.requireActivity().findViewById(R.id.out4_child_extid);
                        childIdDOB = activity.requireActivity().findViewById(R.id.out4_child_dob);
                        break;
                }

                // Set the mother's ID in the text field
                childIdField.setText(individual.getIndividual_uuid());
                childIdDOB.setText(individual.getDob());

                // Increment the flag to update the next field on the next click
                int nextFieldToUpdate = holder.fieldToUpdate + 1;

                // Reset the flag to 1 if it exceeds 4
                if (nextFieldToUpdate > 4) {
                    nextFieldToUpdate = 1;
                }

                holder.fieldToUpdate = nextFieldToUpdate;

                activity.dismiss();
            }
        });

    }


    @Override
    public int getItemCount() {
        return individualList.size();
    }

    public void child(String charText, IndividualViewModel individualViewModel) {
        //individualList.clear();
            if(locations != null)
                try {
                    List<Individual> list = individualViewModel.retrieveChild(socialgroup.getHouseExtId());

                    if (list != null) {
                        individualList.addAll(list);
                    }

                    if (list.isEmpty()) {
                        Toast.makeText(activity.getActivity(), "No Children Under 5 Years", Toast.LENGTH_SHORT).show();
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        notifyDataSetChanged();
    }

}
