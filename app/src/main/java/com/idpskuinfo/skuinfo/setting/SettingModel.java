package com.idpskuinfo.skuinfo.setting;

import android.os.Parcel;
import android.os.Parcelable;

public class SettingModel implements Parcelable {
    String HostName;
    String Port;
    String UserName;
    String Password;
    String DateUpdate;

    public SettingModel() {

    }


    protected SettingModel(Parcel in) {
        HostName = in.readString();
        Port = in.readString();
        UserName = in.readString();
        Password = in.readString();
        DateUpdate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(HostName);
        dest.writeString(Port);
        dest.writeString(UserName);
        dest.writeString(Password);
        dest.writeString(DateUpdate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SettingModel> CREATOR = new Creator<SettingModel>() {
        @Override
        public SettingModel createFromParcel(Parcel in) {
            return new SettingModel(in);
        }

        @Override
        public SettingModel[] newArray(int size) {
            return new SettingModel[size];
        }
    };

    public String getHostName() {
        return HostName;
    }

    public void setHostName(String hostName) {
        HostName = hostName;
    }

    public String getPort() {
        return Port;
    }

    public void setPort(String port) {
        Port = port;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getDateUpdate() {
        return DateUpdate;
    }

    public void setDateUpdate(String dateUpdate) {
        DateUpdate = dateUpdate;
    }
}
