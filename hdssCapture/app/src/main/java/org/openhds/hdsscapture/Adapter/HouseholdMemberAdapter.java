package org.openhds.hdsscapture.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;
import org.openhds.hdsscapture.fragment.SocialgroupFragment.HouseholdMemberData;

import java.util.ArrayList;
import java.util.List;

public class HouseholdMemberAdapter extends RecyclerView.Adapter<HouseholdMemberAdapter.ViewHolder> {

    private Context context;
    private List<HouseholdMemberData> householdMembers;
    private List<KeyValuePair> relationshipOptions;

    public HouseholdMemberAdapter(Context context, List<HouseholdMemberData> householdMembers, List<KeyValuePair> relationshipOptions) {
        this.context = context;
        this.householdMembers = householdMembers != null ? householdMembers : new ArrayList<>();
        this.relationshipOptions = relationshipOptions != null ? relationshipOptions : new ArrayList<>();
    }

    public void updateData(List<HouseholdMemberData> newMembers, List<KeyValuePair> newRelationships) {
        this.householdMembers = newMembers != null ? newMembers : new ArrayList<>();
        this.relationshipOptions = newRelationships != null ? newRelationships : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_household_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final HouseholdMemberData memberData = householdMembers.get(position);

        // Display member name from Individual entity
        String memberName = memberData.individual.firstName + " " + memberData.individual.lastName;
        holder.memberName.setText(memberName);

        // Setup relationship spinner
        ArrayAdapter<KeyValuePair> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, relationshipOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.relationshipSpinner.setAdapter(adapter);

        // Set current relationship
        int selectedPosition = 0;
        for (int i = 0; i < relationshipOptions.size(); i++) {
            if (relationshipOptions.get(i).codeValue == memberData.residency.rltn_head) {
                selectedPosition = i;
                break;
            }
        }
        holder.relationshipSpinner.setSelection(selectedPosition);

        // Handle relationship selection with validation
        holder.relationshipSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                KeyValuePair selected = relationshipOptions.get(spinnerPosition);
                int oldValue = memberData.residency.rltn_head;

                // If selecting Head of Household (value = 1)
                if (selected.codeValue == 1) {
                    // Check if another member is already the head
                    for (int i = 0; i < householdMembers.size(); i++) {
                        if (i != position && householdMembers.get(i).residency.rltn_head == 1) {
                            // Reset this selection back to old value
                            holder.relationshipSpinner.setOnItemSelectedListener(null);
                            for (int j = 0; j < relationshipOptions.size(); j++) {
                                if (relationshipOptions.get(j).codeValue == oldValue) {
                                    holder.relationshipSpinner.setSelection(j);
                                    break;
                                }
                            }
                            holder.relationshipSpinner.setOnItemSelectedListener(this);

                            // Show error message
                            android.widget.Toast.makeText(context,
                                    "Only one member can be Head of Household. Please change " +
                                            householdMembers.get(i).individual.firstName + " " +
                                            householdMembers.get(i).individual.lastName + "'s relationship first.",
                                    android.widget.Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                // Update the residency with the new relationship
                memberData.residency.rltn_head = selected.codeValue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    @Override
    public int getItemCount() {
        return householdMembers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView memberName;
        Spinner relationshipSpinner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.member_name);
            relationshipSpinner = itemView.findViewById(R.id.relationship_spinner);
        }
    }
}