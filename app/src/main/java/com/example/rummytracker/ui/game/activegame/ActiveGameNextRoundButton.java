package com.example.rummytracker.ui.game.activegame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rummytracker.R;

public class ActiveGameNextRoundButton extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        new ViewModelProvider(requireActivity()).get(ActiveGameViewModel.class);
        View root = inflater.inflate(R.layout.fragment_active_player, container, false);
        return root;
    }
}
