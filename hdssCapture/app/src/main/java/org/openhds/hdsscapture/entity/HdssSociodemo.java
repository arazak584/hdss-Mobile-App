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

@Entity(tableName = "sociodemo",
        indices = {
                @Index(value = {"uuid"}, unique = true),
                @Index(value = {"individual_uuid", "complete"}),
                @Index(value = {"socialgroup_uuid"}, unique = true),
                @Index(value = {"socialgroup_uuid", "complete"}),
                @Index(value = {"location_uuid"}),
                @Index(value = {"individual_uuid"}),
                @Index(value = {"fw_uuid"}),
                @Index(value = {"complete"})
        })
public class HdssSociodemo extends BaseObservable {

    @Ignore
    private transient final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Expose
    @NotNull
    @PrimaryKey
    public String socialgroup_uuid;

    @Expose
    public String uuid;

    @Expose
    public String visit_uuid;

    @Expose
    public String individual_uuid;

    @Expose
    public String fw_uuid;

    @Expose
    public String location_uuid;

    @Expose
    public Date formcompldate;

    @Expose
    public Date insertDate;

    @Expose
    public Integer complete;

    @Expose
    public Integer form_comments_yn;

    @Expose
    public String form_comments_txt;


//////////////////////////////////////////////////////////////////////////
    @Expose
    public Date sd_obsstdat;

    @Expose
    public Integer marital_scorres;
    @Expose
    public Integer marital_age;
    @Expose
    public Integer religion_scorres;
    @Expose
    public String religion_spfy_scorres;
    @Expose
    public Integer cethnic;
    @Expose
    public String othr_trb_spfy_cethnic;
    @Expose
    public String nth_trb_spfy_cethnic;
    @Expose
    public Integer head_hh_fcorres;
    @Expose
    public String head_hh_spfy_fcorres;
    @Expose
    public Integer house_occ_tot_fcorres;
    @Expose
    public Integer house_occ_lt5_fcorres;
    @Expose
    public Integer house_occ_ge5_fcorres;
    @Expose
    public Integer h2o_fcorres;
    @Expose
    public String h2o_spfy_fcorres;
    @Expose
    public Integer h2o_dist_fcorres;
    @Expose
    public Integer h2o_hours_fcorres;
    @Expose
    public Integer h2o_mins_fcorres;
    @Expose
    public Integer h2o_prep_fcorres;
    @Expose
    public Integer h2o_prep_spfy_fcorres_1;
    @Expose
    public Integer h2o_prep_spfy_fcorres_2;
    @Expose
    public Integer h2o_prep_spfy_fcorres_3;
    @Expose
    public Integer h2o_prep_spfy_fcorres_4;
    @Expose
    public Integer h2o_prep_spfy_fcorres_5;
    @Expose
    public Integer toilet_fcorres;
    @Expose
    public String toilet_spfy_fcorres;
    @Expose
    public Integer toilet_loc_fcorres;
    @Expose
    public String toilet_loc_spfy_fcorres;
    @Expose
    public Integer toilet_share_fcorres;
    @Expose
    public Integer toilet_share_num_fcorres;
    @Expose
    public Integer ext_wall_fcorres;
    @Expose
    public String ext_wall_spfy_fcorres;
    @Expose
    public Integer floor_fcorres;
    @Expose
    public String floor_spfy_fcorres;
    @Expose
    public Integer roof_fcorres;
    @Expose
    public String roof_spfy_fcorres;
    @Expose
    public Integer electricity_fcorres;
    @Expose
    public Integer solar_fcorres;
    @Expose
    public Integer internet_fcorres;
    @Expose
    public Integer landline_fcorres;
    @Expose
    public Integer mobile_fcorres;
    @Expose
    public Integer mobile_num_fcorres;
    @Expose
    public Integer mobile_access_fcorres;
    @Expose
    public Integer radio_fcorres;
    @Expose
    public Integer radio_num_fcorres;
    @Expose
    public Integer tv_fcorres;
    @Expose
    public Integer tv_num_fcorres;
    @Expose
    public Integer fridge_fcorres;
    @Expose
    public Integer fridge_num_fcorres;
    @Expose
    public Integer computer_fcorres;
    @Expose
    public Integer computer_num_fcorres;
    @Expose
    public Integer watch_fcorres;
    @Expose
    public Integer watch_num_fcorres;
    @Expose
    public Integer bike_fcorres;
    @Expose
    public Integer bike_num_fcorres;
    @Expose
    public Integer motorcycle_fcorres;
    @Expose
    public Integer motorcycle_num_fcorres;
    @Expose
    public Integer car_fcorres;
    @Expose
    public Integer car_num_fcorres;
    @Expose
    public Integer boat_fcorres;
    @Expose
    public Integer boat_num_fcorres;
    @Expose
    public Integer cart_fcorres;
    @Expose
    public Integer cart_num_fcorres;
    @Expose
    public Integer plough_fcorres;
    @Expose
    public Integer plough_num_fcorres;
    @Expose
    public Integer foam_matt_fcorres;
    @Expose
    public Integer foam_matt_num_fcorres;
    @Expose
    public Integer straw_matt_fcorres;
    @Expose
    public Integer straw_matt_num_fcorres;
    @Expose
    public Integer spring_matt_fcorres;
    @Expose
    public Integer spring_matt_num_fcorres;
    @Expose
    public Integer sofa_fcorres;
    @Expose
    public Integer sofa_num_fcorres;
    @Expose
    public Integer lantern_fcorres;
    @Expose
    public Integer lantern_num_fcorres;
    @Expose
    public Integer sew_fcorres;
    @Expose
    public Integer sew_num_fcorres;
    @Expose
    public Integer wash_fcorres;
    @Expose
    public Integer wash_num_fcorres;
    @Expose
    public Integer blender_fcorres;
    @Expose
    public Integer blender_num_fcorres;
    @Expose
    public Integer mosquito_net_fcorres;
    @Expose
    public Integer mosquito_net_num_fcorres;
    @Expose
    public Integer tricycles_fcorres;
    @Expose
    public Integer tricycles_num_fcorres;
    @Expose
    public Integer tables_fcorres;
    @Expose
    public Integer tables_num_fcorres;
    @Expose
    public Integer cabinets_fcorres;
    @Expose
    public Integer cabinets_num_fcorres;
    @Expose
    public Integer sat_dish_fcorres;
    @Expose
    public Integer sat_dish_num_fcorres;
    @Expose
    public Integer dvd_cd_fcorres;
    @Expose
    public Integer dvd_cd_num_fcorres;
    @Expose
    public Integer aircon_fcorres;
    @Expose
    public Integer aircon_num_fcorres;
    @Expose
    public Integer tractor_fcorres;
    @Expose
    public Integer tractor_num_fcorres;
    @Expose
    public Integer own_rent_scorres;
    @Expose
    public String own_rent_spfy_scorres;
    @Expose
    public Integer house_rooms_fcorres;
    @Expose
    public Integer house_room_child_fcorres;
    @Expose
    public Integer land_fcorres;
    @Expose
    public Integer land_use_fcorres_1;
    @Expose
    public Integer land_use_fcorres_2;
    @Expose
    public Integer land_use_fcorres_3;
    @Expose
    public Integer land_use_fcorres_4;
    @Expose
    public Integer land_use_fcorres_5;
    @Expose
    public Integer land_use_fcorres_88;
    @Expose
    public String land_use_spfy_fcorres_88;
    @Expose
    public Integer livestock_fcorres;
    @Expose
    public Integer cattle_fcorres;
    @Expose
    public Integer cattle_num_fcorres;
    @Expose
    public Integer goat_fcorres;
    @Expose
    public Integer goat_num_fcorres;
    @Expose
    public Integer sheep_fcorres;
    @Expose
    public Integer sheep_num_fcorres;
    @Expose
    public Integer poultry_fcorres;
    @Expose
    public Integer poultry_num_fcorres;
    @Expose
    public Integer pig_fcorres;
    @Expose
    public Integer pig_num_fcorres;
    @Expose
    public Integer donkey_fcorres;
    @Expose
    public Integer donkey_num_fcorres;
    @Expose
    public Integer horse_fcorres;
    @Expose
    public Integer horse_num_fcorres;
    @Expose
    public Integer animal_othr_fcorres;
    @Expose
    public String animal_othr_spfy_fcorres;
    @Expose
    public Integer animal_othr_num_fcorres;
    @Expose
    public Integer job_scorres;
    @Expose
    public String job_salary_spfy_scorres;
    @Expose
    public String job_smbus_spfy_scorres;
    @Expose
    public String job_busown_spfy_scorres;
    @Expose
    public String job_skilled_spfy_scorres;
    @Expose
    public String job_unskilled_spfy_scorres;
    @Expose
    public String job_othr_spfy_scorres;
    @Expose
    public Integer ptr_scorres;
    @Expose
    public String ptr_salary_spfy_scorres;
    @Expose
    public String ptr_smbus_spfy_scorres;
    @Expose
    public String ptr_busown_spfy_scorres;
    @Expose
    public String ptr_skilled_spfy_scorres;
    @Expose
    public String ptr_unskilled_spfy_scorres;
    @Expose
    public String ptr_othr_spfy_scorres;
    @Expose
    public Integer stove_fcorres;
    @Expose
    public String stove_spfy_fcorres;
    @Expose
    public Integer stove_fuel_fcorres_1;
    @Expose
    public Integer stove_fuel_fcorres_2;
    @Expose
    public Integer stove_fuel_fcorres_3;
    @Expose
    public Integer stove_fuel_fcorres_4;
    @Expose
    public Integer stove_fuel_fcorres_5;
    @Expose
    public Integer stove_fuel_fcorres_6;
    @Expose
    public Integer stove_fuel_fcorres_7;
    @Expose
    public Integer stove_fuel_fcorres_8;
    @Expose
    public Integer stove_fuel_fcorres_9;
    @Expose
    public Integer stove_fuel_fcorres_10;
    @Expose
    public Integer stove_fuel_fcorres_11;
    @Expose
    public Integer stove_fuel_fcorres_12;
    @Expose
    public Integer stove_fuel_fcorres_13;
    @Expose
    public Integer stove_fuel_fcorres_14;
    @Expose
    public Integer stove_fuel_fcorres_88;
    @Expose
    public String stove_fuel_spfy_fcorres_88;
    @Expose
    public Integer cooking_inside_fcorres;
    @Expose
    public Integer cooking_room_fcorres;
    @Expose
    public Integer cooking_loc_fcorres;
    @Expose
    public Integer cooking_vent_fcorres;
    @Expose
    public Integer smoke_oecoccur;
    @Expose
    public Integer smoke_in_oecdosfrq;
    @Expose
    public Integer smoke_hhold_oecoccur;
    @Expose
    public Integer smoke_hhold_in_oecdosfrq;
    @Expose
    public Integer chew_oecoccur;
    @Expose
    public Integer chew_bnut_oecoccur;
    @Expose
    public Integer drink_oecoccur;
    @Expose
    public String sttime;

    @Expose
    public String edtime;

    @Expose
    public String comment;

    @Expose
    public Integer status = 0;
    @Expose
    public String supervisor;
    @Expose
    public Date approveDate;
    @Expose
    public Integer pets;
    @Expose
    public Integer dogs;
    @Expose
    public Integer guinea_pigs;
    @Expose
    public Integer cats;
    @Expose
    public Integer fish;
    @Expose
    public Integer birds;
    @Expose
    public Integer rabbits;
    @Expose
    public Integer reptiles;
    @Expose
    public Integer pet_other;
    @Expose
    public String pet_other_spfy;
    @Expose
    public Integer pet_vac;
    @Expose
    public Integer id0001;
    @Expose
    public Integer id0002;
    @Expose
    public Integer id0003;
    @Expose
    public Integer id0004;
    @Expose
    public Integer id0005;
    @Expose
    public Integer id0006;
    @Expose
    public Integer id0006_1;
    @Expose
    public Integer id0007;
    @Expose
    public Integer id0007_1;
    @Expose
    public Integer id0008;
    @Expose
    public Integer id0008_1;
    @Expose
    public Integer  id0009;
    @Expose
    public Integer  id0009_1;
    @Expose
    public Integer  id0010;
    @Expose
    public Integer  id0010_1;
    @Expose
    public Integer  id0011;
    @Expose
    public Integer  id0011_1;
    @Expose
    public Integer  id0012;
    @Expose
    public Integer  id0012_1;
    @Expose
    public Integer  id0013;
    @Expose
    public Integer  id0013_1;
    @Expose
    public Integer  id0014;
    @Expose
    public Integer  id0014_1;
    @Expose
    public Integer  id0015;
    @Expose
    public Integer  id0015_1;
    @Expose
    public Integer  id0016;
    @Expose
    public Integer  id0016_1;
    @Expose
    public Integer  id0017;
    @Expose
    public Integer  id0017_1;
    @Expose
    public Integer  id0018;
    @Expose
    public Integer  id0018_1;
    @Expose
    public Integer  id0019;
    @Expose
    public Integer  id0019_1;
    @Expose
    public String  id0021;
    public Date updatedAt;

    public HdssSociodemo() {
    }

    @NotNull
    public String getIndividual_uuid() {
        return individual_uuid;
    }

    public void setIndividual_uuid(@NotNull String individual_uuid) {
        this.individual_uuid = individual_uuid;
    }

    @Ignore
    private transient final SimpleDateFormat g = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    @Bindable
    public String getUpdatedAt() {
        if (updatedAt == null) return null;
        return g.format(updatedAt);
    }

    public void setUpdatedAt(String updatedAt) {
        try {
            this.updatedAt = g.parse(updatedAt);
        } catch (ParseException e) {
            System.out.println("updatedAt Date Error " + e.getMessage());
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
        if (approveDate == null) return null;
        return g.format(approveDate);
    }

    public void setApproveDate(String approveDate) {
        try {
            this.approveDate = g.parse(approveDate);
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    @Bindable
    public String getHouse_occ_tot_fcorres() {
        return house_occ_tot_fcorres == null ? "" : String.valueOf(house_occ_tot_fcorres);
    }

    public void setHouse_occ_tot_fcorres(String house_occ_tot_fcorres) {
        if (house_occ_tot_fcorres == null) this.house_occ_tot_fcorres = null;
        else
            try {
                this.house_occ_tot_fcorres = Integer.valueOf(house_occ_tot_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getHouse_occ_lt5_fcorres() {
        return house_occ_lt5_fcorres == null ? "" : String.valueOf(house_occ_lt5_fcorres);
    }

    public void setHouse_occ_lt5_fcorres(String house_occ_lt5_fcorres) {
        this.house_occ_ge5_fcorres = null;
        if (house_occ_lt5_fcorres == null) this.house_occ_lt5_fcorres = null;
        else
            try {
                this.house_occ_lt5_fcorres = Integer.valueOf(house_occ_lt5_fcorres);
                if (this.house_occ_tot_fcorres != null && this.house_occ_lt5_fcorres != null) {
                    this.house_occ_ge5_fcorres = this.house_occ_tot_fcorres - this.house_occ_lt5_fcorres;
                }
            } catch (NumberFormatException e) {
            }
        notifyPropertyChanged(BR.house_occ_ge5_fcorres);
    }

    @Bindable
    public String getHouse_occ_ge5_fcorres() {
        return house_occ_ge5_fcorres == null ? "" : String.valueOf(house_occ_ge5_fcorres);
    }

    public void setHouse_occ_ge5_fcorres(String house_occ_ge5_fcorres) {
        if (house_occ_ge5_fcorres == null) this.house_occ_ge5_fcorres = null;
        else
            try {
                this.house_occ_ge5_fcorres = Integer.valueOf(house_occ_ge5_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getH2o_hours_fcorres() {
        return h2o_hours_fcorres == null ? "" : String.valueOf(h2o_hours_fcorres);
    }

    public void setH2o_hours_fcorres(String h2o_hours_fcorres) {
        if (h2o_hours_fcorres == null) this.h2o_hours_fcorres = null;
        else
            try {
                this.h2o_hours_fcorres = Integer.valueOf(h2o_hours_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getH2o_mins_fcorres() {
        return h2o_mins_fcorres == null ? "" : String.valueOf(h2o_mins_fcorres);
    }

    public void setH2o_mins_fcorres(String h2o_mins_fcorres) {
        if (h2o_mins_fcorres == null) this.h2o_mins_fcorres = null;
        else
            try {
                this.h2o_mins_fcorres = Integer.valueOf(h2o_mins_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getMobile_num_fcorres() {
        return mobile_num_fcorres == null ? "" : String.valueOf(mobile_num_fcorres);
    }

    public void setMobile_num_fcorres(String mobile_num_fcorres) {
        if (mobile_num_fcorres == null) this.mobile_num_fcorres = null;
        else
            try {
                this.mobile_num_fcorres = Integer.valueOf(mobile_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getRadio_num_fcorres() {
        return radio_num_fcorres == null ? "" : String.valueOf(radio_num_fcorres);
    }

    public void setRadio_num_fcorres(String radio_num_fcorres) {
        if (radio_num_fcorres == null) this.radio_num_fcorres = null;
        else
            try {
                this.radio_num_fcorres = Integer.valueOf(radio_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getTv_num_fcorres() {
        return tv_num_fcorres == null ? "" : String.valueOf(tv_num_fcorres);
    }

    public void setTv_num_fcorres(String tv_num_fcorres) {
        if (tv_num_fcorres == null) this.tv_num_fcorres = null;
        else
            try {
                this.tv_num_fcorres = Integer.valueOf(tv_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getFridge_num_fcorres() {
        return fridge_num_fcorres == null ? "" : String.valueOf(fridge_num_fcorres);
    }

    public void setFridge_num_fcorres(String fridge_num_fcorres) {
        if (fridge_num_fcorres == null) this.fridge_num_fcorres = null;
        else
            try {
                this.fridge_num_fcorres = Integer.valueOf(fridge_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getComputer_num_fcorres() {
        return computer_num_fcorres == null ? "" : String.valueOf(computer_num_fcorres);
    }

    public void setComputer_num_fcorres(String computer_num_fcorres) {
        if (computer_num_fcorres == null) this.computer_num_fcorres = null;
        else
            try {
                this.computer_num_fcorres = Integer.valueOf(computer_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getWatch_num_fcorres() {
        return watch_num_fcorres == null ? "" : String.valueOf(watch_num_fcorres);
    }

    public void setWatch_num_fcorres(String watch_num_fcorres) {
        if (watch_num_fcorres == null) this.watch_num_fcorres = null;
        else
            try {
                this.watch_num_fcorres = Integer.valueOf(watch_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getBike_num_fcorres() {
        return bike_num_fcorres == null ? "" : String.valueOf(bike_num_fcorres);
    }

    public void setBike_num_fcorres(String bike_num_fcorres) {
        if (bike_num_fcorres == null) this.bike_num_fcorres = null;
        else
            try {
                this.bike_num_fcorres = Integer.valueOf(bike_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getMotorcycle_num_fcorres() {
        return motorcycle_num_fcorres == null ? "" : String.valueOf(motorcycle_num_fcorres);
    }

    public void setMotorcycle_num_fcorres(String motorcycle_num_fcorres) {
        if (motorcycle_num_fcorres == null) this.motorcycle_num_fcorres = null;
        else
            try {
                this.motorcycle_num_fcorres = Integer.valueOf(motorcycle_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getCar_num_fcorres() {
        return car_num_fcorres == null ? "" : String.valueOf(car_num_fcorres);
    }

    public void setCar_num_fcorres(String car_num_fcorres) {
        if (car_num_fcorres == null) this.car_num_fcorres = null;
        else
            try {
                this.car_num_fcorres = Integer.valueOf(car_num_fcorres);
                notifyPropertyChanged(BR.car_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getBoat_num_fcorres() {
        return boat_num_fcorres == null ? "" : String.valueOf(boat_num_fcorres);
    }

    public void setBoat_num_fcorres(String boat_num_fcorres) {
        if (boat_num_fcorres == null) this.boat_num_fcorres = null;
        else
            try {
                this.boat_num_fcorres = Integer.valueOf(boat_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getCart_num_fcorres() {
        return cart_num_fcorres == null ? "" : String.valueOf(cart_num_fcorres);
    }

    public void setCart_num_fcorres(String cart_num_fcorres) {
        if (cart_num_fcorres == null) this.cart_num_fcorres = null;
        else
            try {
                this.cart_num_fcorres = Integer.valueOf(cart_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getPlough_num_fcorres() {
        return plough_num_fcorres == null ? "" : String.valueOf(plough_num_fcorres);
    }

    public void setPlough_num_fcorres(String plough_num_fcorres) {
        if (plough_num_fcorres == null) this.plough_num_fcorres = null;
        else
            try {
                this.plough_num_fcorres = Integer.valueOf(plough_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getFoam_matt_num_fcorres() {
        return foam_matt_num_fcorres == null ? "" : String.valueOf(foam_matt_num_fcorres);
    }

    public void setFoam_matt_num_fcorres(String foam_matt_num_fcorres) {
        if (foam_matt_num_fcorres == null) this.foam_matt_num_fcorres = null;
        else
            try {
                this.foam_matt_num_fcorres = Integer.valueOf(foam_matt_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getStraw_matt_num_fcorres() {
        return straw_matt_num_fcorres == null ? "" : String.valueOf(straw_matt_num_fcorres);
    }

    public void setStraw_matt_num_fcorres(String straw_matt_num_fcorres) {
        if (straw_matt_num_fcorres == null) this.straw_matt_num_fcorres = null;
        else
            try {
                this.straw_matt_num_fcorres = Integer.valueOf(straw_matt_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getSpring_matt_num_fcorres() {
        return spring_matt_num_fcorres == null ? "" : String.valueOf(spring_matt_num_fcorres);
    }

    public void setSpring_matt_num_fcorres(String spring_matt_num_fcorres) {
        if (spring_matt_num_fcorres == null) this.spring_matt_num_fcorres = null;
        else
            try {
                this.spring_matt_num_fcorres = Integer.valueOf(spring_matt_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getSofa_num_fcorres() {
        return sofa_num_fcorres == null ? "" : String.valueOf(sofa_num_fcorres);
    }

    public void setSofa_num_fcorres(String sofa_num_fcorres) {
        if (sofa_num_fcorres == null) this.sofa_num_fcorres = null;
        else
            try {
                this.sofa_num_fcorres = Integer.valueOf(sofa_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    public String getLantern_num_fcorres() {
        return lantern_num_fcorres == null ? "" : String.valueOf(lantern_num_fcorres);
    }

    public void setLantern_num_fcorres(String lantern_num_fcorres) {
        if (lantern_num_fcorres == null) this.lantern_num_fcorres = null;
        else
            try {
                this.lantern_num_fcorres = Integer.valueOf(lantern_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getSew_num_fcorres() {
        return sew_num_fcorres == null ? "" : String.valueOf(sew_num_fcorres);
    }

    public void setSew_num_fcorres(String sew_num_fcorres) {
        if (sew_num_fcorres == null) this.sew_num_fcorres = null;
        else
            try {
                this.sew_num_fcorres = Integer.valueOf(sew_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getWash_num_fcorres() {
        return wash_num_fcorres == null ? "" : String.valueOf(wash_num_fcorres);
    }

    public void setWash_num_fcorres(String wash_num_fcorres) {
        if (wash_num_fcorres == null) this.wash_num_fcorres = null;
        else
            try {
                this.wash_num_fcorres = Integer.valueOf(wash_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getBlender_num_fcorres() {
        return blender_num_fcorres == null ? "" : String.valueOf(blender_num_fcorres);
    }

    public void setBlender_num_fcorres(String blender_num_fcorres) {
        if (blender_num_fcorres == null) this.blender_num_fcorres = null;
        else
            try {
                this.blender_num_fcorres = Integer.valueOf(blender_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getMosquito_net_num_fcorres() {
        return mosquito_net_num_fcorres == null ? "" : String.valueOf(mosquito_net_num_fcorres);
    }

    public void setMosquito_net_num_fcorres(String mosquito_net_num_fcorres) {
        if (mosquito_net_num_fcorres == null) this.mosquito_net_num_fcorres = null;
        else
            try {
                this.mosquito_net_num_fcorres = Integer.valueOf(mosquito_net_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getTricycles_num_fcorres() {
        return tricycles_num_fcorres == null ? "" : String.valueOf(tricycles_num_fcorres);
    }

    public void setTricycles_num_fcorres(String tricycles_num_fcorres) {
        if (tricycles_num_fcorres == null) this.tricycles_num_fcorres = null;
        else
            try {
                this.tricycles_num_fcorres = Integer.valueOf(tricycles_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getTables_num_fcorres() {
        return tables_num_fcorres == null ? "" : String.valueOf(tables_num_fcorres);
    }

    public void setTables_num_fcorres(String tables_num_fcorres) {
        if (tables_num_fcorres == null) this.tables_num_fcorres = null;
        else
            try {
                this.tables_num_fcorres = Integer.valueOf(tables_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getCabinets_num_fcorres() {
        return cabinets_num_fcorres == null ? "" : String.valueOf(cabinets_num_fcorres);
    }

    public void setCabinets_num_fcorres(String cabinets_num_fcorres) {
        if (cabinets_num_fcorres == null) this.cabinets_num_fcorres = null;
        else
            try {
                this.cabinets_num_fcorres = Integer.valueOf(cabinets_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getSat_dish_num_fcorres() {
        return sat_dish_num_fcorres == null ? "" : String.valueOf(sat_dish_num_fcorres);
    }

    public void setSat_dish_num_fcorres(String sat_dish_num_fcorres) {
        if (sat_dish_num_fcorres == null) this.sat_dish_num_fcorres = null;
        else
            try {
                this.sat_dish_num_fcorres = Integer.valueOf(sat_dish_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    public String getDvd_cd_num_fcorres() {
        return dvd_cd_num_fcorres == null ? "" : String.valueOf(dvd_cd_num_fcorres);
    }

    public void setDvd_cd_num_fcorres(String dvd_cd_num_fcorres) {
        if (dvd_cd_num_fcorres == null) this.dvd_cd_num_fcorres = null;
        else
            try {
                this.dvd_cd_num_fcorres = Integer.valueOf(dvd_cd_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    public String getAircon_num_fcorres() {
        return aircon_num_fcorres == null ? "" : String.valueOf(aircon_num_fcorres);
    }

    public void setAircon_num_fcorres(String aircon_num_fcorres) {
        if (aircon_num_fcorres == null) this.aircon_num_fcorres = null;
        else
            try {
                this.aircon_num_fcorres = Integer.valueOf(aircon_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getTractor_num_fcorres() {
        return tractor_num_fcorres == null ? "" : String.valueOf(tractor_num_fcorres);
    }

    public void setTractor_num_fcorres(String tractor_num_fcorres) {
        if (tractor_num_fcorres == null) this.tractor_num_fcorres = null;
        else
            try {
                this.tractor_num_fcorres = Integer.valueOf(tractor_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getHouse_rooms_fcorres() {
        return house_rooms_fcorres == null ? "" : String.valueOf(house_rooms_fcorres);
    }

    public void setHouse_rooms_fcorres(String house_rooms_fcorres) {
        if (house_rooms_fcorres == null) this.house_rooms_fcorres = null;
        else
            try {
                this.house_rooms_fcorres = Integer.valueOf(house_rooms_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getCattle_num_fcorres() {
        return cattle_num_fcorres == null ? "" : String.valueOf(cattle_num_fcorres);
    }

    public void setCattle_num_fcorres(String cattle_num_fcorres) {
        if (cattle_num_fcorres == null) this.cattle_num_fcorres = null;
        else
            try {
                this.cattle_num_fcorres = Integer.valueOf(cattle_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getGoat_num_fcorres() {
        return goat_num_fcorres == null ? "" : String.valueOf(goat_num_fcorres);
    }

    public void setGoat_num_fcorres(String goat_num_fcorres) {
        if (goat_num_fcorres == null) this.goat_num_fcorres = null;
        else
            try {
                this.goat_num_fcorres = Integer.valueOf(goat_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getSheep_num_fcorres() {
        return sheep_num_fcorres == null ? "" : String.valueOf(sheep_num_fcorres);
    }

    public void setSheep_num_fcorres(String sheep_num_fcorres) {
        if (sheep_num_fcorres == null) this.sheep_num_fcorres = null;
        else
            try {
                this.sheep_num_fcorres = Integer.valueOf(sheep_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getPoultry_num_fcorres() {
        return poultry_num_fcorres == null ? "" : String.valueOf(poultry_num_fcorres);
    }

    public void setPoultry_num_fcorres(String poultry_num_fcorres) {
        if (poultry_num_fcorres == null) this.poultry_num_fcorres = null;
        else
            try {
                this.poultry_num_fcorres = Integer.valueOf(poultry_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getPig_num_fcorres() {
        return pig_num_fcorres == null ? "" : String.valueOf(pig_num_fcorres);
    }

    public void setPig_num_fcorres(String pig_num_fcorres) {
        if (pig_num_fcorres == null) this.pig_num_fcorres = null;
        else
            try {
                this.pig_num_fcorres = Integer.valueOf(pig_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getDonkey_num_fcorres() {
        return donkey_num_fcorres == null ? "" : String.valueOf(donkey_num_fcorres);
    }

    public void setDonkey_num_fcorres(String donkey_num_fcorres) {
        if (donkey_num_fcorres == null) this.donkey_num_fcorres = null;
        else
            try {
                this.donkey_num_fcorres = Integer.valueOf(donkey_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getHorse_num_fcorres() {
        return horse_num_fcorres == null ? "" : String.valueOf(horse_num_fcorres);
    }

    public void setHorse_num_fcorres(String horse_num_fcorres) {
        if (horse_num_fcorres == null) this.horse_num_fcorres = null;
        else
            try {
                this.horse_num_fcorres = Integer.valueOf(horse_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }

    @Bindable
    public String getAnimal_othr_num_fcorres() {
        return animal_othr_num_fcorres == null ? "" : String.valueOf(animal_othr_num_fcorres);
    }

    public void setAnimal_othr_num_fcorres(String animal_othr_num_fcorres) {
        if (animal_othr_num_fcorres == null) this.animal_othr_num_fcorres = null;
        else
            try {
                this.animal_othr_num_fcorres = Integer.valueOf(animal_othr_num_fcorres);
            } catch (NumberFormatException e) {
            }
    }


///////////////////////////////////////////////////////////////////////////////////////////////////


    public void setMnh03_form_comments_yn(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            form_comments_yn = Integer.parseInt(TAG);
            form_comments_txt = null;
            notifyPropertyChanged(BR._all);
        }
    }

    public String getMarital_age() {
        return marital_age == null ? "" : String.valueOf(marital_age);
    }

    public void setMarital_age(String marital_age) {
        if (marital_age == null) this.marital_age = null;
        else
            try {
                this.marital_age = Integer.valueOf(marital_age);
            } catch (NumberFormatException e) {
            }
    }


    public void setH2o_prep_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            h2o_prep_fcorres = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }


    public void setH2o_prep_spfy_fcorres_1(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            h2o_prep_spfy_fcorres_1 = Integer.parseInt(TAG);
        }
    }

    public void setH2o_prep_spfy_fcorres_2(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            h2o_prep_spfy_fcorres_2 = Integer.parseInt(TAG);
        }
    }

    public void setH2o_prep_spfy_fcorres_3(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            h2o_prep_spfy_fcorres_3 = Integer.parseInt(TAG);
        }
    }

    public void setH2o_prep_spfy_fcorres_4(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            h2o_prep_spfy_fcorres_4 = Integer.parseInt(TAG);
        }
    }

    public void setH2o_prep_spfy_fcorres_5(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            h2o_prep_spfy_fcorres_5 = Integer.parseInt(TAG);
        }
    }

    public void setToilet_share_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            toilet_share_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }


    public void setFloor_fcorres(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            floor_fcorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            floor_fcorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);

        }
        patternSkipper(view);

    }


    public void setElectricity_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            electricity_fcorres = Integer.parseInt(TAG);
        }
    }

    public void setSolar_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            solar_fcorres = Integer.parseInt(TAG);
        }
    }

    public void setInternet_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            internet_fcorres = Integer.parseInt(TAG);
        }
    }

    public void setLandline_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            landline_fcorres = Integer.parseInt(TAG);
        }
    }

    public void setMobile_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mobile_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }


    public void setRadio_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            radio_fcorres = Integer.parseInt(TAG);


            patternSkipper(view);
        }
    }

    public void setTv_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            tv_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setFridge_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            fridge_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setComputer_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            computer_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setWatch_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            watch_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setBike_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            bike_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setMotorcycle_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            motorcycle_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setCar_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            car_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setBoat_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            boat_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setCart_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            cart_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setPlough_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            plough_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setFoam_matt_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            foam_matt_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setStraw_matt_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            straw_matt_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setSpring_matt_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            spring_matt_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setSofa_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            sofa_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setLantern_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            lantern_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setSew_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            sew_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setWash_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            wash_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setBlender_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            blender_fcorres = Integer.parseInt(TAG);
            patternSkipper(view);
        }

    }

    public void setMosquito_net_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            mosquito_net_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setTricycles_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            tricycles_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setTables_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            tables_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setCabinets_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            cabinets_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setSat_dish_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            sat_dish_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setDvd_cd_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            dvd_cd_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setAircon_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            aircon_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setTractor_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            tractor_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }


    public void setLand_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            land_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }


    public void setLand_use_fcorres_1(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            land_use_fcorres_1 = Integer.parseInt(TAG);
        }
    }

    public void setLand_use_fcorres_2(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            land_use_fcorres_2 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setLand_use_fcorres_3(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            land_use_fcorres_3 = Integer.parseInt(TAG);
        }
    }

    public void setLand_use_fcorres_4(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            land_use_fcorres_4 = Integer.parseInt(TAG);
        }
    }

    public void setLand_use_fcorres_5(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            land_use_fcorres_5 = Integer.parseInt(TAG);
        }
    }

    public void setLand_use_fcorres_88(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            land_use_fcorres_88 = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setLivestock_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            livestock_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setCattle_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            cattle_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setGoat_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            goat_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setSheep_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            sheep_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setPoultry_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            poultry_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setPig_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            pig_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setDonkey_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            donkey_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setHorse_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            horse_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setAnimal_othr_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            animal_othr_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }


    public void setCooking_inside_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            cooking_inside_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }


    public void setCooking_vent_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            cooking_vent_fcorres = Integer.parseInt(TAG);
        }
    }

    public void setSmoke_oecoccur(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            smoke_oecoccur = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setSmoke_hhold_oecoccur(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            smoke_hhold_oecoccur = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setChew_oecoccur(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            chew_oecoccur = Integer.parseInt(TAG);
        }
    }

    public void setDrink_oecoccur(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            drink_oecoccur = Integer.parseInt(TAG);
        }
    }

    public void setPets(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            pets = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setDogs(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            dogs = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setGuinea_pigs(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            guinea_pigs = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setCats(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            cats = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setFish(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            fish = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setBirds(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            birds = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setRabbits(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            rabbits = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setReptiles(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            reptiles = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setPet_other(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            pet_other = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    //SPINNER STARTS HERE


    public void setMarital_scorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            marital_scorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            marital_scorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);

        }
        patternSkipper(view);
    }


    public void setReligion_scorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            religion_scorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            religion_scorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);

        }
        patternSkipper(view);

    }


    public void setCethnic(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            cethnic = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            cethnic = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);

        }
        patternSkipper(view);

    }


    public void setHead_hh_fcorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            head_hh_fcorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            head_hh_fcorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);

        }
        patternSkipper(view);

    }


    public void setH2o_fcorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            h2o_fcorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            h2o_fcorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);

        }
        patternSkipper(view);

    }


    @Bindable
    public String getH2o_dist_fcorres() {
        if (h2o_dist_fcorres == null) return "";
        return "" + h2o_dist_fcorres;
    }


    public void setH2o_dist_fcorres(String h2o_dist_fcorres) {
        h2o_hours_fcorres = null;
        h2o_mins_fcorres = null;
        try {
            this.h2o_dist_fcorres = Integer.parseInt(h2o_dist_fcorres);
            if (this.h2o_dist_fcorres != null && this.h2o_dist_fcorres < 5) {
                this.h2o_hours_fcorres = 0;
                this.h2o_mins_fcorres = 0;
            }
        } catch (NumberFormatException e) {

        }
        notifyPropertyChanged(BR.h2o_hours_fcorres);
        notifyPropertyChanged(BR.h2o_mins_fcorres);
    }


    public void setToilet_fcorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            toilet_fcorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            toilet_fcorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);

        }
        patternSkipper(view);

    }

    public void setToilet_loc_fcorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            toilet_loc_fcorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            toilet_loc_fcorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);

        }
        patternSkipper(view);

    }


    public void setToilet_share_num_fcorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            toilet_share_num_fcorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            toilet_share_num_fcorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    public void setExt_wall_fcorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            ext_wall_fcorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            ext_wall_fcorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);

        }
        patternSkipper(view);

    }


    public void setRoof_fcorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            roof_fcorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            roof_fcorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);

        }
        patternSkipper(view);

    }

    public void setPet_vac(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            pet_vac = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            pet_vac = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);

        }
        patternSkipper(view);

    }


    public void setMobile_access_fcorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            mobile_access_fcorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            mobile_access_fcorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }


    public void setOwn_rent_scorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            own_rent_scorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            own_rent_scorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);

        }
        patternSkipper(view);

    }

    public void setJob_scorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            job_scorres = AppConstants.NOSELECT;
            patternSkipper(view);
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            job_scorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }


    public void setPtr_scorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            ptr_scorres = AppConstants.NOSELECT;
            patternSkipper(view);
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            ptr_scorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }
        patternSkipper(view);

    }


    public void setStove_fcorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            stove_fcorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            stove_fcorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);

        }
        patternSkipper(view);

    }

    public void setStove_fuel_fcorres_1(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_1 = Integer.parseInt(TAG);
        }
    }

    public void setStove_fuel_fcorres_2(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_2 = Integer.parseInt(TAG);
        }
    }

    public void setStove_fuel_fcorres_3(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_3 = Integer.parseInt(TAG);
        }
    }

    public void setStove_fuel_fcorres_4(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_4 = Integer.parseInt(TAG);
        }
    }

    public void setStove_fuel_fcorres_5(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_5 = Integer.parseInt(TAG);
        }
    }

    public void setStove_fuel_fcorres_6(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_6 = Integer.parseInt(TAG);
        }
    }

    public void setStove_fuel_fcorres_7(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_7 = Integer.parseInt(TAG);
        }
    }

    public void setStove_fuel_fcorres_8(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_8 = Integer.parseInt(TAG);
        }
    }

    public void setStove_fuel_fcorres_9(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_9 = Integer.parseInt(TAG);
        }
    }

    public void setStove_fuel_fcorres_10(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_10 = Integer.parseInt(TAG);
        }
    }

    public void setStove_fuel_fcorres_11(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_11 = Integer.parseInt(TAG);
        }
    }


    public void setStove_fuel_fcorres_12(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_12 = Integer.parseInt(TAG);
        }
    }

    public void setStove_fuel_fcorres_13(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_13 = Integer.parseInt(TAG);
        }
    }

    public void setStove_fuel_fcorres_14(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_14 = Integer.parseInt(TAG);
        }
    }

    public void setStove_fuel_fcorres_88(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            stove_fuel_fcorres_88 = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setCooking_loc_fcorres(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            cooking_loc_fcorres = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            cooking_loc_fcorres = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

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


    public void setSmoke_in_oecdosfrq(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            smoke_in_oecdosfrq = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            smoke_in_oecdosfrq = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }


    public void setSmoke_hhold_in_oecdosfrq(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            smoke_hhold_in_oecdosfrq = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            smoke_hhold_in_oecdosfrq = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
        }

    }

    public void setId0001(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0001 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0001 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0002(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0002 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0002 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0003(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0003 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0003 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0004(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0004 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0004 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0005(AdapterView<?> parent, View view, int position, long id) {

        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0005 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0005 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0006(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0006 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0006_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0006_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0006_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0007(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0007 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0007_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0007_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0007_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0008(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0008 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0008_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0008_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0008_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0009(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0009 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0009_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0009_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0009_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0010(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0010 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0010_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0010_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0010_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0011(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0011 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0011_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0011_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0011_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0012(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0012 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0012_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0012_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0012_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0013(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0013 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0013_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0013_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0013_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0014(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0014 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0014_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0014_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0014_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0015(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0015 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0015_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0015_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0015_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0016(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0016 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0016_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0016_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0016_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0017(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0017 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0017_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0017_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0017_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0018(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0018 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0018_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0018_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0018_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }

    public void setId0019(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            id0019 = Integer.parseInt(TAG);
            patternSkipper(view);
        }
    }

    public void setId0019_1(AdapterView<?> parent, View view, int position, long id) {
        if (position != parent.getSelectedItemPosition()) {
            parent.setSelection(position);
        }
        if (position == 0) {
            id0019_1 = AppConstants.NOSELECT;
        } else {
            final KeyValuePair kv = (KeyValuePair) parent.getItemAtPosition(position);
            id0019_1 = kv.codeValue;
            ((TextView) parent.getChildAt(0)).setTextColor(Color.MAGENTA);
            ((TextView) parent.getChildAt(0)).setTextSize(20);
            patternSkipper(view);
        }
    }




    public void setHouse_room_child_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            house_room_child_fcorres = Integer.parseInt(TAG);
        }
    }

    public void setCooking_room_fcorres(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            cooking_room_fcorres = Integer.parseInt(TAG);

            patternSkipper(view);
        }
    }

    public void setChew_bnut_oecoccur(RadioGroup view, int checkedId) {
        if (checkedId != view.getCheckedRadioButtonId()) {
            view.check(checkedId);
        }
        if (view.findViewById(checkedId) != null) {
            final String TAG = "" + view.findViewById(checkedId).getTag();
            chew_bnut_oecoccur = Integer.parseInt(TAG);
        }
    }



    @Bindable
    public String getSd_obsstdat() {
        if (sd_obsstdat == null) return "";
        return f.format(sd_obsstdat);
    }

    public void setSd_obsstdat(String sd_obsstdat) {
        if (sd_obsstdat == null) this.sd_obsstdat = null;
        else
            try {
                this.sd_obsstdat = (sd_obsstdat == null) ? null : f.parse(sd_obsstdat);
            } catch (ParseException e) {

            }
    }

    @Bindable
    public String getFormcompldate() {
        if (formcompldate == null) return "";
        return f.format(formcompldate);
    }

    public void setFormcompldate(String formcompldate) {
        if (formcompldate == null) this.formcompldate = null;
        else
            try {
                this.formcompldate = (formcompldate == null) ? null : f.parse(formcompldate);
            } catch (ParseException e) {

            }
    }

    private void patternSkipper(View view) {

        if (view != null) {


            if (marital_scorres == null || marital_scorres != 1)
                setMarital_age(null);

            if (religion_scorres == null || religion_scorres != AppConstants.OTHER_SPECIFY)
                religion_spfy_scorres = null;

            if (cethnic == null || cethnic != AppConstants.OTHER_SPECIFY)
                othr_trb_spfy_cethnic = null;

            if (head_hh_fcorres == null || head_hh_fcorres != AppConstants.OTHER_SPECIFY)
                head_hh_spfy_fcorres = null;

            if (h2o_fcorres == null || h2o_fcorres != AppConstants.OTHER_SPECIFY)
                h2o_spfy_fcorres = null;

            if (form_comments_yn == null || form_comments_yn !=AppConstants.YES)
                form_comments_txt = null;

            if (h2o_prep_fcorres == null || h2o_prep_fcorres != AppConstants.YES) {

                h2o_prep_spfy_fcorres_1 = null;
                h2o_prep_spfy_fcorres_2 = null;
                h2o_prep_spfy_fcorres_3 = null;
                h2o_prep_spfy_fcorres_4 = null;
                h2o_prep_spfy_fcorres_5 = null;

            }

            if (toilet_fcorres == null || toilet_fcorres != AppConstants.OTHER_SPECIFY)
                toilet_spfy_fcorres = null;

            if (toilet_fcorres == null || toilet_fcorres == 12) {
                toilet_loc_fcorres = null;
                toilet_share_fcorres = null;
            }

            if (toilet_loc_fcorres == null || toilet_loc_fcorres != AppConstants.OTHER_SPECIFY)
                toilet_loc_spfy_fcorres = null;

            if (toilet_share_fcorres == null || toilet_share_fcorres != AppConstants.YES)
                toilet_share_num_fcorres = AppConstants.NOSELECT;

            if (ext_wall_fcorres == null || ext_wall_fcorres != AppConstants.OTHER_SPECIFY)
                ext_wall_spfy_fcorres = null;

            if (floor_fcorres == null || floor_fcorres != AppConstants.OTHER_SPECIFY)
                floor_spfy_fcorres = null;

            if (roof_fcorres == null || roof_fcorres != AppConstants.OTHER_SPECIFY)
                roof_spfy_fcorres = null;

            if (mobile_fcorres == null || mobile_fcorres != AppConstants.YES) {
                setMobile_num_fcorres(null);
            }

            if (radio_fcorres == null || radio_fcorres != AppConstants.YES)
                setRadio_num_fcorres(null);

            if (tv_fcorres == null || tv_fcorres != AppConstants.YES)
                setTv_num_fcorres(null);

            if (fridge_fcorres == null || fridge_fcorres != AppConstants.YES)
                setFridge_num_fcorres(null);

            if (computer_fcorres == null || computer_fcorres != AppConstants.YES)
                setComputer_num_fcorres(null);

            if (watch_fcorres == null || watch_fcorres != AppConstants.YES)
                setWatch_num_fcorres(null);

            if (bike_fcorres == null || bike_fcorres != AppConstants.YES)
                setBike_num_fcorres(null);

            if (motorcycle_fcorres == null || motorcycle_fcorres != AppConstants.YES)
                setMotorcycle_num_fcorres(null);

            if (car_fcorres == null || car_fcorres != AppConstants.YES)
                setCar_num_fcorres(null);

            if (boat_fcorres == null || boat_fcorres != AppConstants.YES)
                setBoat_num_fcorres(null);

            if (cart_fcorres == null || cart_fcorres != AppConstants.YES)
                setCart_num_fcorres(null);

            if (plough_fcorres == null || plough_fcorres != AppConstants.YES)
                setPlough_num_fcorres(null);

            if (foam_matt_fcorres == null || foam_matt_fcorres != AppConstants.YES)
                setFoam_matt_num_fcorres(null);

            if (straw_matt_fcorres == null || straw_matt_fcorres != AppConstants.YES)
                setStraw_matt_num_fcorres(null);

            if (spring_matt_fcorres == null || spring_matt_fcorres != AppConstants.YES)
                setSpring_matt_num_fcorres(null);

            if (sofa_fcorres == null || sofa_fcorres != AppConstants.YES)
                setSofa_num_fcorres(null);

            if (lantern_fcorres == null || lantern_fcorres != AppConstants.YES)
                setLantern_num_fcorres(null);

            if (sew_fcorres == null || sew_fcorres != AppConstants.YES)
                setSew_num_fcorres(null);

            if (wash_fcorres == null || wash_fcorres != AppConstants.YES)
                setWash_num_fcorres(null);

            if (blender_fcorres == null || blender_fcorres != AppConstants.YES)
                setBlender_num_fcorres(null);

            if (mosquito_net_fcorres == null || mosquito_net_fcorres != AppConstants.YES)
                setMosquito_net_num_fcorres(null);

            if (tricycles_fcorres == null || tricycles_fcorres != AppConstants.YES)
                setTricycles_num_fcorres(null);

            if (tables_fcorres == null || tables_fcorres != AppConstants.YES)
                setTables_num_fcorres(null);

            if (cabinets_fcorres == null || cabinets_fcorres != AppConstants.YES)
                setCabinets_num_fcorres(null);

            if (sat_dish_fcorres == null || sat_dish_fcorres != AppConstants.YES)
                setSat_dish_num_fcorres(null);

            if (dvd_cd_fcorres == null || dvd_cd_fcorres != AppConstants.YES)
                setDvd_cd_num_fcorres(null);

            if (aircon_fcorres == null || aircon_fcorres != AppConstants.YES)
                setAircon_num_fcorres(null);

            if (tractor_fcorres == null || tractor_fcorres != AppConstants.YES)
                setTractor_num_fcorres(null);

            if (own_rent_scorres == null || own_rent_scorres != AppConstants.OTHER_SPECIFY)
                own_rent_spfy_scorres = null;

            if (land_fcorres == null || land_fcorres != AppConstants.YES) {
                land_use_fcorres_1 = null;
                land_use_fcorres_2 = null;
                land_use_fcorres_3 = null;
                land_use_fcorres_4 = null;
                land_use_fcorres_5 = null;
                land_use_fcorres_88 = null;

            }

            if (land_use_fcorres_88 == null || land_use_fcorres_88 != AppConstants.OTHER_SPECIFY)
                land_use_spfy_fcorres_88 = null;

            if (livestock_fcorres == null || livestock_fcorres != AppConstants.YES) {

                cattle_fcorres = null;
                goat_fcorres = null;
                sheep_fcorres = null;
                poultry_fcorres = null;
                pig_fcorres = null;
                donkey_fcorres = null;
                horse_fcorres = null;
                animal_othr_fcorres = null;
            }

            if (stove_fcorres == null || stove_fcorres == AppConstants.LPG || stove_fcorres ==AppConstants.non) {

                stove_fuel_fcorres_1 = null;
                stove_fuel_fcorres_2 = null;
                stove_fuel_fcorres_3 = null;
                stove_fuel_fcorres_4 = null;
                stove_fuel_fcorres_5 = null;
                stove_fuel_fcorres_6 = null;
                stove_fuel_fcorres_7 = null;
                stove_fuel_fcorres_8 = null;
                stove_fuel_fcorres_9 = null;
                stove_fuel_fcorres_10 = null;
                stove_fuel_fcorres_11 = null;
                stove_fuel_fcorres_12 = null;
                stove_fuel_fcorres_13 = null;
                stove_fuel_fcorres_14 = null;
                stove_fuel_fcorres_88 = null;
            }

            if (cattle_fcorres == null || cattle_fcorres != AppConstants.YES)
                setCattle_num_fcorres(null);

            if (goat_fcorres == null || goat_fcorres != AppConstants.YES)
                setGoat_num_fcorres(null);

            if (sheep_fcorres == null || sheep_fcorres != AppConstants.YES)
                setSheep_num_fcorres(null);

            if (poultry_fcorres == null || poultry_fcorres != AppConstants.YES)
                setPoultry_num_fcorres(null);

            if (pig_fcorres == null || pig_fcorres != AppConstants.YES)
                setPig_num_fcorres(null);

            if (donkey_fcorres == null || donkey_fcorres != AppConstants.YES)
                setDonkey_num_fcorres(null);

            if (horse_fcorres == null || horse_fcorres != AppConstants.YES)
                setHorse_num_fcorres(null);

            if (animal_othr_fcorres == null || animal_othr_fcorres != AppConstants.YES) {
                animal_othr_spfy_fcorres = null;
                setAnimal_othr_num_fcorres(null);
            }

            if (job_scorres == null || job_scorres != AppConstants.OTHER_SPECIFY)
                job_othr_spfy_scorres = null;

            if (ptr_scorres == null || ptr_scorres != AppConstants.OTHER_SPECIFY)
                ptr_othr_spfy_scorres = null;

            if (stove_fcorres == null || stove_fcorres != AppConstants.OTHER_SPECIFY)
                stove_spfy_fcorres = null;

            if (stove_fuel_fcorres_88 == null || stove_fuel_fcorres_88 != AppConstants.YES)
                stove_fuel_spfy_fcorres_88 = null;

            if (cooking_inside_fcorres == null || cooking_inside_fcorres != AppConstants.YES)
                cooking_room_fcorres = null;

            if (smoke_oecoccur == null || smoke_oecoccur != AppConstants.YES)
                smoke_in_oecdosfrq = null;

            if (smoke_hhold_oecoccur == null || smoke_hhold_oecoccur != AppConstants.YES)
                smoke_hhold_in_oecdosfrq = null;

            if (id0001 == null || id0001 == 3)
                id0002 = null;

            if (id0003 == null || id0003 != AppConstants.YES)
                id0004 = null;

            if (id0003 == null || (id0003 != AppConstants.NO && id0003 != 3))
                id0005 = null;

            if(id0006 == null || id0006 != 1)
                id0006_1 = null;
            if(id0007 == null || id0007 != 1)
                id0007_1 = null;
            if(id0008 == null || id0008 != 1)
                id0008_1 = null;

            if(id0009 == null || id0009 != 1)
                id0009_1 = null;
            if(id0010 == null || id0010 != 1)
                id0010_1 = null;
            if(id0011 == null || id0011 != 1)
                id0011_1 = null;
            if(id0012 == null || id0012 != 1)
                id0012_1 = null;
            if(id0013 == null || id0013 != 1)
                id0013_1 = null;
            if(id0014 == null || id0014 != 1)
                id0014_1 = null;
            if(id0015 == null || id0015 != 1)
                id0015_1 = null;
            if(id0016 == null || id0016 != 1)
                id0016_1 = null;
            if(id0017 == null || id0017 != 1)
                id0017_1 = null;
            if(id0018 == null || id0018 != 1)
                id0018_1 = null;
            if(id0019 == null || id0019 != 1)
                id0019_1 = null;


            notifyPropertyChanged(BR._all);
        }

    }

}//end
