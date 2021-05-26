package com.example.rummytracker.ui.game.activegame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.rummytracker.R;

import java.util.ArrayList;

public class WinnerDialogFragment extends DialogFragment {

    private Context mContext;

    public WinnerDialogFragment (Context context){
        mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.win_dialog_fragment, null);

        ArrayList<PlayerScorePair> playerScoreList = getArguments().getParcelableArrayList("playerScoreList");

        TextView winnerNameView = dialogView.findViewById(R.id.dialog_winner_name);
        TextView player2View = dialogView.findViewById(R.id.player2score);
        TextView player3View = dialogView.findViewById(R.id.player3score);
        TextView player4View = dialogView.findViewById(R.id.player4score);

        winnerNameView.setText(getArguments().getString("winner name") + ": " + playerScoreList.get(0).getScore());
        player2View.setText(playerScoreList.get(1).getName() + ": " + playerScoreList.get(1).getScore());
        if(playerScoreList.size() > 2){
            player3View.setText(playerScoreList.get(2).getName() + ": " + playerScoreList.get(2).getScore());
        }else{
            player3View.setVisibility(View.INVISIBLE);
        }
        if(playerScoreList.size() > 3){
            player4View.setText(playerScoreList.get(3).getName() + ": " + playerScoreList.get(3).getScore());
        }else{
            player4View.setVisibility(View.INVISIBLE);
        }

        Button newGameButton = dialogView.findViewById(R.id.new_game_dialog_button);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                ((Activity) mContext).finish();
            }
        });

        builder.setView(dialogView);
        return builder.create();
    }

}
