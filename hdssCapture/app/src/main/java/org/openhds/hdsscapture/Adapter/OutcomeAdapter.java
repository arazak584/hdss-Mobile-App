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
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.OutcomeAdapterFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class OutcomeAdapter extends RecyclerView.Adapter<OutcomeAdapter.ViewHolder> {

    OutcomeAdapterFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final Socialgroup socialgroup;
    private final List<Pregnancyoutcome> pregnancyoutcomeList;
    private Individual individual;

    public OutcomeAdapter(OutcomeAdapterFragment activity, Locations locations, Socialgroup socialgroup) {
        this.activity = activity;
        this.locations = locations;
        this.socialgroup = socialgroup;
        pregnancyoutcomeList = new ArrayList<>();
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
    public OutcomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.itemlist_outcome, parent, false);
        OutcomeAdapter.ViewHolder viewHolder = new OutcomeAdapter.ViewHolder(listItem);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull OutcomeAdapter.ViewHolder holder, int position) {
        final Pregnancyoutcome outcome = pregnancyoutcomeList.get(position);
        Log.d("Outcome", "outcome ID" + outcome.uuid);
        Date insertDate = outcome.outcomeDate;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedInsertDate = sdf.format(insertDate);

        holder.permid.setText(outcome.fw_uuid);
        holder.firstname.setText(outcome.visit_uuid);
        holder.lastname.setText(outcome.father_uuid);
        holder.insertDate.setText(formattedInsertDate);
        //holder.insertDate.setText(individual.insertDate);

//        holder.cardView.setOnClickListener(v -> {
//            if (outcome != null) {
//                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                        OutcomeFragment.newInstance(individual,locations, socialgroup,outcome)).commit();
//                Log.d("Outcome", "outcome ID" + outcome.uuid);
//            }else {
//                Toast.makeText(activity.getActivity(), "Error: Some data is null", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return pregnancyoutcomeList.size();
    }

    public void outcome(PregnancyoutcomeViewModel viewModel) {
        pregnancyoutcomeList.clear();
        if(socialgroup != null)
            try {
                List<Pregnancyoutcome> list = viewModel.retrieveOutcome(socialgroup.getExtId());

                if (list != null) {
                    pregnancyoutcomeList.addAll(list);
                }

                if (list.isEmpty()) {
                    Toast.makeText(activity.getActivity(), "No Outcome Record", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        notifyDataSetChanged();
    }
}
