package com.idpskuinfo.skuinfo;

import android.os.Parcel;
import android.os.Parcelable;

public class SkuInfo implements Parcelable {
    private String skucode;
    private String description;
    private String retail;
    private String skutipe;


    public SkuInfo(String skucode, String description, String retail, String skutipe) {
        this.skucode = skucode;
        this.description = description;
        this.retail = retail;
        this.skutipe = skutipe;
    }

    protected SkuInfo(Parcel in) {
        skucode = in.readString();
        description = in.readString();
        retail = in.readString();
        skutipe = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(skucode);
        dest.writeString(description);
        dest.writeString(retail);
        dest.writeString(skutipe);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SkuInfo> CREATOR = new Creator<SkuInfo>() {
        @Override
        public SkuInfo createFromParcel(Parcel in) {
            return new SkuInfo(in);
        }

        @Override
        public SkuInfo[] newArray(int size) {
            return new SkuInfo[size];
        }
    };

    public String getSkucode() {
        return skucode;
    }

    public void setSkucode(String skucode) {
        this.skucode = skucode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRetail() {
        return retail;
    }

    public void setRetail(String retail) {
        this.retail = retail;
    }

    public String getSkutipe() {
        return skutipe;
    }

    public void setSkutipe(String skutipe) {
        this.skutipe = skutipe;
    }
}
