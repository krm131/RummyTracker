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

        TextView winnerNameView = dialogView.findViewById(R.id.dialog_winner_name);
        winnerNameView.setText(getArguments().getString("winner name"));

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
