package org.openhds.hdsscapture.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;

import java.util.List;

public class CompoundAdapter extends RecyclerView.Adapter<CompoundAdapter.CompoundViewHolder> {

    private List<String> compounds;

    public CompoundAdapter(List<String> compounds) {
        this.compounds = compounds;
    }

    @NonNull
    @Override
    public CompoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compound, parent, false);
        return new CompoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompoundViewHolder holder, int position) {
        String compound = compounds.get(position);
        holder.bind(compound, position + 1);
    }

    @Override
    public int getItemCount() {
        return compounds.size();
    }

    public void updateData(List<String> newCompounds) {
        this.compounds.clear();
        this.compounds.addAll(newCompounds);
        notifyDataSetChanged();
    }

    static class CompoundViewHolder extends RecyclerView.ViewHolder {
        private TextView numberTextView;
        private TextView compoundTextView;

        public CompoundViewHolder(@NonNull View itemView) {
            super(itemView);
            numberTextView = itemView.findViewById(R.id.compound_number);
            compoundTextView = itemView.findViewById(R.id.compound_text);
        }

        public void bind(String compound, int position) {
            numberTextView.setText(String.valueOf(position));
            compoundTextView.setText(compound);
        }
    }
}