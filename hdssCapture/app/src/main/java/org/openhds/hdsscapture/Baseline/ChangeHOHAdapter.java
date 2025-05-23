package org.openhds.hdsscapture.Baseline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChangeHOHAdapter extends RecyclerView.Adapter<ChangeHOHAdapter.ViewHolder>{

    ChangeHoHFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final Socialgroup socialgroup;
    private final List<Individual> individualList;
    public interface ChangehohSelectionListener {
        void onChangehohSelected(String headId);
    }

    private ChangeHOHAdapter.ChangehohSelectionListener listener;

    public void setChangehohSelectionListener(ChangeHOHAdapter.ChangehohSelectionListener listener) {
        this.listener = listener;
    }

    public ChangeHOHAdapter(ChangeHoHFragment activity, Locations locations, Socialgroup socialgroup) {
        this.activity = activity;
        this.locations = locations;
        this.socialgroup = socialgroup;
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
    public ChangeHOHAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.changehoh_itemlist, parent, false);
        ChangeHOHAdapter.ViewHolder viewHolder = new ChangeHOHAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeHOHAdapter.ViewHolder holder, int position) {
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

                // Hide the HHDialogFragment
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

            if(BaseFragment.selectedLocation != null)
                try {
                    List<Individual> list = individualViewModel.retrieveHOH(BaseFragment.selectedLocation.getCompno());

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
