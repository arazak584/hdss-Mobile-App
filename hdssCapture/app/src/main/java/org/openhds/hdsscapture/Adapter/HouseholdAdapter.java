package org.openhds.hdsscapture.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.fragment.BlankFragment;
import org.openhds.hdsscapture.fragment.HouseholdFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HouseholdAdapter extends RecyclerView.Adapter<HouseholdAdapter.ViewHolder> {

    BlankFragment activity;
    LayoutInflater inflater;
    private Locations locations;
    private List<Socialgroup> socialgroupList;


    public HouseholdAdapter(BlankFragment activity, Locations locations) {
        this.activity = activity;
        this.locations = locations;
        socialgroupList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, hhid;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);
            this.name = view.findViewById(R.id.hhead_name);
            this.hhid = view.findViewById(R.id.hhid_head);
            this.linearLayout = view.findViewById(R.id.searchHousehold);
        }
    }

    @NonNull
    @Override
    public HouseholdAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.household_itemlist, parent, false);
        HouseholdAdapter.ViewHolder viewHolder = new HouseholdAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HouseholdAdapter.ViewHolder holder, int position) {
        final Socialgroup socialgroup = socialgroupList.get(position);

        holder.name.setText(socialgroup.getGroupName());
        holder.hhid.setText(socialgroup.getExtId());

        Integer visit = socialgroup.complete;

        if (visit != null) {
            holder.hhid.setTextColor(Color.parseColor("#32CD32"));
            holder.name.setTextColor(Color.parseColor("#32CD32"));
        }else {
            holder.hhid.setTextColor(Color.parseColor("#FF4500"));
            holder.name.setTextColor(Color.parseColor("#FF4500"));
        }


        holder.linearLayout.setOnClickListener(v -> {
            activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                   HouseholdFragment.newInstance(locations, socialgroup)).commit();
        });
    }

    @Override
    public int getItemCount() {
        return socialgroupList.size();
    }

//    public void filter(String charText, SocialgroupViewModel socialgroupViewModel) {
//        socialgroupList.clear();
//            if(locations != null)
//                try {
//                    List<Socialgroup> list = socialgroupViewModel.retrieveBySocialgroup(locations.getCompextId());
//
//                    if (list != null) {
//                        socialgroupList.addAll(list);
//                    }
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//
//        notifyDataSetChanged();
//    }

    public void search(SocialgroupViewModel socialgroupViewModel) {
        socialgroupList.clear();
        if(locations != null)
            try {
                List<Socialgroup> list = socialgroupViewModel.retrieveBySocialgroup(locations.getCompextId());

                if (list != null) {
                    socialgroupList.addAll(list);
                }
                if (list.isEmpty()) {
                    Toast.makeText(activity.getActivity(), "No household Found", Toast.LENGTH_SHORT).show();
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        notifyDataSetChanged();
        activity.dismissLoadingDialog();
    }

}
