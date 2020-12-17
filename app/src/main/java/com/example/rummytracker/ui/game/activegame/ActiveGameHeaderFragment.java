package com.example.rummytracker.ui.game.activegame;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rummytracker.R;

public class ActiveGameHeaderFragment extends Fragment {
    private String fragmentTag;
    private ActiveGameViewModel activeGameViewModel;
    private TextView roundNumberText;
    private TextView targetScoreText;

    public ActiveGameHeaderFragment() {}

    public static ActiveGameHeaderFragment newInstance(String tag){
        ActiveGameHeaderFragment activeGameHeaderFragment = new ActiveGameHeaderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        activeGameHeaderFragment.setArguments(bundle);

        return activeGameHeaderFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        fragmentTag = getArguments().getString("tag");
        activeGameViewModel = new ViewModelProvider(requireActivity()).get(ActiveGameViewModel.class);
        View root = inflater.inflate(R.layout.active_game_header_fragment, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        roundNumberText = view.findViewById(R.id.round_number_text);
        targetScoreText = view.findViewById(R.id.target_score_text);

        //updateHeader();
        Log.d("HeaderFragment", "Setting header text...");
        roundNumberText.setText("Round: 0");
        targetScoreText.setText("500");

    }

    private void updateHeader() {
        roundNumberText.setText(Integer.toString(activeGameViewModel.getRoundNumber()));
    }
}
