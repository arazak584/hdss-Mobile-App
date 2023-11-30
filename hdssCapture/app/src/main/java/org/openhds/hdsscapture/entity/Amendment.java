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

@Entity(tableName = "amendment")
public class Amendment extends BaseObservable {

    @Expose
    @NotNull
    @PrimaryKey
    public String individual_uuid;

    @Expose
    public String uuid;
    @Expose
    public Date insertDate;
    @Expose
    public String orig_firstName;
    @Expose
    public String orig_lastName;
    @Expose
    public Integer orig_gender;
    @Expose
    public Date orig_dob;
    @Expose
    public String orig_otherName;
    @Expose
    public String orig_ghanacard;

    //Amendment
    @Expose
    public String repl_firstName;
    @Expose
    public String repl_lastName;
    @Expose
    public Integer repl_gender;
    @Expose
    public Date repl_dob;
    @Expose
    public String repl_otherName;
    @Expose
    public String repl_ghanacard;

    @Expose
    public Integer yn_firstName;
    @Expose
    public Integer yn_lastName;
    @Expose
    public Integer yn_gender;
    @Expose
    public Integer yn_dob;
    @Expose
    public Integer yn_otherName;
    @Expose
    public Integer yn_ghanacard;
    @Expose
    public Integer complete;
    @Expose
    public String fw_uuid;

    public Amendment(){}

    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Bindable
    @NotNull
    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(@NotNull String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    public String getInsertDate() {
        if (insertDate == null) return null;
        return f.format(insertDate);
    }

    public void setInsertDate(String insertDate) {
        try {
            this.insertDate = f.parse(insertDate);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }

    public String getOrig_dob() {
        if (orig_dob == null) return null;
        return f.format(orig_dob);
    }

    public void setOrig_dob(String orig_dob) {
        try {
            this.orig_dob = f.parse(orig_dob);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }

    public String getRepl_dob() {
        if (repl_dob == null) return null;
        return f.format(repl_dob);
    }

    public void setRepl_dob(String repl_dob) {
        try {
            this.repl_dob = f.parse(repl_dob);
        } catch (ParseException e) {
            System.out.println("Date Error " + e.getMessage());
        }
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOrig_firstName() {
        return orig_firstName;
    }

    public void setOrig_firstName(String orig_firstName) {
        this.orig_firstName = orig_firstName;
    }

    public String getOrig_lastName() {
        return orig_lastName;
    }

    public void setOrig_lastName(String orig_lastName) {
        this.orig_lastName = orig_lastName;
    }

    public Integer getOrig_gender() {
        return orig_gender;
    }

    public void setOrig_gender(Integer orig_gender) {
        this.orig_gender = orig_gender;
    }

    public String getOrig_otherName() {
        return orig_otherName;
    }

    public void setOrig_otherName(String orig_otherName) {
        this.orig_otherName = orig_otherName;
    }

    public String getOrig_ghanacard() {
        return orig_ghanacard;
    }

    public void setOrig_ghanacard(String orig_ghanacard) {
        this.orig_ghanacard = orig_ghanacard;
    }

    public String getRepl_firstName() {
        return repl_firstName;
    }

    public void setRepl_firstName(String repl_firstName) {
        this.repl_firstName = repl_firstName;
    }

    public String getRepl_lastName() {
        return repl_lastName;
    }

    public void setRepl_lastName(String repl_lastName) {
        this.repl_lastName = repl_lastName;
    }

    public Integer getRepl_gender() {
        return repl_gender;
    }

    public void setRepl_gender(Integer repl_gender) {
        this.repl_gender = repl_gender;
    }

    public String getRepl_otherName() {
        return repl_otherName;
    }

    public void setRepl_otherName(String repl_otherName) {
        this.repl_otherName = repl_otherName;
    }

    public String getRepl_ghanacard() {
        return repl_ghanacard;
    }

    public void setRepl_ghanacard(String repl_ghanacard) {
        this.repl_ghanacard = repl_ghanacard;
    }

    public void setYn_firstName(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            yn_firstName = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setYn_lastName(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            yn_lastName = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setYn_otherName(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            yn_otherName = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setYn_ghanacard(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            yn_ghanacard = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setYn_gender(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            yn_gender = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setYn_dob(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            yn_dob = Integer.parseInt(TAG);
            patternSkipper(view);
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

    //SPINNERS ENTITY Gender
    public void setGenders(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            orig_gender = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            orig_gender = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    public void setGender(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            repl_gender = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            repl_gender = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    private void patternSkipper(View view) {

        if (view != null) {

            notifyPropertyChanged(BR._all);
        }

    }
}
