package org.openhds.hdsscapture.entity;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;
import org.openhds.hdsscapture.AppConstants;
import org.openhds.hdsscapture.BR;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "morbidity",
        indices = {@Index(value = {"individual_uuid","fw_uuid","complete","socialgroup_uuid"}, unique = false)})
public class Morbidity extends BaseObservable {

    @Expose
    @NotNull
    @PrimaryKey
    public String individual_uuid;

    @Expose
    public Date insertDate;

    @Expose
    public Integer complete;

    @Expose
    public String fw_uuid;
    @Expose
    public String uuid;
    @Expose
    public String location_uuid;
    @Expose
    public String socialgroup_uuid;
    @Expose
    public String ind_name;

    @Expose
    public Integer fever;
    @Expose
    public Integer fever_days;
    @Expose
    public Integer fever_treat;
    @Expose
    public Integer hypertension;
    @Expose
    public Integer hypertension_dur;
    @Expose
    public Integer hypertension_trt;

    @Expose
    public Integer diabetes;
    @Expose
    public Integer diabetes_dur;
    @Expose
    public Integer diabetes_trt;
    @Expose
    public Integer heart;
    @Expose
    public Integer heart_dur;
    @Expose
    public Integer heart_trt;

    @Expose
    public Integer stroke;
    @Expose
    public Integer stroke_dur;
    @Expose
    public Integer stroke_trt;
    @Expose
    public Integer sickle;
    @Expose
    public Integer sickle_dur;
    @Expose
    public Integer sickle_trt;
    @Expose
    public Integer asthma;
    @Expose
    public Integer asthma_dur;
    @Expose
    public Integer asthma_trt;
    @Expose
    public Integer epilepsy;
    @Expose
    public Integer epilepsy_dur;
    @Expose
    public Integer epilepsy_trt;
    @Expose
    public String comment;
    @Expose
    public Integer status = 0;
    @Expose
    public String supervisor;
    @Expose
    public Date approveDate;
    @Expose
    public String fw_name;
    @Expose
    public String compno;
    @Expose
    public String sttime;

    @Expose
    public String edtime;


    public Morbidity(){}

    @Ignore
    public transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Bindable
    public String getInsertDate() {
        if (insertDate == null) return "";
        return f.format(insertDate);
    }

    public void setInsertDate(String insertDate) {
        try {
            this.insertDate = f.parse(insertDate);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
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

    @Bindable
    public String getApproveDate() {
        if (approveDate == null) return null;
        return f.format(approveDate);
    }

    public void setApproveDate(String approveDate) {
        try {
            this.approveDate = f.parse(approveDate);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }


    public void setFever_treat(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            fever_treat = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setFever(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            fever = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

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

    public void setHypertension(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            hypertension = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setHypertension_trt(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            hypertension_trt = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setDiabetes(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            diabetes = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setDiabetes_trt(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            diabetes_trt = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setHeart(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            heart = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setHeart_trt(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            heart_trt = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setStroke(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stroke = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setStroke_trt(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stroke_trt = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setSickle(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            sickle = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setSickle_trt(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            sickle_trt = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setAsthma(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            asthma = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setAsthma_trt(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            asthma_trt = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setEpilepsy(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            epilepsy = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setEpilepsy_trt(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            epilepsy_trt = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public String getHypertension_dur() {
        return hypertension_dur == null ? "" : String.valueOf(hypertension_dur);
    }

    public void setHypertension_dur(String hypertension_dur) {
        if (hypertension_dur == null) this.hypertension_dur = null;
        else
            try {
                this.hypertension_dur = Integer.valueOf(hypertension_dur);
            } catch (NumberFormatException e) {
            }
    }

    public String getDiabetes_dur() {
        return diabetes_dur == null ? "" : String.valueOf(diabetes_dur);
    }
    public void setDiabetes_dur(String diabetes_dur) {
        if (diabetes_dur == null) this.diabetes_dur = null;
        else
            try {
                this.diabetes_dur = Integer.valueOf(diabetes_dur);
            } catch (NumberFormatException e) {
            }
    }

    public String getHeart_dur() {
        return heart_dur == null ? "" : String.valueOf(heart_dur);
    }
    public void setHeart_dur(String heart_dur) {
        if (heart_dur == null) this.heart_dur = null;
        else
            try {
                this.heart_dur = Integer.valueOf(heart_dur);
            } catch (NumberFormatException e) {
            }
    }

    public String getStroke_dur() {
        return stroke_dur == null ? "" : String.valueOf(stroke_dur);
    }
    public void setStroke_dur(String stroke_dur) {
        if (stroke_dur == null) this.stroke_dur = null;
        else
            try {
                this.stroke_dur = Integer.valueOf(stroke_dur);
            } catch (NumberFormatException e) {
            }
    }

    public String getSickle_dur() {
        return sickle_dur == null ? "" : String.valueOf(sickle_dur);
    }
    public void setSickle_dur(String sickle_dur) {
        if (sickle_dur == null) this.sickle_dur = null;
        else
            try {
                this.sickle_dur = Integer.valueOf(sickle_dur);
            } catch (NumberFormatException e) {
            }
    }

    public String getAsthma_dur() {
        return asthma_dur == null ? "" : String.valueOf(asthma_dur);
    }
    public void setAsthma_dur(String asthma_dur) {
        if (asthma_dur == null) this.asthma_dur = null;
        else
            try {
                this.asthma_dur = Integer.valueOf(asthma_dur);
            } catch (NumberFormatException e) {
            }
    }

    public String getEpilepsy_dur() {
        return epilepsy_dur == null ? "" : String.valueOf(epilepsy_dur);
    }
    public void setEpilepsy_dur(String epilepsy_dur) {
        if (epilepsy_dur == null) this.epilepsy_dur = null;
        else
            try {
                this.epilepsy_dur = Integer.valueOf(epilepsy_dur);
            } catch (NumberFormatException e) {
            }
    }

    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    @NotNull
    public String getSocialgroup_uuid() {
        return socialgroup_uuid;
    }

    public void setSocialgroup_uuid(@NotNull String socialgroup_uuid) {
        this.socialgroup_uuid = socialgroup_uuid;
    }

    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLocation_uuid() {
        return location_uuid;
    }

    public void setLocation_uuid(String location_uuid) {
        this.location_uuid = location_uuid;
    }

    public String getInd_name() {
        return ind_name;
    }

    public void setInd_name(String ind_name) {
        this.ind_name = ind_name;
    }


    public String getFever_days() {
        return fever_days == null ? "" : String.valueOf(fever_days);
    }

    public void setFever_days(String fever_days) {
        if (fever_days == null) this.fever_days = null;
        else
            try {
                this.fever_days = Integer.valueOf(fever_days);
            } catch (NumberFormatException e) {
            }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getFw_name() {
        return fw_name;
    }

    public void setFw_name(String fw_name) {
        this.fw_name = fw_name;
    }

    public String getCompno() {
        return compno;
    }

    public void setCompno(String compno) {
        this.compno = compno;
    }

    //SPINNERS
    public void setFever_treat(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            fever_treat = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            fever_treat = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }


    public void patternSkipper(View view) {

        if (view != null) {

            if(hypertension == null || hypertension!=1) {
                hypertension_dur = null;
                hypertension_trt = null;
            }

            if (diabetes == null || diabetes !=1) {
                diabetes_trt = null;
                diabetes_dur = null;
            }

            if (heart == null || heart !=1) {
                heart_trt = null;
                heart_dur = null;
            }

            if (stroke == null || stroke !=1) {
                stroke_trt = null;
                stroke_dur = null;
            }

            if (sickle == null || sickle !=1) {
                sickle_trt = null;
                sickle_dur = null;
            }

            if (asthma == null || asthma !=1) {
                asthma_trt = null;
                asthma_dur = null;
            }

            if (epilepsy == null || epilepsy !=1) {
                epilepsy_trt = null;
                epilepsy_dur = null;
            }

            if (fever == null || fever !=1) {
                fever_treat = null;
                fever_days = null;
            }

        notifyPropertyChanged(BR._all);
        }
    }
}
