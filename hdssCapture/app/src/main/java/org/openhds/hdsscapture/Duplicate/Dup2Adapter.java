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
import org.openhds.hdsscapture.fragment.ClusterFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class Dup2Adapter extends RecyclerView.Adapter<Dup2Adapter.ViewHolder>{

    Dup2DialogFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final Socialgroup socialgroup;
    private final Residency residency;
    private final List<Individual> individualList;
    boolean isFirstField = true;

    public interface PartnerSelectionListener {
        void onPartnerSelected(String partnerId);
    }

    private Dup2Adapter.PartnerSelectionListener listener;

    public void setPartnerSelectionListener(Dup2Adapter.PartnerSelectionListener listener) {
        this.listener = listener;
    }

    public Dup2Adapter(Dup2DialogFragment activity, Residency residency, Locations locations, Socialgroup socialgroup) {
        this.activity = activity;
        this.locations = locations;
        this.residency = residency;
        this.socialgroup = socialgroup;
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
    public Dup2Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.dup_itemlist, parent, false);
        Dup2Adapter.ViewHolder viewHolder = new Dup2Adapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Dup2Adapter.ViewHolder holder, int position) {
        final Individual individual = individualList.get(position);

        holder.permid.setText(individual.getExtId());
        holder.dob.setText(individual.getDob());
        holder.firstname.setText(individual.getFirstName());
        holder.lastname.setText(individual.getLastName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text field in the IndividualFragment where you want to insert the mother's ID
                EditText partnerIdField = activity.requireActivity().findViewById(R.id.dup2_uuid);
                EditText fname = activity.requireActivity().findViewById(R.id.dup2_fname);
                EditText lname = activity.requireActivity().findViewById(R.id.dup2_lname);
                EditText dob = activity.requireActivity().findViewById(R.id.dup2_dob);

                // Set the mother's ID in the text field
                partnerIdField.setText(individual.getUuid());
                fname.setText(individual.getFirstName());
                lname.setText(individual.getLastName());
                dob.setText(individual.getDob());

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

            if(ClusterFragment.selectedLocation != null)
                try {
                    List<Individual> list = individualViewModel.retrieveDup(ClusterFragment.selectedLocation.getCompno());

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
