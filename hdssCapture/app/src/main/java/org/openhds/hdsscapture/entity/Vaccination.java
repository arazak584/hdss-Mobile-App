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
import org.openhds.hdsscapture.BR;
import org.openhds.hdsscapture.entity.subqueries.KeyValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "vaccination")
public class Vaccination extends BaseObservable {

    @SerializedName("individual_uuid")
    @Expose
    @NotNull
    @ColumnInfo(name = "individual_uuid")
    @PrimaryKey
    public String individual_uuid;

    @Expose
    public String uuid;

    @Expose
    public Date insertDate;

    @Expose
    public Date editDate;

    @Expose
    public String socialgroup_uuid;

    @Expose
    public String location_uuid;

    @Expose
    public Date dob;

    @Expose
    public Date bcg;

    @Expose
    public Date opv0;

    @Expose
    public Date opv1;

    @Expose
    public Date opv2;

    @Expose
    public Date opv3;

    @Expose
    public Date dpt_hepb_hib1;

    @Expose
    public Date dpt_hepb_hib2;

    @Expose
    public Date dpt_hepb_hib3;

    @Expose
    public Date pneumo1;

    @Expose
    public Date pneumo2;

    @Expose
    public Date pneumo3;

    @Expose
    public Date rota1;

    @Expose
    public Date rota2;

    @Expose
    public Date rota3;

    @Expose
    public Date ipv;

    @Expose
    public Date vitaminA6;

    @Expose
    public Date vitaminA12;

    @Expose
    public Date vitaminA18;

    @Expose
    public Date rtss6;

    @Expose
    public Date rtss7;

    @Expose
    public Date rtss9;

    @Expose
    public Date rtss18;

    @Expose
    public Date measles_rubella1;

    @Expose
    public Date measles_rubella2;

    @Expose
    public Date yellow_fever;

    @Expose
    public Date menA;

    @Expose
    public Date itn;

    @Expose
    public String fw_uuid;

    @Expose
    public Integer hcard;

    @Expose
    public Integer reason;

    @Expose
    public Integer onet;

    @Expose
    public Integer rea;

    @Expose
    public Integer hl;

    @Expose
    public Integer hod;

    @Expose
    public Integer hom;

    @Expose
    public Integer nhis;

    @Expose
    public Integer sbf;

    @Expose
    public Integer stm;

    @Expose
    public Integer sty;

    @Expose
    public Integer admission;

    @Expose
    public String reason_oth;

    @Expose
    public String rea_oth;

    @ColumnInfo(name = "complete")
    public Integer complete;

    @Expose
    public Integer bednet;//Does your household have a bednet?

    @Expose
    public Integer slpbednet;//Did the baby sleep under bednet last Night?

    @Expose
    public Integer chlbednet;//How many children sleep under the bednet?

    @Expose
    public String weight;//Weight of child at birth in Kg?

    @Expose
    public Integer scar;//Does the child have the BCG scar? (Observe)

    @Expose
    public Date admitDate;//Date of Admission

    @Expose
    public Integer fever;//In the last two weeks did the child report Fever

    @Expose
    public Integer fevertreat;//Did you seek for treatment

    @Expose
    public Integer diarrhoea;//In the last two weeks did the child report diarrhoea

    @Expose
    public Integer diarrhoeatreat;//Did you seek for treatment

    @Expose
    public Integer arti;//In the last two weeks did the child report ARTI

    @Expose
    public Integer artitreat;//Did you seek for treatment

    @Expose
    public Integer muac;//Mid-Upper Arm circumference MUAC
    @Expose
    public String sttime;

    @Expose
    public String edtime;

    public Vaccination(){}


    @Ignore
    public transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


    public String getInsertDate() {
        if (insertDate == null) return null;
        return f.format(insertDate);
    }

    public void setInsertDate(String insertDate) {
        try {
            this.insertDate = f.parse(insertDate);
        } catch (ParseException e) {
        }
    }

    public String getEditDate() {
        if (editDate == null) return null;
        return f.format(editDate);
    }

    public void setEditDate(String editDate) {
        try {
            this.editDate = f.parse(editDate);
        } catch (ParseException e) {
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

    @NotNull
    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(@NotNull String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSocialgroup_uuid() {
        return socialgroup_uuid;
    }

    public void setSocialgroup_uuid(String socialgroup_uuid) {
        this.socialgroup_uuid = socialgroup_uuid;
    }

    public String getLocation_uuid() {
        return location_uuid;
    }

    public void setLocation_uuid(String location_uuid) {
        this.location_uuid = location_uuid;
    }

    public String getDob() {
        if (dob == null) return null;
        return f.format(dob);
    }

    public void setDob(String dob) {
        try {
            this.dob = f.parse(dob);
        } catch (ParseException e) {
        }
    }

    public String getAdmitDate() {
        if (admitDate == null) return null;
        return f.format(admitDate);
    }

    public void setAdmitDate(String admitDate) {
        try {
            this.admitDate = f.parse(admitDate);
        } catch (ParseException e) {
        }
    }

    public String getBcg() {
        if (bcg == null) return null;
        return f.format(bcg);
    }

    public void setBcg(String bcg) {
        try {
            this.bcg = f.parse(bcg);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for opv0
    public String getOpv0() {
        if (opv0 == null) return null;
        return f.format(opv0);
    }

    public void setOpv0(String opv0) {
        try {
            this.opv0 = f.parse(opv0);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for opv1
    public String getOpv1() {
        if (opv1 == null) return null;
        return f.format(opv1);
    }

    public void setOpv1(String opv1) {
        try {
            this.opv1 = f.parse(opv1);
        } catch (ParseException e) {
        }
    }

    public String getOpv2() {
        if (opv2 == null) return null;
        return f.format(opv2);
    }

    public void setOpv2(String opv2) {
        try {
            this.opv2 = f.parse(opv2);
        } catch (ParseException e) {
        }
    }

    public String getOpv3() {
        if (opv3 == null) return null;
        return f.format(opv3);
    }

    public void setOpv3(String opv3) {
        try {
            this.opv3 = f.parse(opv3);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for dpt_hepb_hib1
    public String getDpt_hepb_hib1() {
        if (dpt_hepb_hib1 == null) return null;
        return f.format(dpt_hepb_hib1);
    }

    public void setDpt_hepb_hib1(String dpt_hepb_hib1) {
        try {
            this.dpt_hepb_hib1 = f.parse(dpt_hepb_hib1);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for dpt_hepb_hib2
    public String getDpt_hepb_hib2() {
        if (dpt_hepb_hib2 == null) return null;
        return f.format(dpt_hepb_hib2);
    }

    public void setDpt_hepb_hib2(String dpt_hepb_hib2) {
        try {
            this.dpt_hepb_hib2 = f.parse(dpt_hepb_hib2);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for dpt_hepb_hib3
    public String getDpt_hepb_hib3() {
        if (dpt_hepb_hib3 == null) return null;
        return f.format(dpt_hepb_hib3);
    }

    public void setDpt_hepb_hib3(String dpt_hepb_hib3) {
        try {
            this.dpt_hepb_hib3 = f.parse(dpt_hepb_hib3);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for pneumo1
    public String getPneumo1() {
        if (pneumo1 == null) return null;
        return f.format(pneumo1);
    }

    public void setPneumo1(String pneumo1) {
        try {
            this.pneumo1 = f.parse(pneumo1);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for pneumo2
    public String getPneumo2() {
        if (pneumo2 == null) return null;
        return f.format(pneumo2);
    }

    public void setPneumo2(String pneumo2) {
        try {
            this.pneumo2 = f.parse(pneumo2);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for pneumo3
    public String getPneumo3() {
        if (pneumo3 == null) return null;
        return f.format(pneumo3);
    }

    public void setPneumo3(String pneumo3) {
        try {
            this.pneumo3 = f.parse(pneumo3);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for rota1
    public String getRota1() {
        if (rota1 == null) return null;
        return f.format(rota1);
    }

    public void setRota1(String rota1) {
        try {
            this.rota1 = f.parse(rota1);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for rota2
    public String getRota2() {
        if (rota2 == null) return null;
        return f.format(rota2);
    }

    public void setRota2(String rota2) {
        try {
            this.rota2 = f.parse(rota2);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for rota3
    public String getRota3() {
        if (rota3 == null) return null;
        return f.format(rota3);
    }

    public void setRota3(String rota3) {
        try {
            this.rota3 = f.parse(rota3);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for ipv
    public String getIpv() {
        if (ipv == null) return null;
        return f.format(ipv);
    }

    public void setIpv(String ipv) {
        try {
            this.ipv = f.parse(ipv);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for vitaminA6
    public String getVitaminA6() {
        if (vitaminA6 == null) return null;
        return f.format(vitaminA6);
    }

    public void setVitaminA6(String vitaminA6) {
        try {
            this.vitaminA6 = f.parse(vitaminA6);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for vitaminA12
    public String getVitaminA12() {
        if (vitaminA12 == null) return null;
        return f.format(vitaminA12);
    }

    public void setVitaminA12(String vitaminA12) {
        try {
            this.vitaminA12 = f.parse(vitaminA12);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for vitaminA18
    public String getVitaminA18() {
        if (vitaminA18 == null) return null;
        return f.format(vitaminA18);
    }

    public void setVitaminA18(String vitaminA18) {
        try {
            this.vitaminA18 = f.parse(vitaminA18);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for rtss6
    public String getRtss6() {
        if (rtss6 == null) return null;
        return f.format(rtss6);
    }

    public void setRtss6(String rtss6) {
        try {
            this.rtss6 = f.parse(rtss6);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for rtss7
    public String getRtss7() {
        if (rtss7 == null) return null;
        return f.format(rtss7);
    }

    public void setRtss7(String rtss7) {
        try {
            this.rtss7 = f.parse(rtss7);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for rtss9
    public String getRtss9() {
        if (rtss9 == null) return null;
        return f.format(rtss9);
    }

    public void setRtss9(String rtss9) {
        try {
            this.rtss9 = f.parse(rtss9);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for rtss18
    public String getRtss18() {
        if (rtss18 == null) return null;
        return f.format(rtss18);
    }

    public void setRtss18(String rtss18) {
        try {
            this.rtss18 = f.parse(rtss18);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for measles_rubella1
    public String getMeasles_rubella1() {
        if (measles_rubella1 == null) return null;
        return f.format(measles_rubella1);
    }

    public void setMeasles_rubella1(String measles_rubella1) {
        try {
            this.measles_rubella1 = f.parse(measles_rubella1);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for measles_rubella2
    public String getMeasles_rubella2() {
        if (measles_rubella2 == null) return null;
        return f.format(measles_rubella2);
    }

    public void setMeasles_rubella2(String measles_rubella2) {
        try {
            this.measles_rubella2 = f.parse(measles_rubella2);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for yellow_fever
    public String getYellow_fever() {
        if (yellow_fever == null) return null;
        return f.format(yellow_fever);
    }

    public void setYellow_fever(String yellow_fever) {
        try {
            this.yellow_fever = f.parse(yellow_fever);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for menA
    public String getMenA() {
        if (menA == null) return null;
        return f.format(menA);
    }

    public void setMenA(String menA) {
        try {
            this.menA = f.parse(menA);
        } catch (ParseException e) {
        }
    }

    // Getter and Setter for itn
    public String getItn() {
        if (itn == null) return null;
        return f.format(itn);
    }

    public void setItn(String itn) {
        try {
            this.itn = f.parse(itn);
        } catch (ParseException e) {
        }
    }

    public Integer getHcard() {
        return hcard;
    }

    public void setHcard(Integer hcard) {
        this.hcard = hcard;
    }

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }

    public Integer getOnet() {
        return onet;
    }

    public void setOnet(Integer onet) {
        this.onet = onet;
    }

    public Integer getRea() {
        return rea;
    }

    public void setRea(Integer rea) {
        this.rea = rea;
    }

    public Integer getHl() {
        return hl;
    }

    public void setHl(Integer hl) {
        this.hl = hl;
    }

    public String getHod() {
        return hod == null ? "" : String.valueOf(hod);
    }

    public void setHod(String hod) {
        if (hod == null) this.hod = null;
        else
            try {
                this.hod = Integer.valueOf(hod);
            } catch (NumberFormatException e) {
            }
    }

    public String getHom() {
        return hom == null ? "" : String.valueOf(hom);
    }

    public void setHom(String hom) {
        if (hom == null) this.hom = null;
        else
            try {
                this.hom = Integer.valueOf(hom);
            } catch (NumberFormatException e) {
            }
    }

    public Integer getNhis() {
        return nhis;
    }

    public void setNhis(Integer nhis) {
        this.nhis = nhis;
    }

    public Integer getSbf() {
        return sbf;
    }

    public void setSbf(Integer sbf) {
        this.sbf = sbf;
    }

    public String getStm() {
        return stm == null ? "" : String.valueOf(stm);
    }

    public void setStm(String stm) {
        if (stm == null) this.stm = null;
        else
            try {
                this.stm = Integer.valueOf(stm);
            } catch (NumberFormatException e) {
            }
    }

    public String getSty() {
        return sty == null ? "" : String.valueOf(sty);
    }

    public void setSty(String sty) {
        if (sty == null) this.sty = null;
        else
            try {
                this.sty = Integer.valueOf(sty);
            } catch (NumberFormatException e) {
            }
    }

    public String getFw_uuid() {
        return fw_uuid;
    }

    public void setFw_uuid(String fw_uuid) {
        this.fw_uuid = fw_uuid;
    }

    public Integer getAdmission() {
        return admission;
    }

    public void setAdmission(Integer admission) {
        this.admission = admission;
    }

    public String getReason_oth() {
        return reason_oth;
    }

    public void setReason_oth(String reason_oth) {
        this.reason_oth = reason_oth;
    }

    public String getRea_oth() {
        return rea_oth;
    }

    public void setRea_oth(String rea_oth) {
        this.rea_oth = rea_oth;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public Integer getBednet() {
        return bednet;
    }

    public void setBednet(Integer bednet) {
        this.bednet = bednet;
    }

    public Integer getSlpbednet() {
        return slpbednet;
    }

    public void setSlpbednet(Integer slpbednet) {
        this.slpbednet = slpbednet;
    }

    public String getChlbednet() {
        return chlbednet == null ? "" : String.valueOf(chlbednet);
    }

    public void setChlbednet(String chlbednet) {
        if (chlbednet == null) this.chlbednet = null;
        else
            try {
                this.chlbednet = Integer.valueOf(chlbednet);
            } catch (NumberFormatException e) {
            }
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Integer getScar() {
        return scar;
    }

    public void setScar(Integer scar) {
        this.scar = scar;
    }

    public Integer getFever() {
        return fever;
    }

    public void setFever(Integer fever) {
        this.fever = fever;
    }

    public Integer getFevertreat() {
        return fevertreat;
    }

    public void setFevertreat(Integer fevertreat) {
        this.fevertreat = fevertreat;
    }

    public Integer getDiarrhoea() {
        return diarrhoea;
    }

    public void setDiarrhoea(Integer diarrhoea) {
        this.diarrhoea = diarrhoea;
    }

    public Integer getDiarrhoeatreat() {
        return diarrhoeatreat;
    }

    public void setDiarrhoeatreat(Integer diarrhoeatreat) {
        this.diarrhoeatreat = diarrhoeatreat;
    }

    public Integer getArti() {
        return arti;
    }

    public void setArti(Integer arti) {
        this.arti = arti;
    }

    public Integer getArtitreat() {
        return artitreat;
    }

    public void setArtitreat(Integer artitreat) {
        this.artitreat = artitreat;
    }

    public String getMuac() {
        return muac == null ? "" : String.valueOf(muac);
    }

    public void setMuac(String muac) {
        if (muac == null) this.muac = null;
        else
            try {
                this.muac = Integer.valueOf(muac);
            } catch (NumberFormatException e) {
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

    public void setHcard(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            hcard = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            hcard = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setReason(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            reason = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            reason = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setOnet(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            onet = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            onet = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    public void setRea(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            rea = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            rea = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setHl(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            hl = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            hl = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    public void setNhis(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            nhis = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            nhis = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    public void setSbf(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            sbf = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            sbf = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setAdmission(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            admission = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            admission = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setBednet(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            bednet = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            bednet = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setSlpbednet(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            slpbednet = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            slpbednet = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setScar(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            scar = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            scar = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    public void setFever(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            fever = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            fever = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setFevertreat(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            fevertreat = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            fevertreat = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    public void setDiarrhoea(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            diarrhoea = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            diarrhoea = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setDiarrhoeatreat(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            diarrhoeatreat = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            diarrhoeatreat = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    public void setArti(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            arti = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            arti = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }

    public void setArtitreat(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            artitreat = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            artitreat = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    private void patternSkipper(View view) {

        if (view != null) {

            if (fever == null || fever != AppConstants.YES)
                setFevertreat(null);
            if (diarrhoea == null || diarrhoea != AppConstants.YES)
                setDiarrhoeatreat(null);
            if (arti == null || arti != AppConstants.YES)
                setArtitreat(null);

            if (bednet == null || bednet != AppConstants.YES) {
                setChlbednet(null);
                setSlpbednet(null);
                setOnet(null);
            }

            notifyPropertyChanged(BR._all);
        }
    }

}
