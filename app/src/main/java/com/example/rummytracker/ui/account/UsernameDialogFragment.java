package com.example.rummytracker.ui.account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rummytracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashSet;
import java.util.Set;

public class UsernameDialogFragment extends DialogFragment {

    private FirebaseFirestore db;
    private AccountViewModel accountViewModel;
    private final static int AVAILABLE = 0;
    private final static int TAKEN = 1;
    private final static int ERROR = 2;
    int available;
    Set<String> userNames;

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        db = FirebaseFirestore.getInstance();
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        buildUsernameList();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_username, null);
        final EditText username_editText = dialogView.findViewById(R.id.edittext_username);
        final TextInputLayout select_layout = dialogView.findViewById(R.id.username_select_layout);
        dialogView.findViewById(R.id.button_dialog_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkUsernameAvailability(username_editText.getText().toString())){
                    accountViewModel.setNewUserCreated(username_editText.getText().toString());
                    UsernameDialogFragment.this.dismiss();
                }else{
                    select_layout.setError("Username taken");
                }
            }
        });
        builder.setView(dialogView);
        return builder.create();
    }

    private boolean checkUsernameAvailability(String username){
        if(userNames.contains(username)){
            return false;
        }
        return true;
    }

    private void buildUsernameList (){
        userNames = new HashSet<>();
        db.collection("players").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc: task.getResult()){
                                userNames.add(doc.getString("username"));
                            }
                        }
                    }
                });
    }
}
