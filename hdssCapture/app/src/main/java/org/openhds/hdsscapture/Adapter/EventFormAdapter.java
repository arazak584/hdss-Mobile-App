package org.openhds.hdsscapture.Adapter;

import static org.openhds.hdsscapture.AppConstants.COMPLETE;
import static org.openhds.hdsscapture.AppConstants.EVENT_HDSS16;
import static org.openhds.hdsscapture.AppConstants.MARKED_COMPLETE;
import static org.openhds.hdsscapture.AppConstants.NOT_DONE;
import static org.openhds.hdsscapture.AppConstants.SUBMITTED;
import static org.openhds.hdsscapture.AppConstants.UPDATE;
import static org.openhds.hdsscapture.AppConstants.UPDATED;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Residency;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.subqueries.EventForm;
import org.openhds.hdsscapture.Duplicate.DupFragment;
import org.openhds.hdsscapture.fragment.EventsFragment;

import java.util.List;

public class EventFormAdapter extends RecyclerView.Adapter<EventFormAdapter.ViewHolder> {

    private final Locations locations;
    private final Socialgroup socialgroup;
    private final Residency residency;
    private final Individual individual;
    private final List<EventForm> eventForms;
    private final EventsFragment activity;


    public EventFormAdapter(Locations locations, Socialgroup socialgroup, Residency residency, Individual individual,List<EventForm> eventForms, EventsFragment activity) {
        this.locations = locations;
        this.socialgroup = socialgroup;
        this.residency = residency;
        this.individual = individual;
        this.eventForms = eventForms;
        this.activity = activity;

    }

    @NonNull
    @Override
    public EventFormAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_event_forms, parent, false);

        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull EventFormAdapter.ViewHolder holder, int position) {
        final EventForm eventForm = eventForms.get(position);
        final String status;

            if (eventForm.complete == null ) {
                holder.cardView.setBackgroundColor(Color.WHITE);
                 status = NOT_DONE;
            } else if (eventForm.complete != null) {
                if (eventForm.complete == COMPLETE ) {
                    holder.cardView.setBackgroundColor(Color.GREEN);
                    holder.textView_status.setTextColor(Color.BLACK);
                    holder.textView_event.setTextColor(Color.BLACK);
                    status = MARKED_COMPLETE;
                } else if (eventForm.complete == UPDATED) {
                    holder.cardView.setBackgroundColor(Color.YELLOW);
                    status = UPDATE;
                }else {
                    holder.cardView.setBackgroundColor(Color.BLUE);
                    holder.textView_status.setTextColor(Color.WHITE);
                    holder.textView_event.setTextColor(Color.WHITE);
                    status = SUBMITTED;
                }
            }else {
                holder.cardView.setBackgroundColor(Color.TRANSPARENT);
                status = NOT_DONE;
            }


        holder.textView_event.setText(eventForm.event_name);
        holder.textView_form.setText(eventForm.form_name);
        holder.textView_status.setText(status);
        holder.cardView.setOnClickListener(view -> formFactory(individual, residency, locations, socialgroup, eventForm));

    }

    private void formFactory(Individual individual, Residency residency, Locations locations, Socialgroup socialgroup, EventForm eventForm) {
        switch (eventForm.event_name) {
//            case EVENT_HDSS1: {
//                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                        ResidencyFragment.newInstance(individual,residency, locations,socialgroup,eventForm)).commit();
//
//                break;
//            }

//            case EVENT_HDSS3: {
//
//                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                        SocialgroupFragment.newInstance(individual,residency, locations, socialgroup, eventForm)).commit();
//
//                break;
//            }
//
//            case EVENT_HDSS4: {
//
//                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                        DemographicFragment.newInstance(individual,residency, locations, socialgroup, eventForm)).commit();
//
//                break;
//            }
//
//            case EVENT_HDSS7: {
//
//                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                        RelationshipFragment.newInstance(individual,residency, locations, socialgroup,eventForm)).commit();
//
//                break;
//            }
//
//
//            case EVENT_HDSS10: {
//
//                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                        PregnancyFragment.newInstance(individual,residency, locations, socialgroup, eventForm)).commit();
//
//                break;
//            }
//
//            case EVENT_HDSS13: {
//
//                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                        PregnancyExtraFragment.newInstance(individual,residency, locations, socialgroup, eventForm)).commit();
//
//                break;
//            }
//
//            case EVENT_SOCIO: {
//
//                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                        SocioFragment.newInstance(individual,residency, locations, socialgroup, eventForm)).commit();
//
//                break;
//            }
//
//            case EVENT_HDSS11: {
//
//                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                        PregnancyoutcomeFragment.newInstance(individual,residency, locations, socialgroup, eventForm)).commit();
//
//                break;
//            }
//
//            case EVENT_HDSS12: {
//
//                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                        Pregnancyoutcome1Fragment.newInstance(individual,residency, locations, socialgroup, eventForm)).commit();
//
//                break;
//            }
//            case EVENT_HDSS14: {
//
//                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                        AmendmentFragment.newInstance(individual,residency, locations, socialgroup, eventForm)).commit();
//
//                break;
//            }
//
//            case EVENT_HDSS15: {
//
//                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
//                        VaccinationFragment.newInstance(individual,residency, locations, socialgroup, eventForm)).commit();
//
//                break;
//            }

            case EVENT_HDSS16: {

                activity.requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_cluster,
                        DupFragment.newInstance(individual,residency, locations, socialgroup)).commit();

                break;
            }
        }

    }



    @Override
    public int getItemCount() {
        return eventForms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_event, textView_form, textView_status;
        public LinearLayout linearLayout;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textView_event = itemView.findViewById(R.id.textView_event);
            this.textView_form = itemView.findViewById(R.id.textView_form);
            this.textView_status = itemView.findViewById(R.id.textView_status);
            cardView = itemView.findViewById(R.id.linearLayout_eventform);
        }
    }
}
