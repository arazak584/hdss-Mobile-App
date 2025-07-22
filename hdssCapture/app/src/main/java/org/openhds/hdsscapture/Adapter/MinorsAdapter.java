package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Dialog.MinorDialogFragment;
import org.openhds.hdsscapture.Dialog.PregnancyDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.ClusterFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MinorsAdapter extends RecyclerView.Adapter<MinorsAdapter.ViewHolder> {

    MinorDialogFragment activity;
    LayoutInflater inflater;
    private final Locations locations;
    private final Socialgroup socialgroup;
    private final Residency residency;
    private final List<Individual> individualList;
    private final String compno;

    public MinorsAdapter(MinorDialogFragment activity, Residency residency, Locations locations, Socialgroup socialgroup, String compno) {
        this.activity = activity;
        this.locations = locations;
        this.residency = residency;
        this.socialgroup = socialgroup;
        this.compno = compno;
        individualList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mothname, chdname, compno;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);
            this.mothname = view.findViewById(R.id.mothername);
            this.chdname = view.findViewById(R.id.childname);
            this.compno = view.findViewById(R.id.compno);
            this.linearLayout = view.findViewById(R.id.MinorIndividuals);
        }
    }

    @NonNull
    @Override
    public MinorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.minor_itemlist, parent, false);
        MinorsAdapter.ViewHolder viewHolder = new MinorsAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MinorsAdapter.ViewHolder holder, int position) {
        final Individual individual = individualList.get(position);

        holder.mothname.setText(individual.lastName);
        holder.chdname.setText(individual.firstName);
        holder.compno.setText(individual.compno);

    }

    @Override
    public int getItemCount() {
        return individualList.size();
    }

    public void filter(String charText, IndividualViewModel individualViewModel) {
        individualList.clear();
            if(socialgroup != null)
                try {
                    List<Individual> list = individualViewModel.minors(compno, socialgroup.extId);

                    if (list != null) {
                        individualList.addAll(list);
                    }

                    if (list.isEmpty()) {
                        Toast.makeText(activity.getActivity(), "No Minor", Toast.LENGTH_SHORT).show();
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        notifyDataSetChanged();
    }
}
