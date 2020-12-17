package com.example.rummytracker.ui.game.activegame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.rummytracker.R;
import com.example.rummytracker.ui.game.Player;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import org.w3c.dom.Text;

public class ActivePlayerFragment extends Fragment {

    private FirebaseFirestore db;

    private int fragmentIndex;
    private ActiveGameViewModel activeGameViewModel;
    private Player player;

    private TextView playerNameTextView;
    private TextView playerScoreTextView;
    private EditText playerRoundScoreEdit;
    private ImageView dealerChipImg;

    public ActivePlayerFragment () {}

    public static ActivePlayerFragment newInstance(int index, Player p) {
        ActivePlayerFragment frag = new ActivePlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        bundle.putParcelable("player", p);
        frag.setArguments(bundle);

        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentIndex = getArguments().getInt("index");
        player = getArguments().getParcelable("player");
        activeGameViewModel = new ViewModelProvider(requireActivity()).get(ActiveGameViewModel.class);
        View root = inflater.inflate(R.layout.fragment_active_player, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        playerNameTextView = view.findViewById(R.id.active_player_name_text);
        playerScoreTextView = view.findViewById(R.id.active_player_score);
        playerRoundScoreEdit = view.findViewById(R.id.round_score_edit_text);
        dealerChipImg = view.findViewById(R.id.dealer_chip_img);

        if(!player.isGuest()){
            db.collection("players").whereEqualTo("username", player.getUsername()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            player.setName(document.getString("name"));
                            player.setUserid(document.getString("user_id"));
                        }
                        if(player.getName().indexOf(" ") > 0){
                            playerNameTextView.setText(player.getName().substring(0, player.getName().indexOf(" ") + 2));
                        }else{
                            playerNameTextView.setText(player.getName());
                        }
                    }
                }
            });
        }else{
            playerNameTextView.setText(player.getUsername());
        }
        updatePlayerScore();
        updateDealerChipVis();

        playerRoundScoreEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    activeGameViewModel.setScoreSubmitted(false);
                }else{
                    activeGameViewModel.updateRoundScore(fragmentIndex, parseRoundScore());
                    activeGameViewModel.setScoreSubmitted(true);
                }
            }
        });

        activeGameViewModel.getLiveScoreArray().observe(getViewLifecycleOwner(), new Observer<int[]>() {
            @Override
            public void onChanged(int[] ints) {
                updatePlayerScore();
                playerRoundScoreEdit.getText().clear();
            }
        });

        activeGameViewModel.getLiveDealerIndex().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                updateDealerChipVis();
            }
        });

        activeGameViewModel.getLiveIsWinner().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer winnerIndex) {
                if(winnerIndex > -1) {
                    updatePlayerDatabase(winnerIndex);
                }
            }
        });
    }

    private void updatePlayerScore(){
        int[] scores = activeGameViewModel.getScoreArray();
        playerScoreTextView.setText(Integer.toString(scores[fragmentIndex]));
    }

    private int parseRoundScore(){
        try{
            return Integer.valueOf(playerRoundScoreEdit.getText().toString());
        }catch(NumberFormatException e){
            return 0;
        }
    }

    private void updateDealerChipVis(){
        if(activeGameViewModel.getDealerIndex() == fragmentIndex){
            dealerChipImg.setVisibility(View.VISIBLE);
        }else{
            dealerChipImg.setVisibility(View.INVISIBLE);
        }
    }

    private void updatePlayerDatabase(final int winnerIndex){
        if (player.isGuest()) { return; }
        final int[] scores = activeGameViewModel.getScoreArray();
        final DocumentReference doc = db.collection("players").document(player.getUserid()).collection("stats").document("overall");
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException{
                DocumentSnapshot snapshot = transaction.get(doc);
                transaction.update(doc, "games_played", FieldValue.increment(1));
                transaction.update(doc, "total_points", FieldValue.increment(scores[fragmentIndex]));
                transaction.update(doc, "total_turns", FieldValue.increment(activeGameViewModel.getRoundNumber()));
                if(winnerIndex == fragmentIndex) {
                    transaction.update(doc, "games_won", FieldValue.increment(1));
                    if (snapshot.getDouble("highest_win_score").intValue() < scores[fragmentIndex]) {
                        transaction.update(doc, "highest_win_score", scores[fragmentIndex]);
                    }
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(requireContext(), "Player " + fragmentIndex + " updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
