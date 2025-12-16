package org.openhds.hdsscapture.entity;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.BR;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "pregnancyoutcome",
        indices = {
                @Index(value = {"uuid"}, unique = true),
                @Index(value = {"mother_uuid", "complete"}),
                @Index(value = {"mother_uuid"}),
                @Index(value = {"father_uuid"}),
                @Index(value = {"visit_uuid"}),
                @Index(value = {"location"}),
                @Index(value = {"fw_uuid"}),
                @Index(value = {"complete"})
        })
public class Pregnancyoutcome extends BaseObservable implements Parcelable {

    @SerializedName("uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "uuid")
    @PrimaryKey
    public String uuid;

    @Expose
    public Date insertDate;

    @Expose
    public Date conceptionDate;

    @Expose
    public Date outcomeDate;

    @Expose
    public String mother_uuid;

    @Expose
    public String father_uuid;

    @Expose
    public String visit_uuid;

    @Expose
    public Integer numberofBirths;

    @Expose
    public Integer numberOfLiveBirths;

    @Expose
    public String fw_uuid;

    @Expose
    public Integer b_place;
    @Expose
    public Integer not_del; //Why was child not delivered at health facility
    @Expose
    public String not_del_other;//Other
    @Expose
    public Integer ass_del;//Who assisted you during deivery?
    @Expose
    public String ass_del_other;//Other, Specify
    @Expose
    public Integer how_del;//How was the child delivered?
    @Expose
    public String how_del_other;//Other, Specify
    @Expose
    public Integer first_nb;//Is this your first live birth
    @Expose
    public Integer l_birth;//How many previous live births have you had?
    @Expose
    public Integer rec_anc;//During the time that you were pregnant, did you receive any Antenatal Care?
    @Expose
    public Integer why_no_anc;//Why, No?
    @Expose
    public Integer where_anc;//Where did you receive the ANC?
    @Expose
    public String where_anc_Other;

    @Expose
    public String whlth_fac;
    @Expose
    public Integer who_anc;//Who attended to you?
    @Expose
    public Integer month_pg;//How many months pregnant were you when you first received  Antenatal Care?
    @Expose
    public Integer num_anc;//How many ANC visits did you make before you delivered?
    @Expose
    public Integer rec_ipt;//During the time that you were pregnant, did you receive IPT infront of a nurse?
    @Expose
    public Integer first_rec;//How many months pregnant were you when you first received  IPT?
    @Expose
    public Integer many_ipt;//How many times did you take IPT in front of a nurse during the pregnancy?

    @Expose
    public Integer id1001;//Did you ever breastfeed the child?
    @Expose
    public Integer id1002;//How long after delivery did you first put child to the breast?
    @Expose
    public Integer id1003;//In the first 2 days after birth was the child given anything other than breastmilk to drink (water, infant formula)
    @Expose
    public Integer id1004;//Are you still breastfeeding the child?
    @Expose
    public Integer id1005;//What did you feed the child with in the first 6 months of birth?
    @Expose
    public Integer id1006;//Do you have plans to have more children?
    @Expose
    public Integer id1007;//If YES, how long will you want to wait before becoming pregnant again?
    @Expose
    public Integer id1008;//Are you currently using any family planning method to delay or prevent pregnancy?
    @Expose
    public Integer id1009;//If Yes/NO, what will be your specific family planning method of preference when finally you decide to use one?

    @Expose
    public Integer stillbirth;
    @Expose
    public Integer father;

    @Expose
    public Integer extra;

    @Expose
    public Integer complete;

    @Expose
    public Integer id;
    @Expose
    public String sttime;

    @Expose
    public String edtime;

    @Expose
    public String pregnancy_uuid;

    @Expose
    public String location;

    @Expose
    public String comment;

    @Expose
    public Integer status = 0;
    @Expose
    public String supervisor;
    @Expose
    public Date approveDate;

    public Pregnancyoutcome(){}


    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Ignore
    private transient final SimpleDateFormat g = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public void setStatus(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            status = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getApproveDate() {
        if (approveDate == null) return "";
        return g.format(approveDate);
    }

    public void setApproveDate(String approveDate) {
        try {
            this.approveDate = g.parse(approveDate);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    public String getSttime() {
        return sttime;
    }

    public void setSttime(String sttime) {
        this.sttime = sttime;
    }

    public String getEdtime() {
        return edtime;
    }

    public void setEdtime(String edtime) {
        this.edtime = edtime;
    }

    public String getMother_uuid() {
        return mother_uuid;
    }

    public void setMother_uuid(String mother_uuid) {
        this.mother_uuid = mother_uuid;
    }

    public String getFather_uuid() {
        return father_uuid;
    }

    public void setFather_uuid(String father_uuid) {
        this.father_uuid = father_uuid;
    }

    public String getVisit_uuid() {
        return visit_uuid;
    }

    public void setVisit_uuid(String visit_uuid) {
        this.visit_uuid = visit_uuid;
    }

    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    public String getInsertDate() {
        if (insertDate == null) return null;
        return f.format(insertDate);
    }

    public void setInsertDate(String insertDate) {
        try {
            this.insertDate = f.parse(insertDate);
        } catch (ParseException e) {
            System.out.println("Visit Date Error " + e.getMessage());
        }
    }

    public String getOutcomeDate() {
        if (outcomeDate == null) return null;
        return f.format(outcomeDate);
    }

    public void setOutcomeDate(String outcomeDate) {
        try {
            this.outcomeDate = f.parse(outcomeDate);

        } catch (ParseException e) {
            System.out.println("Outcome Date Error " + e.getMessage());
        }
    }

    public String getConceptionDate() {
        if (conceptionDate == null) return null;
        return f.format(conceptionDate);
    }

    public void setConceptionDate(String conceptionDate) {
        try {
            this.conceptionDate = f.parse(conceptionDate);

        } catch (ParseException e) {
            System.out.println("Conception Date Error " + e.getMessage());
        }
    }



    public void setNumberofBirths(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            numberofBirths = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public Integer getB_place() {
        return b_place;
    }

    public void setB_place(Integer b_place) {
        this.b_place = b_place;
    }

    public Integer getNot_del() {
        return not_del;
    }

    public void setNot_del(Integer not_del) {
        this.not_del = not_del;
    }

    public String getNot_del_other() {
        return not_del_other;
    }

    public void setNot_del_other(String not_del_other) {
        this.not_del_other = not_del_other;
    }

    public Integer getAss_del() {
        return ass_del;
    }

    public void setAss_del(Integer ass_del) {
        this.ass_del = ass_del;
    }

    public String getAss_del_other() {
        return ass_del_other;
    }

    public void setAss_del_other(String ass_del_other) {
        this.ass_del_other = ass_del_other;
    }

    public Integer getHow_del() {
        return how_del;
    }

    public void setHow_del(Integer how_del) {
        this.how_del = how_del;
    }

    public String getHow_del_other() {
        return how_del_other;
    }

    public void setHow_del_other(String how_del_other) {
        this.how_del_other = how_del_other;
    }

    public Integer getFirst_nb() {
        return first_nb;
    }

    public void setFirst_nb(Integer first_nb) {
        this.first_nb = first_nb;
    }

    public String getL_birth() {
        return  l_birth == null ? "" : String.valueOf(l_birth);
    }

    public void setL_birth(String l_birth) {

        try {
            this.l_birth = (l_birth == null) ? null : Integer.valueOf(l_birth);
        } catch (NumberFormatException e) {
        }
    }

    public Integer getRec_anc() {
        return rec_anc;
    }

    public void setRec_anc(Integer rec_anc) {
        this.rec_anc = rec_anc;
    }

    public Integer getWhy_no_anc() {
        return why_no_anc;
    }

    public void setWhy_no_anc(Integer why_no_anc) {
        this.why_no_anc = why_no_anc;
    }

    public Integer getWhere_anc() {
        return where_anc;
    }

    public void setWhere_anc(Integer where_anc) {
        this.where_anc = where_anc;
    }

    public String getWhere_anc_Other() {
        return where_anc_Other;
    }

    public void setWhere_anc_Other(String where_anc_Other) {
        this.where_anc_Other = where_anc_Other;
    }

    public Integer getWho_anc() {
        return who_anc;
    }

    public void setWho_anc(Integer who_anc) {
        this.who_anc = who_anc;
    }

    public String getMonth_pg() {
        return  month_pg == null ? "" : String.valueOf(month_pg);
    }

    public void setMonth_pg(String month_pg) {
        try {
            this.month_pg = (month_pg == null) ? null : Integer.valueOf(month_pg);
        } catch (NumberFormatException e) {
        }
    }

    public String getNum_anc() {
        return  num_anc == null ? "" : String.valueOf(num_anc);
    }

    public void setNum_anc(String num_anc) {
        try {
            this.num_anc = (num_anc == null) ? null : Integer.valueOf(num_anc);
        } catch (NumberFormatException e) {
        }
    }

    public Integer getRec_ipt() {
        return rec_ipt;
    }

    public void setRec_ipt(Integer rec_ipt) {
        this.rec_ipt = rec_ipt;
    }

    public String getFirst_rec() {
        return  first_rec == null ? "" : String.valueOf(first_rec);
    }

    public void setFirst_rec(String first_rec) {
        try {
            this.first_rec = (first_rec == null) ? null : Integer.valueOf(first_rec);
        } catch (NumberFormatException e) {
        }
    }

    public String getMany_ipt() {
        return  many_ipt == null ? "" : String.valueOf(many_ipt);
    }

    public void setMany_ipt(String many_ipt) {
        try {
            this.many_ipt = (many_ipt == null) ? null : Integer.valueOf(many_ipt);
        } catch (NumberFormatException e) {
        }
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    public String getPregnancy_uuid() {
        return pregnancy_uuid;
    }

    public void setPregnancy_uuid(String pregnancy_uuid) {
        this.pregnancy_uuid = pregnancy_uuid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNumberOfLiveBirths() {
        return numberOfLiveBirths == null ? "" : String.valueOf(numberOfLiveBirths);
    }

    public void setNumberOfLiveBirths(String numberOfLiveBirths) {
        try {
            this.numberOfLiveBirths = (numberOfLiveBirths == null) ? null : Integer.valueOf(numberOfLiveBirths);
        } catch (NumberFormatException e) {
        }
    }

    protected Pregnancyoutcome(Parcel in) {

        this.insertDate = (java.util.Date) in.readSerializable();
        this.outcomeDate = (java.util.Date) in.readSerializable();

    }

    public static final Creator<Pregnancyoutcome> CREATOR = new Creator<Pregnancyoutcome>() {
        @Override
        public Pregnancyoutcome createFromParcel(Parcel in) {
            return new Pregnancyoutcome(in);
        }

        @Override
        public Pregnancyoutcome[] newArray(int size) {
            return new Pregnancyoutcome[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.insertDate);
        dest.writeSerializable(this.outcomeDate);
    }

    //SPINNERS ENTITY
    public void setB_place (AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            b_place = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            b_place = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
        }
        if(b_place == null || b_place!=4 || b_place!=5 || b_place!=6){
            this.not_del=null;
        }

    }

    //SPINNERS ENTITY COMPLETE FORM FOR SYNC
    public void setComplete(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            complete = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            complete = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //Why was child not delivered at health facility
    public void setNot_del(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            not_del = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            not_del = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //Who assisted you during deivery?
    public void setAss_del(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            ass_del = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            ass_del = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //How was the child delivered?
    public void setHow_del(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            how_del = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            how_del = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //Is this your first live birth
    public void setFirst_nb(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            first_nb = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            first_nb = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //Who attended to you?
    public void setWho_anc(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            who_anc = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            who_anc = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //During the time that you were pregnant, did you receive any Antenatal Care?
    public void setRec_anc(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            rec_anc = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            rec_anc = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //Why, No?
    public void setWhy_no_anc(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            why_no_anc = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            why_no_anc = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //Where did you receive the ANC?
    public void setWhere_anc(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            where_anc = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            where_anc = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }



    //During the time that you were pregnant, did you receive IPT infront of a nurse?
    public void setRec_ipt(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            rec_ipt = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            rec_ipt = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setId1001(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id1001 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id1001 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setId1002(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id1002 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id1002 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setId1003(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id1003 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id1003 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setId1004(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id1004 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id1004 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setId1005(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id1005 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id1005 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setId1006(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id1006 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id1006 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setId1007(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id1007 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id1007 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setId1008(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id1008 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id1008 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setId1009(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id1009 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id1009 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }


    //Still Birth

    public void setStillbirth(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stillbirth = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    //SPINNERS ENTITY
    public void setFather(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            father = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            father = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    //SPINNERS ENTITY
    public void setExtra(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            extra = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            extra = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    private void patternSkipper(View view) {

        if (view != null) {

            if(not_del == null || not_del!=77){
                setNot_del_other(null);
            }

            if(ass_del == null || ass_del!=77){
                setAss_del_other(null);
            }

            if(how_del == null || how_del!=77){
                setHow_del_other(null);
            }

            if(first_nb == null || first_nb!=2){
                l_birth = null;
            }

            if(rec_anc == null || rec_anc!=2){
                setWhy_no_anc(null);
            }

            if(rec_anc == null || rec_anc!=1){
                setWhere_anc(null);
            }

            if(where_anc == null || where_anc!=77){
                setWhere_anc_Other(null);
            }

            if(rec_anc == null || rec_anc!=1){
                setWho_anc(null);
                setMonth_pg(null);
                setNum_anc(null);
                setRec_ipt(null);
            }

            if(rec_ipt == null || rec_ipt!=1){
                setFirst_rec(null);
                setMany_ipt(null);
            }

            if(id1001== null || id1001!=1){
                id1002 = null;
                id1004 = null;
            }

            if(id1006== null || id1006!=1){
                id1007 = null;
            }

            notifyPropertyChanged(BR._all);
        }
    }

}
