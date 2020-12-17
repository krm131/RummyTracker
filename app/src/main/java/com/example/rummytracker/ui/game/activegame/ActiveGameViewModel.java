package com.example.rummytracker.ui.game.activegame;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class ActiveGameViewModel extends ViewModel {

    private MutableLiveData<int[]> scoreArray;
    private MutableLiveData<int[]> roundScoreArray;
    private MutableLiveData<Integer> roundNumber;
    private int playerCount;
    private MutableLiveData<Integer> dealerIndex;
    private boolean scoreSubmitted = true;
    private MutableLiveData<Integer> isWinner;

    public ActiveGameViewModel(){
        Random random = new Random();
        roundNumber = new MutableLiveData<>(1);
        dealerIndex = new MutableLiveData<>(random.nextInt(4));
        isWinner = new MutableLiveData<>(-1);
    }

    public void setPlayerCount(int i) {
        playerCount = i;
        shiftDealer();
        scoreArray = new MutableLiveData<>(new int[i]);
        roundScoreArray = new MutableLiveData<>(new int[i]);
    }

    public MutableLiveData<Integer> getLiveDealerIndex(){
        return dealerIndex;
    }

    public MutableLiveData<int[]> getLiveScoreArray(){
        return scoreArray;
    }

    public MutableLiveData<Integer> getLiveRoundNumber(){
        return roundNumber;
    }

    public int[] getScoreArray() {
        return scoreArray.getValue();
    }

    public void setScoreArray(int[] scoreArray) {
        this.scoreArray.setValue(scoreArray);
    }

    public int[] getRoundScoreArray() {
        return roundScoreArray.getValue();
    }

    public boolean isScoreSubmitted() {
        return scoreSubmitted;
    }

    public void setScoreSubmitted(boolean scoresSubmitted) {
        this.scoreSubmitted = scoresSubmitted;
    }

    public int getDealerIndex(){ return dealerIndex.getValue(); }

    public int getPlayerCount() { return playerCount; }

    public int getRoundNumber() { return roundNumber.getValue(); }

    public void incrementRoundNumber() {
        roundNumber.setValue(roundNumber.getValue() + 1);
    }

    public void resetRoundNumber() {
        roundNumber.setValue(1);
    }

    public void shiftDealer() {
        dealerIndex.setValue((dealerIndex.getValue()+1) % playerCount);
    }

    public void updateRoundScore( int index, int score){
        int temp[] = roundScoreArray.getValue();
        temp[index] = score;
        roundScoreArray.setValue(temp);
    }

    public void resetRoundScoreArray() {
        roundScoreArray.setValue(new int[playerCount]);
    }

    public void setIsWinner(int i){
        isWinner.setValue(i);
    }

    public MutableLiveData<Integer> getLiveIsWinner(){
        return isWinner;
    }

}
