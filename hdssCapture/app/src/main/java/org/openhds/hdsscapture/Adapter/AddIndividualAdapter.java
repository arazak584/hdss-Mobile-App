package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.Individual;

public class AddIndividualAdapter extends ListAdapter<Individual, AddIndividualAdapter.AddIndividualHolder> {

    //private List<AddIndividualActivity> addindividual = new ArrayList<>();
    private onItemClickListener listener;

    public AddIndividualAdapter() { super(DIFF_CALLBACK);  }

    private static final DiffUtil.ItemCallback<Individual> DIFF_CALLBACK = new DiffUtil.ItemCallback<Individual>() {
        @Override
        public boolean areItemsTheSame(@NonNull Individual oldItem, @NonNull Individual newItem) {
            return oldItem.getExtId() == newItem.getExtId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Individual oldItem, @NonNull Individual newItem) {
            return oldItem.getExtId().equals(newItem.getExtId()) &&
                    //oldItem.getFirstname().equals(newItem.getFirstname()) &&
                    //oldItem.getLastname().equals(newItem.getLastname()) &&
                    //oldItem.getNickname().equals(newItem.getNickname()) &&
                    oldItem.getDob().equals(newItem.getDob());
        }
    };

    @NonNull
    @Override
    public AddIndividualHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.individual_itemlist, parent, false);
        return new AddIndividualHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddIndividualHolder holder, int position) {
        Individual currentIndividual = getItem(position);
        holder.textViewExtid.setText(currentIndividual.getExtId());
        //holder.textViewFirstname.setText(currentIndividual.getFirstname());
        //holder.textViewLastname.setText(currentIndividual.getLastname());
        //holder.textViewNickname.setText(currentIndividual.getNickname());
        holder.textViewDob.setText(currentIndividual.getDob());
    }

    //get the expenses position to the outside world
    public Individual getIndividualAt(int position){
        return getItem(position);
    }

    class AddIndividualHolder extends RecyclerView.ViewHolder{
        private TextView textViewExtid;
        private TextView textViewFirstname;
        private TextView textViewLastname;
        private TextView textViewNickname;
        private TextView textViewDob;

        public AddIndividualHolder(@NonNull View itemView) {
            super(itemView);
            textViewExtid = itemView.findViewById(R.id.text_permid);
            textViewFirstname = itemView.findViewById(R.id.text_firstname);
            textViewLastname = itemView.findViewById(R.id.text_lastname);
            textViewDob = itemView.findViewById(R.id.text_dob);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //Check to not click an invalid position
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    //interface to make individual clickable before editing
    public interface onItemClickListener{
        void onItemClick(Individual individual);
    }

    public void setOnItemClickListener(AddIndividualAdapter.onItemClickListener listener){
        this.listener = listener;
    }
}
