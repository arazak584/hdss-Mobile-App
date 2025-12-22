package org.openhds.hdsscapture.Duplicate;

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
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class OriginalAdapter extends RecyclerView.Adapter<OriginalAdapter.ViewHolder>{

    OriginalDialogFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final Socialgroup socialgroup;
    private final Residency residency;
    private final List<Individual> individualList;
    boolean isFirstField = true;

    public interface PartnerSelectionListener {
        void onPartnerSelected(String partnerId);
    }

    private OriginalAdapter.PartnerSelectionListener listener;
    private final String compno;
    private final String individid;

    public void setPartnerSelectionListener(OriginalAdapter.PartnerSelectionListener listener) {
        this.listener = listener;
    }

    public OriginalAdapter(OriginalDialogFragment activity, Residency residency, Locations locations, Socialgroup socialgroup, String compno, String individid) {
        this.activity = activity;
        this.locations = locations;
        this.residency = residency;
        this.socialgroup = socialgroup;
        this.compno = compno;
        this.individid = individid;
        individualList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, permid, dob;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);
            this.permid = view.findViewById(R.id.dup_permid);
            this.dob = view.findViewById(R.id.dup_dob);
            this.firstname = view.findViewById(R.id.dup_fname);
            this.lastname = view.findViewById(R.id.dup_lname);
            this.linearLayout = view.findViewById(R.id.searchedDup);
        }

    }

    @NonNull
    @Override
    public OriginalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.dup_itemlist, parent, false);
        OriginalAdapter.ViewHolder viewHolder = new OriginalAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OriginalAdapter.ViewHolder holder, int position) {
        final Individual individual = individualList.get(position);

        holder.permid.setText(individual.getExtId());
        holder.dob.setText(individual.getDob());
        holder.firstname.setText(individual.getFirstName());
        holder.lastname.setText(individual.getLastName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text field in the IndividualFragment where you want to insert the mother's ID
                EditText partnerIdField = activity.requireActivity().findViewById(R.id.uuid);
                EditText fname = activity.requireActivity().findViewById(R.id.fname);
                EditText lname = activity.requireActivity().findViewById(R.id.lname);
                EditText dob = activity.requireActivity().findViewById(R.id.dob);
                EditText compno = activity.requireActivity().findViewById(R.id.compno);

                // Set the mother's ID in the text field
                partnerIdField.setText(individual.getUuid());
                fname.setText(individual.getFirstName());
                lname.setText(individual.getLastName());
                dob.setText(individual.getDob());
                compno.setText(individual.getCompno());

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
                List<Individual> list = individualViewModel.retrieveBy(charText);

                if (list != null) {
                    individualList.addAll(list);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            if(socialgroup != null)
                try {
                    List<Individual> list = individualViewModel.retrieveDup(compno, individid);

                    if (list != null) {
                        individualList.addAll(list);
                    }
                    if (list.isEmpty()) {
                        Toast.makeText(activity.getActivity(), "No Individual Found In This Compound", Toast.LENGTH_SHORT).show();
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
