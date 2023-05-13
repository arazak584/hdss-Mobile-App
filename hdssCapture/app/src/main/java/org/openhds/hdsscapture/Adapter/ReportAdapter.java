package org.openhds.hdsscapture.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openhds.hdsscapture.Activity.ReportActivity;
import org.openhds.hdsscapture.R;
import org.openhds.hdsscapture.Viewmodel.DeathViewModel;
import org.openhds.hdsscapture.Viewmodel.DemographicViewModel;
import org.openhds.hdsscapture.Viewmodel.HdssSociodemoViewModel;
import org.openhds.hdsscapture.Viewmodel.IndividualViewModel;
import org.openhds.hdsscapture.Viewmodel.InmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.LocationViewModel;
import org.openhds.hdsscapture.Viewmodel.OutmigrationViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyViewModel;
import org.openhds.hdsscapture.Viewmodel.PregnancyoutcomeViewModel;
import org.openhds.hdsscapture.Viewmodel.RelationshipViewModel;
import org.openhds.hdsscapture.Viewmodel.SocialgroupViewModel;
import org.openhds.hdsscapture.Viewmodel.VisitViewModel;
import org.openhds.hdsscapture.entity.Death;
import org.openhds.hdsscapture.entity.Demographic;
import org.openhds.hdsscapture.entity.HdssSociodemo;
import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.entity.Inmigration;
import org.openhds.hdsscapture.entity.Locations;
import org.openhds.hdsscapture.entity.Outmigration;
import org.openhds.hdsscapture.entity.Pregnancy;
import org.openhds.hdsscapture.entity.Pregnancyoutcome;
import org.openhds.hdsscapture.entity.Relationship;
import org.openhds.hdsscapture.entity.Socialgroup;
import org.openhds.hdsscapture.entity.Visit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    ReportActivity activity;
    LayoutInflater inflater;
    private List<Individual> individualList;
    private List<Visit> visitList;
    private List<Visit> visitLists;
    private List<Locations> locationsLists;
    private List<Socialgroup> socialgroupList;
    private List<Inmigration> inmigrationList;
    private List<Outmigration> outmigrationList;
    private List<Pregnancy> pregnancyList;
    private List<Pregnancyoutcome> pregnancyoutcomeList;
    private List<Death> deathList;
    private List<Demographic> demographicList;
    private List<HdssSociodemo> hdssSociodemoList;
    private List<Relationship> relationshipList;
    private Context context;

    public ReportAdapter(ReportActivity activity) {
        this.activity = activity;
        individualList = new ArrayList<>();
        visitList = new ArrayList<>();
        visitLists = new ArrayList<>();
        locationsLists = new ArrayList<>();
        socialgroupList = new ArrayList<>();
        inmigrationList = new ArrayList<>();
        outmigrationList = new ArrayList<>();
        pregnancyList = new ArrayList<>();
        pregnancyoutcomeList = new ArrayList<>();
        deathList = new ArrayList<>();
        demographicList = new ArrayList<>();
        hdssSociodemoList = new ArrayList<>();
        relationshipList = new ArrayList<>();
        inflater = LayoutInflater.from(activity);
        context = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView indcnt, title;
        LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            this.indcnt = view.findViewById(R.id.report_count);
            this.title = view.findViewById(R.id.report_title);
            this.linearLayout = view.findViewById(R.id.hdssReport);

        }
    }


    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = inflater.inflate(R.layout.report_item, parent, false);
        ReportAdapter.ViewHolder viewHolder = new ReportAdapter.ViewHolder(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, int position) {
        if (position == 0 && individualList.size() >= 0) {
            final Individual individual = individualList.get(0);
            holder.indcnt.setText(individual.getIndividual_uuid());
            holder.title.setText("Individuals");
        } else if (position == 1 && visitList.size() >= 0) {
            final Visit visit = visitList.get(0);
            holder.indcnt.setText(visit.getVisitExtId());
            holder.title.setText("Household Visits");
        }else if (position == 2 && visitLists.size() >= 0) {
            final Visit visit = visitLists.get(0);
            holder.indcnt.setText(visit.getLocation_uuid());
            holder.title.setText("Location Visits");
        }else if (position == 3 && locationsLists.size() >= 0) {
            final Locations locations = locationsLists.get(0);
            holder.indcnt.setText(locations.getLocation_uuid());
            holder.title.setText("New Locations");
        }else if (position == 4 && socialgroupList.size() >= 0) {
            final Socialgroup socialgroup = socialgroupList.get(0);
            holder.indcnt.setText(socialgroup.getSocialgroup_uuid());
            holder.title.setText("New Households");
        }else if (position == 5 && inmigrationList.size() >= 0) {
            final Inmigration inmigration = inmigrationList.get(0);
            holder.indcnt.setText(inmigration.getIndividual_uuid());
            holder.title.setText("Inmigrations");
        }else if (position == 6 && outmigrationList.size() >= 0) {
            final Outmigration outmigration = outmigrationList.get(0);
            holder.indcnt.setText(outmigration.getIndividual_uuid());
            holder.title.setText("Outmigrations");
        }else if (position == 7 && pregnancyList.size() >= 0) {
            final Pregnancy pregnancy = pregnancyList.get(0);
            holder.indcnt.setText(pregnancy.getObs_uuid());
            holder.title.setText("Pregnancy Observations");
        }else if (position == 8 && pregnancyoutcomeList.size() >= 0) {
            final Pregnancyoutcome pregnancyoutcome = pregnancyoutcomeList.get(0);
            holder.indcnt.setText(pregnancyoutcome.getPreg_uuid());
            holder.title.setText("Pregnancy Outcomes");
        }else if (position == 9 && deathList.size() >= 0) {
            final Death death = deathList.get(0);
            holder.indcnt.setText(death.getIndividual_uuid());
            holder.title.setText("Deaths");
        }else if (position == 10 && demographicList.size() >= 0) {
            final Demographic demographic = demographicList.get(0);
            holder.indcnt.setText(demographic.getIndividual_uuid());
            holder.title.setText("Demographics");
        }else if (position == 11 && hdssSociodemoList.size() >= 0) {
            final HdssSociodemo hdssSociodemo = hdssSociodemoList.get(0);
            holder.indcnt.setText(hdssSociodemo.getSocialgroup_uuid());
            holder.title.setText("Household Socio-Demographics");
        }else if (position == 12 && relationshipList.size() >= 0) {
            final Relationship relationship = relationshipList.get(0);
            holder.indcnt.setText(relationship.getIndividual_uuid());
            holder.title.setText("Relationships");
        }

    }


    @Override
    public int getItemCount() {
        return individualList.size()+ visitList.size()+ visitLists.size()+ locationsLists.size()+ socialgroupList.size()
                + inmigrationList.size() + outmigrationList.size() + pregnancyList.size()+ pregnancyoutcomeList.size()
                + deathList.size()+ demographicList.size()+ hdssSociodemoList.size()+ relationshipList.size();
    }

    public void report(Date startDate, Date endDate, String username, IndividualViewModel individualViewModel, VisitViewModel visitViewModel, LocationViewModel locationViewModel,
                       SocialgroupViewModel socialgroupViewModel, InmigrationViewModel inmigrationViewModel, OutmigrationViewModel outmigrationViewModel,
                       PregnancyViewModel pregnancyViewModel, PregnancyoutcomeViewModel pregnancyoutcomeViewModel, DeathViewModel deathViewModel,
                       DemographicViewModel demographicViewModel, HdssSociodemoViewModel hdssSociodemoViewModel, RelationshipViewModel relationshipViewModel) {
        individualList.clear();
        visitList.clear();
        visitLists.clear();
        locationsLists.clear();
        socialgroupList.clear();
        inmigrationList.clear();
        outmigrationList.clear();
        pregnancyList.clear();
        pregnancyoutcomeList.clear();
        deathList.clear();
        demographicList.clear();
        hdssSociodemoList.clear();
        relationshipList.clear();
        if (startDate != null && endDate !=null && username!=null) {
            try {
                long count = individualViewModel.countIndividuals(startDate, endDate,username);
                Individual countIndividual = new Individual();
                countIndividual.setIndividual_uuid(String.format(Locale.getDefault(), "%d", count));
                individualList.add(countIndividual);

                // Get Household visit count
                long visitCount = visitViewModel.countVisits(startDate, endDate,username);
                Visit countVisit = new Visit();
                countVisit.setVisitExtId(String.format(Locale.getDefault(), "%d", visitCount));
                visitList.add(countVisit);

                // Get Location visit count
                long locCount = visitViewModel.countLocs(startDate, endDate, username);
                Visit countLoc = new Visit();
                countLoc.setLocation_uuid(String.format(Locale.getDefault(), "%d", locCount));
                visitLists.add(countLoc);

                // Get New Location count
                long locationsCount = locationViewModel.count(startDate, endDate,username);
                Locations countLocations = new Locations();
                countLocations.setLocation_uuid(String.format(Locale.getDefault(), "%d", locationsCount));
                locationsLists.add(countLocations);

                // Get New Household count
                long householdCount = socialgroupViewModel.count(startDate, endDate,username);
                Socialgroup counthse = new Socialgroup();
                counthse.setSocialgroup_uuid(String.format(Locale.getDefault(), "%d", householdCount));
                socialgroupList.add(counthse);

                // Get Inmigration count
                long imgCount = inmigrationViewModel.count(startDate, endDate,username);
                Inmigration countimg = new Inmigration();
                countimg.setIndividual_uuid(String.format(Locale.getDefault(), "%d", imgCount));
                inmigrationList.add(countimg);

                // Get Outmigration count
                long omgCount = outmigrationViewModel.count(startDate, endDate,username);
                Outmigration countomg = new Outmigration();
                countomg.setIndividual_uuid(String.format(Locale.getDefault(), "%d", omgCount));
                outmigrationList.add(countomg);

                // Get Pregnancy count
                long pregCount = pregnancyViewModel.count(startDate, endDate,username);
                Pregnancy countpreg = new Pregnancy();
                countpreg.setObs_uuid(String.format(Locale.getDefault(), "%d", pregCount));
                pregnancyList.add(countpreg);

                // Get Pregnancy Outcome count
                long birthCount = pregnancyoutcomeViewModel.count(startDate, endDate,username);
                Pregnancyoutcome countbirth = new Pregnancyoutcome();
                countbirth.setPreg_uuid(String.format(Locale.getDefault(), "%d", birthCount));
                pregnancyoutcomeList.add(countbirth);

                // Get Death count
                long deathCount = deathViewModel.count(startDate, endDate,username);
                Death countdeath = new Death();
                countdeath.setIndividual_uuid(String.format(Locale.getDefault(), "%d", deathCount));
                deathList.add(countdeath);

                // Get Demographics count
                long demoCount = demographicViewModel.count(startDate, endDate,username);
                Demographic countdemo = new Demographic();
                countdemo.setIndividual_uuid(String.format(Locale.getDefault(), "%d", demoCount));
                demographicList.add(countdemo);

                // Get SES count
                long sesCount = hdssSociodemoViewModel.count(startDate, endDate,username);
                HdssSociodemo countses = new HdssSociodemo();
                countses.setSocialgroup_uuid(String.format(Locale.getDefault(), "%d", sesCount));
                hdssSociodemoList.add(countses);

                // Get Relationship count
                long relCount = relationshipViewModel.count(startDate, endDate, username);
                Relationship countrel = new Relationship();
                countrel.setIndividual_uuid(String.format(Locale.getDefault(), "%d", relCount));
                relationshipList.add(countrel);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

}
