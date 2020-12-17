package com.example.rummytracker.ui.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rummytracker.R;
import com.example.rummytracker.ui.game.activegame.ActiveGameFragmentActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NewGameFragment extends Fragment {
    GameViewModel gameViewModel;
    Button startGameButton;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        View root = inflater.inflate(R.layout.fragment_new_game, container, false);
        getChildFragmentManager().beginTransaction()
                .add(R.id.new_player_1, PlayerAddFragment.newInstance("NewPlayer1"), "PlayerAddFragment1")
                .add(R.id.new_player_2, PlayerAddFragment.newInstance("NewPlayer2"), "PlayerAddFragment2")
                .add(R.id.new_player_3, PlayerAddFragment.newInstance("NewPlayer3"), "PlayerAddFragment3")
                .add(R.id.new_player_4, PlayerAddFragment.newInstance("NewPlayer4"), "PlayerAddFragment4")
                .commit();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startGameButton = view.findViewById(R.id.start_game_button);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptGameStart();
            }
        });

    }

    private void attemptGameStart(){
        for(boolean b: gameViewModel.getPlayerStatusArray()){
            if(!b){
                startGameButton.setError("Invalid players");
            }
        }

        ArrayList<Player> selectedPlayers = new ArrayList<>(4);
        for(Player p: gameViewModel.getPlayerArray()){
            if(p != null){
                selectedPlayers.add(p);
            }
        }
        selectedPlayers.trimToSize();
        if(selectedPlayers.size() < 2){
            startGameButton.setError("Must have at least 2 players");
        }else{
            Intent intent = new Intent(getActivity(), ActiveGameFragmentActivity.class);
            intent.putParcelableArrayListExtra("selectedPlayers", selectedPlayers);
            startActivity(intent);
        }
    }
}
