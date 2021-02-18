package com.idpskuinfo.skuinfo;

import android.os.Parcel;
import android.os.Parcelable;

public class RateInfo implements Parcelable {
    private String currency;
    private String description;
    private String rateidr;

    public RateInfo(String currency, String description, String rateidr) {
        this.currency = currency;
        this.description = description;
        this.rateidr = rateidr;
    }

    protected RateInfo(Parcel in) {
        currency = in.readString();
        description = in.readString();
        rateidr = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(currency);
        dest.writeString(description);
        dest.writeString(rateidr);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RateInfo> CREATOR = new Creator<RateInfo>() {
        @Override
        public RateInfo createFromParcel(Parcel in) {
            return new RateInfo(in);
        }

        @Override
        public RateInfo[] newArray(int size) {
            return new RateInfo[size];
        }
    };

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRateidr() {
        return rateidr;
    }

    public void setRateidr(String rateidr) {
        this.rateidr = rateidr;
    }

    public static Creator<RateInfo> getCREATOR() {
        return CREATOR;
    }
}
