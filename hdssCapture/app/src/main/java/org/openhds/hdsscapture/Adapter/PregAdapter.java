package org.openhds.hdsscapture.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.ClusterFragment;
import org.openhds.hdsscapture.fragment.DthFragment;
import org.openhds.hdsscapture.fragment.DthAdapterFragment;
import org.openhds.hdsscapture.fragment.PregAdapterFragment;
import org.openhds.hdsscapture.fragment.PregFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class PregAdapter extends RecyclerView.Adapter<PregAdapter.ViewHolder> {

    PregAdapterFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final Socialgroup socialgroup;
    private final List<Pregnancy> pregnancyList;
    private Individual individual;

    public PregAdapter(PregAdapterFragment activity, Locations locations, Socialgroup socialgroup) {
        this.activity = activity;
        this.locations = locations;
        this.socialgroup = socialgroup;
        pregnancyList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, permid, insertDate;
        LinearLayout linearLayout;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.permid = view.findViewById(R.id.dth_permid);
            this.firstname = view.findViewById(R.id.dth_firstname);
            this.lastname = view.findViewById(R.id.dth_lastname);
            this.insertDate = view.findViewById(R.id.text_insertdate);
            this.cardView = view.findViewById(R.id.itemIndividual);
        }
    }

    @NonNull
    @Override
    public PregAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.itemlist_preg, parent, false);
        PregAdapter.ViewHolder viewHolder = new PregAdapter.ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull PregAdapter.ViewHolder holder, int position) {
        final Pregnancy pregnancy = pregnancyList.get(position);
        Log.d("Preg", "Preg ID" + pregnancy.uuid);
        Date insertDate = pregnancy.recordedDate;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedInsertDate = sdf.format(insertDate);

        holder.permid.setText(pregnancy.visit_uuid);
        holder.firstname.setText(pregnancy.firstName);
        holder.lastname.setText(pregnancy.lastName);
        holder.insertDate.setText(formattedInsertDate);
        //holder.insertDate.setText(individual.insertDate);

        holder.cardView.setOnClickListener(v -> {
            if (pregnancy != null) {
                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                        PregFragment.newInstance(individual,locations, socialgroup,pregnancy)).commit();
                Log.d("Preg", "Preg ID" + pregnancy.uuid);
            }else {
                Toast.makeText(activity.getActivity(), "Error: Some data is null", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pregnancyList.size();
    }

    public void preg(PregnancyViewModel pregnancyViewModel) {
        pregnancyList.clear();
        if(socialgroup != null)
            try {
                List<Pregnancy> list = pregnancyViewModel.retrievePreg(socialgroup.getExtId());

                if (list != null) {
                    pregnancyList.addAll(list);
                }

                if (list.isEmpty()) {
                    Toast.makeText(activity.getActivity(), "No Pregnacy Record", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        notifyDataSetChanged();
    }
}
