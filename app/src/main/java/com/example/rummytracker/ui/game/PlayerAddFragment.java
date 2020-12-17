package com.example.rummytracker.ui.game;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.rummytracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerAddFragment extends Fragment {

    private GameViewModel gameViewModel;
    private AutoCompleteTextView playerUserName;
    private FirebaseFirestore db;
    private Set<String> playerSet;
    private ColorStateList textColorsOriginal;
    private CheckBox guestCheckBox;
    private TextInputLayout playerLayout;
    private String fragmentTag;
    private int fragmentIndex;

    public PlayerAddFragment() {}

    public static PlayerAddFragment newInstance(String tag){
        PlayerAddFragment playerAddFragment = new PlayerAddFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        playerAddFragment.setArguments(bundle);

        return playerAddFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        fragmentTag = getArguments().getString("tag");
        fragmentIndex = getFragmentIndex();
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add_new_player, container, false);
        return root;
    }
 
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        guestCheckBox = view.findViewById(R.id.guest_check_box);
        playerUserName = view.findViewById(R.id.player_autoComplete);
        playerLayout = view.findViewById(R.id.player_textLayout);
        textColorsOriginal = playerUserName.getTextColors();
        playerSet = new HashSet<>();

        gameViewModel.getLivePlayerList().observe(getViewLifecycleOwner(), new Observer<Player[]>() {
            @Override
            public void onChanged(Player[] p) {
                String text = playerUserName.getText().toString();
                if(checkForDuplicates(text)){
                    setTextError("Duplicate player");
                }else if (!guestCheckBox.isChecked() && !checkInDatabase(text)){
                    setTextError("Player does not exist");
                }else{
                    setTextNormal();
                }
            }
        });
        db = FirebaseFirestore.getInstance();
        db.collection("players").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Log.d("GAME_VIEW_MODEL", "task result size: " + task.getResult().size());
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        playerSet.add(doc.getString("username"));
                    }
                    buildAutoComplete();
                }
            }
        });

        playerUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    playerUserName.setTextColor(textColorsOriginal);
                    playerLayout.setError(null);
                }else{
                    String text = playerUserName.getText().toString();
                    if(text.isEmpty()){
                        setTextNormal();
                        updatePlayerList(null, false);
                    }else{
                        if(guestCheckBox.isChecked()){
                            setTextNormal();
                            updatePlayerList(text, true);
                        }else{
                            if(checkInDatabase(text)){
                                setTextNormal();
                                updatePlayerList(text, false);
                            }else{
                                updatePlayerList(null, false);
                                setTextError("Player does not exist");
                            }
                        }
                    }
                }
            }
        });

        guestCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String text = playerUserName.getText().toString();
                if(!text.isEmpty()){
                    if(isChecked){
                        setTextNormal();
                        updatePlayerList(text, true);
                    }else{
                        if(checkInDatabase(text)){
                            setTextNormal();
                            updatePlayerList(text, false);
                        }else{
                            setTextError("Player does not exist");
                            updatePlayerList(null, false);
                        }
                    }
                }
            }
        });
    }

    private void buildAutoComplete(){
        Log.d(fragmentTag, "set size: " + playerSet.size());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(playerSet));
        playerUserName.setAdapter(adapter);
    }

    private void setTextNormal(){
        playerUserName.setTextColor(textColorsOriginal);
        playerLayout.setError(null);
        gameViewModel.setPlayerStatus(fragmentIndex, true);
    }

    private void setTextError(String message){
        playerUserName.setTextColor(Color.RED);
        playerLayout.setError(message);
        gameViewModel.setPlayerStatus(fragmentIndex, false);
    }

    private boolean checkInDatabase(String username){
        if(username == null || username.isEmpty()) return true;
        return playerSet.contains(username);
    }

    private void updatePlayerList(String username, boolean isGuest){
        Player[] playerArray = gameViewModel.getPlayerArray();

        if(username == null){
            playerArray[fragmentIndex] = null;
        }else if(playerArray[fragmentIndex] == null){
            playerArray[fragmentIndex] = new Player(username, isGuest);
        }else{
            playerArray[fragmentIndex] = new Player(username, isGuest);
        }
        gameViewModel.setPlayerArray(playerArray);
    }

    private boolean checkForDuplicates(String username) {
        Log.d(fragmentTag, "Checking for dups");
        if(username == null || username.isEmpty()){ return false; }
        int count = 0;
        Player array[] = gameViewModel.getPlayerArray();
        for(Player p: array){
            if(p != null) {
                if (p.getUsername().equals(username)) {
                    count++;
                }
            }
            Log.d(fragmentTag, "username count: " + count);
            if(count > 1) return true;
        }
        return false;
    }

    private int getFragmentIndex(){
        switch(fragmentTag){
            case "NewPlayer1":
                return 0;
            case "NewPlayer2":
                return 1;
            case "NewPlayer3":
                return 2;
            case "NewPlayer4":
                return 3;
            default:
                return -1;
        }
    }

}
