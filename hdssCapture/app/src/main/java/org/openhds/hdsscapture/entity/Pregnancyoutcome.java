package org.openhds.hdsscapture.entity;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "pregnancyoutcome")
public class Pregnancyoutcome extends BaseObservable {

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
    public String mother;

    @Expose
    public String father;

    @Expose
    public String childEverborn;

    @Expose
    public String visitid;

    @Expose
    public String NumberofBirths;

    @Expose
    public String fw;

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
    public Integer whlth_fac;//Which health facility
    @Expose
    public String whlth_fac_Other;
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
    public Integer chd_weight;//Was the child weighed at birth?
    @Expose
    public Integer chd_size;//How much did the child weigh (estimated baby size)
    @Expose
    public Integer weig_hcard;//Record weight in kilograms from Health Card

    @Expose
    public Integer complete;

    public Pregnancyoutcome(){}


    @Ignore
    public final SimpleDateFormat f = new SimpleDateFormat("yyyy-MMM-dd", Locale.US);

    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    public String getInsertDate() {
        if (insertDate == null) return "SELECT DATE OF VISIT";
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
        if (outcomeDate == null) return "SELECT DATE OF OUTCOME";
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
        if (conceptionDate == null) return "SELECT DATE OF Conception";
        return f.format(conceptionDate);
    }

    public void setConceptionDate(String conceptionDate) {
        try {
            this.conceptionDate = f.parse(conceptionDate);

        } catch (ParseException e) {
            System.out.println("Conception Date Error " + e.getMessage());
        }
    }


    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getMother() {
        return mother;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public String getChildEverborn() {
        return childEverborn;
    }

    public void setChildEverborn(String childEverborn) {
        this.childEverborn = childEverborn;
    }

    public String getVisitid() {
        return visitid;
    }

    public void setVisitid(String visitid) {
        this.visitid = visitid;
    }

    public String getNumberofBirths() {
        return NumberofBirths;
    }

    public void setNumberofBirths(String numberofBirths) {
        NumberofBirths = numberofBirths;
    }

    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
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

    public Integer getWhlth_fac() {
        return whlth_fac;
    }

    public void setWhlth_fac(Integer whlth_fac) {
        this.whlth_fac = whlth_fac;
    }

    public String getWhlth_fac_Other() {
        return whlth_fac_Other;
    }

    public void setWhlth_fac_Other(String whlth_fac_Other) {
        this.whlth_fac_Other = whlth_fac_Other;
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

    public Integer getChd_weight() {
        return chd_weight;
    }

    public void setChd_weight(Integer chd_weight) {
        this.chd_weight = chd_weight;
    }

    public Integer getChd_size() {
        return  chd_size;
    }

    public void setChd_size(Integer chd_size) {

            this.chd_size = chd_size ;

    }


    public String getWeig_hcard() {
        return  weig_hcard == null ? "" : String.valueOf(weig_hcard);
    }

    public void setWeig_hcard(String weig_hcard) {
        try {
            this.weig_hcard = (weig_hcard == null) ? null : Integer.valueOf(weig_hcard);
        } catch (NumberFormatException e) {
        }
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }


    //SPINNERS ENTITY
    public void setBirthplace (AdapterView<?> parent, View view, int position, long id) {

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

    }

    //Which health facility
    public void setWhlth_fac(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            whlth_fac = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            whlth_fac = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

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

    }

    //During the time that you were pregnant, did you receive IPT infront of a nurse?
    public void setChd_weight(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            chd_weight = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            chd_weight = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    //During the time that you were pregnant, did you receive IPT infront of a nurse?
    public void setChd_size(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            chd_size = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            chd_size = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

}
