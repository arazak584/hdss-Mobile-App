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

import org.openhds.hdsscapture.Dialog.FatherDialogFragment;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class FatherAdapter extends RecyclerView.Adapter<FatherAdapter.ViewHolder> {


    FatherDialogFragment activity;
    LayoutInflater inflater;
    private Locations locations;
    private List<Individual> individualList;

    public interface FatherSelectionListener {
        void onFatherSelected(String fatherId);
    }

    private FatherAdapter.FatherSelectionListener listener;

    public void setFatherSelectionListener(FatherAdapter.FatherSelectionListener listener) {
        this.listener = listener;
    }

    public FatherAdapter(FatherDialogFragment activity, Locations locations) {
        this.activity = activity;
        this.locations = locations;
        individualList = new ArrayList<>();
        inflater = LayoutInflater.from(activity.requireContext());
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname, permid, dob;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);
            this.permid = view.findViewById(R.id.father_permid);
            this.dob = view.findViewById(R.id.father_dob);
            this.firstname = view.findViewById(R.id.father_fname);
            this.lastname = view.findViewById(R.id.father_lname);
            this.linearLayout = view.findViewById(R.id.searchedFather);
        }
    }

    @NonNull
    @Override
    public FatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.father_itemlist, parent, false);
        FatherAdapter.ViewHolder viewHolder = new FatherAdapter.ViewHolder(listItem);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FatherAdapter.ViewHolder holder, int position) {
        final Individual individual = individualList.get(position);

        holder.permid.setText(individual.getExtId());
        holder.dob.setText(individual.getDob());
        holder.firstname.setText(individual.getFirstName());
        holder.lastname.setText(individual.getLastName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text field in the IndividualFragment where you want to insert the mother's ID
                EditText fatherIdField = activity.requireActivity().findViewById(R.id.individual_father);
                EditText fatherAge = activity.requireActivity().findViewById(R.id.fathers_age);
                EditText fathername = activity.requireActivity().findViewById(R.id.father_name);
                EditText fatherDob = activity.requireActivity().findViewById(R.id.father_dob);
                // Set the mother's ID in the text field
                fatherIdField.setText(individual.getUuid());
                fatherAge.setText(String.valueOf(individual.getAge()));
                fathername.setText(individual.getFirstName() + " " + individual.getLastName());
                fatherDob.setText(individual.getDob());

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
        if (charText != null && charText.length() > 4) {
            charText = charText.toLowerCase(Locale.getDefault());

            try {
                List<Individual> list = individualViewModel.retrieveByFatherSearch(charText);

                if (list != null) {
                    individualList.addAll(list);
                }

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            if(locations != null)
                try {
                    List<Individual> list = individualViewModel.retrieveByFather(locations.getCompextId());

                    if (list != null) {
                        individualList.addAll(list);
                    }

                    if (list.isEmpty()) {
                        Toast.makeText(activity.getActivity(), "No Adult Male Found In This Compound", Toast.LENGTH_SHORT).show();
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
