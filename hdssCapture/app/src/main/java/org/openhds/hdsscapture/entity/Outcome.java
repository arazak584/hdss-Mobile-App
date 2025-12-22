package org.openhds.hdsscapture.entity;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
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

@Entity(tableName = "outcome",
        indices = {
                @Index(value = {"uuid"}, unique = true),
                @Index(value = {"childuuid"}),
                @Index(value = {"mother_uuid"}),
                @Index(value = {"complete"})
        })
public class Outcome extends BaseObservable {

    @SerializedName("uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "uuid")
    @PrimaryKey
    public String uuid;

    @Expose
    public String childuuid;

    @Expose
    public String mother_uuid;

    @Expose
    public String preg_uuid;

    @Expose
    public Integer type;

    @Expose
    public Integer complete;

    @Expose
    public Integer chd_weight;//Was the child weighed at birth?
    @Expose
    public Integer chd_size;//How much did the child weigh (estimated baby size)
    @Expose
    public String weig_hcard;//Record weight in kilograms from Health Card

    @Expose
    public Integer outcomeNumber;

    public Outcome(){}

    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


    @NotNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NotNull String uuid) {
        this.uuid = uuid;
    }

    public String getChilduuid() {
        return childuuid;
    }

    public void setChilduuid(String childuuid) {
        this.childuuid = childuuid;
    }

    public String getMother_uuid() {
        return mother_uuid;
    }

    public void setMother_uuid(String mother_uuid) {
        this.mother_uuid = mother_uuid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getComplete() {
        return complete;
    }

    public void setComplete(Integer complete) {
        this.complete = complete;
    }

    public String getPreg_uuid() {
        return preg_uuid;
    }

    public void setPreg_uuid(String preg_uuid) {
        this.preg_uuid = preg_uuid;
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
        return weig_hcard;
    }

    public void setWeig_hcard(String weig_hcard) {
        this.weig_hcard = weig_hcard;
    }

    //SPINNERS ENTITY
    public void setType(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            type = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            type = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            notifyPropertyChanged(BR._all);
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
        patternSkipper(view);

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

    private void patternSkipper(View view) {

        if (view != null) {

            if(chd_weight == null || chd_weight!=1){
                setWeig_hcard(null);
            }

            if(type == null || type!=1){
                setWeig_hcard(null);
                setChd_size(null);
                setChd_weight(null);
            }

            notifyPropertyChanged(BR._all);
        }
    }

}
