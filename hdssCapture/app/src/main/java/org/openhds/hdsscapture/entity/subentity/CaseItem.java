package org.openhds.hdsscapture.entity.subentity;

import android.os.Parcel;
import android.os.Parcelable;

public class CaseItem implements Parcelable {
    public CaseItem() {
    }

    public String individual_uuid;
    public String firstName;
    public String lastName;
    public String extId;
    public Integer age;
    public String dob;
    public String location_uuid;
    public String residency_uuid;
    public String socialgroup_uuid;
    public String visit_uuid;
    public Integer gender;

    protected CaseItem(Parcel in) {
        this.individual_uuid = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.extId = in.readString();
        this.age = in.readInt();
        this.dob = in.readString();
        this.location_uuid = in.readString();
        this.residency_uuid = in.readString();
        this.socialgroup_uuid = in.readString();
        this.visit_uuid = in.readString();
        this.gender = in.readInt();
    }

    public static final Creator<CaseItem> CREATOR = new Creator<CaseItem>() {
        @Override
        public CaseItem createFromParcel(Parcel in) {
            return new CaseItem(in);
        }

        @Override
        public CaseItem[] newArray(int size) {
            return new CaseItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.individual_uuid);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.extId);
        dest.writeInt(this.age);
        dest.writeSerializable(this.dob);
        dest.writeString(this.location_uuid);
        dest.writeString(this.residency_uuid);
        dest.writeString(this.socialgroup_uuid);
        dest.writeString(this.visit_uuid);
        dest.writeInt(this.gender);
    }

}
