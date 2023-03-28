package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Location;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.PregnancyDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PregnancyAdapter extends RecyclerView.Adapter<PregnancyAdapter.ViewHolder> {

    PregnancyDialogFragment activity;
    LayoutInflater inflater;
    private Location location;
    private Socialgroup socialgroup;
    private Residency residency;
    private List<Pregnancy> pregnancyList;
    private Individual individual;

    public PregnancyAdapter(PregnancyDialogFragment activity,Individual individual, Residency residency, Location location, Socialgroup socialgroup) {
        this.activity = activity;
        this.location = location;
        this.residency = residency;
        this.socialgroup = socialgroup;
        this.individual = individual;
        pregnancyList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, doc,exp;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);
            this.firstname = view.findViewById(R.id.preg_fname);
            this.lastname = view.findViewById(R.id.preg_lname);
            this.doc = view.findViewById(R.id.preg_Date);
            this.exp = view.findViewById(R.id.preg_DateDelivery);
            this.linearLayout = view.findViewById(R.id.searchedPregnancy);
        }
    }

    @NonNull
    @Override
    public PregnancyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.pregnancy_itemlist, parent, false);
        PregnancyAdapter.ViewHolder viewHolder = new PregnancyAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PregnancyAdapter.ViewHolder holder, int position) {
        final Pregnancy pregnancy = pregnancyList.get(position);

        holder.doc.setText(pregnancy.getRecordedDate());
        holder.exp.setText(pregnancy.getExpectedDeliveryDate());
        holder.firstname.setText(pregnancy.firstName);
        holder.lastname.setText(pregnancy.lastName);

        holder.linearLayout.setOnClickListener(v -> {
            activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                    PregnancyDialogFragment.newInstance(individual, residency, location, socialgroup,pregnancy )).commit();
        });
    }

    @Override
    public int getItemCount() {
        return pregnancyList.size();
    }

    public void filter(String charText, PregnancyViewModel pregnancyViewModel) {
        pregnancyList.clear();
            if(location != null)
                try {
                    List<Pregnancy> list = pregnancyViewModel.retrievePregnancy(location.getCompextId());

                    if (list != null) {
                        pregnancyList.addAll(list);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        notifyDataSetChanged();
    }
}
