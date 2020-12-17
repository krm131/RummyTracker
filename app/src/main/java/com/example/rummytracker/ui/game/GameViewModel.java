package com.example.rummytracker.ui.game;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameViewModel extends ViewModel {

    protected final static int LOBBY_OK = 0;
    protected final static int LOBBY_INVALID = 1;

    private MutableLiveData<Player[]> playerArray;
    private MutableLiveData<boolean[]> playerStatus;

    public GameViewModel(){
        playerArray = new MutableLiveData<>(new Player[4]);
        boolean b[] = new boolean[4];
        Arrays.fill(b, true);
        playerStatus = new MutableLiveData<>(b);

    }

    public void setPlayerArray(Player[] p){
        playerArray.setValue(p);
        Log.d("GAME_VIEW_MODEL", "Trigger set value listeners");
    }

    public Player[] getPlayerArray(){ return playerArray.getValue(); }

    public void setPlayerStatus(int index, boolean b) {
        boolean[] newStatus = playerStatus.getValue();
        newStatus[index] = b;
        playerStatus.setValue(newStatus);
    }

    public boolean[] getPlayerStatusArray() { return playerStatus.getValue(); }

    public LiveData<Player[]> getLivePlayerList() { return playerArray; }


}
