package com.example.rummytracker.ui.game.activegame;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerScorePair implements Parcelable, Comparable<PlayerScorePair> {

    private String name;
    private int score;

    public PlayerScorePair(String n, int s){
        name = n;
        score = s;
    }

    protected PlayerScorePair(Parcel in) {
        name = in.readString();
        score = in.readInt();
    }

    public static final Creator<PlayerScorePair> CREATOR = new Creator<PlayerScorePair>() {
        @Override
        public PlayerScorePair createFromParcel(Parcel in) {
            return new PlayerScorePair(in);
        }

        @Override
        public PlayerScorePair[] newArray(int size) {
            return new PlayerScorePair[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(score);
    }

    public int getScore(){
        return score;
    }

    public String getName(){
        return name;
    }

    @Override
    public int compareTo(PlayerScorePair playerScorePair) {
        return score - playerScorePair.getScore();
    }
}
