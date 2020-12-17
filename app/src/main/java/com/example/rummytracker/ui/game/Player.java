package com.example.rummytracker.ui.game;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

public class Player implements Parcelable {

    private boolean isGuest;
    private String username;
    private String name = null;
    private String userid = null;

    public Player (String username, boolean isGuest){
        this.username = username;
        this.isGuest = isGuest;
    }

    protected Player(Parcel in) {
        isGuest = in.readByte() != 0;
        username = in.readString();
    }

    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public void setUserid ( String s){
        userid = s;
    }

    public String getUserid(){
        return userid;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String n){ name = n;}

    public String getName() {return name;}


    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBoolean(isGuest);
        parcel.writeString(username);
    }
}
