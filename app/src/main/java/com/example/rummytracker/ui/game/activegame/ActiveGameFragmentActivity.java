package com.example.rummytracker.ui.game.activegame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.rummytracker.R;
import com.example.rummytracker.ui.game.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActiveGameFragmentActivity extends FragmentActivity{
    ActiveGameViewModel activeGameViewModel;
    Button endRoundButton;
    TextView roundNumberView;
    ArrayList<Player> players;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_active_game);

        players = getIntent().getParcelableArrayListExtra("selectedPlayers");
        activeGameViewModel = new ViewModelProvider(this).get(ActiveGameViewModel.class);
        activeGameViewModel.setPlayerCount(players.size());


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.active_player_1, ActivePlayerFragment.newInstance(0, players.get(0)), "activePlayer1");
        transaction.add(R.id.active_player_2, ActivePlayerFragment.newInstance(1, players.get(1)), "activePlayer2");
        if(players.size() > 2){
            transaction.add(R.id.active_player_3, ActivePlayerFragment.newInstance(2, players.get(2)), "activePlayer3");
        }
        if(players.size() > 3){
            transaction.add(R.id.active_player_4, ActivePlayerFragment.newInstance(3, players.get(3)), "activePlayers4");
        }
        transaction.commit();

        endRoundButton = findViewById(R.id.end_round_button);
        roundNumberView = findViewById(R.id.round_number_text_view);

        endRoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextRound();
            }
        });

        activeGameViewModel.getLiveRoundNumber().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                updateRoundNumber();
            }
        });

    }

    private void updateRoundNumber(){
        roundNumberView.setText("Round: " + activeGameViewModel.getRoundNumber());
    }

    private void nextRound(){
        if(activeGameViewModel.isScoreSubmitted()) {
            int[] totalScores = activeGameViewModel.getScoreArray();
            int[] roundScores = activeGameViewModel.getRoundScoreArray();
            for(int i = 0; i < totalScores.length; i++){
                totalScores[i] = totalScores[i] + roundScores[i];
            }
            activeGameViewModel.setScoreArray(totalScores);
            if(!checkWinCondition(totalScores)) {
                activeGameViewModel.resetRoundScoreArray();
                activeGameViewModel.incrementRoundNumber();
                activeGameViewModel.shiftDealer();
            }
        }
    }

    private boolean checkWinCondition(int[] scores){
        int winIndex = -1;
        int topScore = Integer.MIN_VALUE;
        for(int i = 0; i < scores.length; i++){
            if(scores[i] >= 500 && scores[i] > topScore){
                winIndex = i;
                topScore = scores[i];
            }else if(scores[i] >= 500 && scores[i] == topScore){
                winIndex = -1;
            }
        }
        if(winIndex == -1){ return false; }
        activeGameViewModel.setIsWinner(winIndex);
        Bundle args = new Bundle();
        if(players.get(winIndex).isGuest()){
            args.putString("winner name", players.get(winIndex).getUsername());
        }else {
            args.putString("winner name", players.get(winIndex).getName());
        }

        ArrayList<PlayerScorePair> playerScoreList = new ArrayList<>(scores.length);
        PlayerScorePair tempPlayer;
        for(int i = 0; i < scores.length; i++){
            if(players.get(winIndex).isGuest()){
                tempPlayer = new PlayerScorePair(players.get(i).getUsername(), scores[i]);
            }else {
                tempPlayer = new PlayerScorePair(players.get(i).getName(), scores[i]);
            }
            playerScoreList.add(tempPlayer);
        }
        Collections.sort(playerScoreList, Collections.reverseOrder());
        args.putParcelableArrayList("playerScoreList", playerScoreList);

        DialogFragment winnerDialog = new WinnerDialogFragment(this);
        winnerDialog.setArguments(args);
        winnerDialog.show(getSupportFragmentManager(), "winnerDialog");
        //Toast.makeText(this, "The winner is: " + players.get(winIndex).getName(), Toast.LENGTH_LONG).show();
        return true;
    }

}
