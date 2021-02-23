package com.idpskuinfo.skuinfo.data;

import android.os.Parcel;
import android.os.Parcelable;

public class SkuModel implements Parcelable {
    private String skucode;
    private String skudes;
    private String skuret;
    private String skutype;


    public SkuModel(){

    }

    public String getSkucode() {
        return skucode;
    }

    public void setSkucode(String skucode) {
        this.skucode = skucode;
    }

    public String getSkudes() {
        return skudes;
    }

    public void setSkudes(String skudes) {
        this.skudes = skudes;
    }

    public String getSkuret() {
        return skuret;
    }

    public void setSkuret(String skuret) {
        this.skuret = skuret;
    }

    public String getSkutype() {
        return skutype;
    }

    public void setSkutype(String skutype) {
        this.skutype = skutype;
    }

    protected SkuModel(Parcel in) {
        skucode = in.readString();
        skudes = in.readString();
        skuret = in.readString();
        skutype = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(skucode);
        dest.writeString(skudes);
        dest.writeString(skuret);
        dest.writeString(skutype);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SkuModel> CREATOR = new Creator<SkuModel>() {
        @Override
        public SkuModel createFromParcel(Parcel in) {
            return new SkuModel(in);
        }

        @Override
        public SkuModel[] newArray(int size) {
            return new SkuModel[size];
        }
    };
}
