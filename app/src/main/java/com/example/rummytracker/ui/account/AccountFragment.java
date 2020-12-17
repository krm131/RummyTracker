package com.example.rummytracker.ui.account;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.rummytracker.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends Fragment {

    private AccountViewModel accountViewModel;
    private FirebaseFirestore db;

    private TextView userTextView;
    private Button logoutButton;
    private TextView usernameTextView;
    private TextView userGamesWonView;
    private TextView userGamePlayedView;
    private TextView userHighestWinScoreView;
    private TextView userAvgPPG;
    private TextView userAvgPPT;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        View root = inflater.inflate(R.layout.fragment_account, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        final NavController navController = Navigation.findNavController(view);
        userTextView = view.findViewById(R.id.text_user);
        usernameTextView = view.findViewById(R.id.text_username);
        userGamesWonView = view.findViewById(R.id.account_text_games_won);
        userGamePlayedView = view.findViewById(R.id.account_text_games_played);
        userHighestWinScoreView = view.findViewById(R.id.account_text_highest_win_score);
        userAvgPPG = view.findViewById(R.id.account_text_average_ppg);
        userAvgPPT = view.findViewById(R.id.account_text_average_ppt);

        accountViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser != null){
                    userTextView.setText(firebaseUser.getDisplayName());
                    db.collection("players").document(firebaseUser.getUid()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    usernameTextView.setText(documentSnapshot.getString("username"));
                                }
                            });



                }else{
                    userTextView.setVisibility(View.INVISIBLE);
                    navController.navigate(R.id.nav_login);
                }
            }
        });

        accountViewModel.getNewUserCreated().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String newUser) {
                if(newUser != null){
                    addUsername(newUser);
                }
            }
        });



        if(accountViewModel.getUser().getValue() != null) {
            final DocumentReference docRef = db.collection("players")
                    .document(accountViewModel.getUser().getValue().getUid())
                    .collection("stats")
                    .document("overall");

            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w("Account Stats", "Listen failed.", error);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        int totalPoints = snapshot.getDouble("total_points").intValue();
                        int gamesPlayed = snapshot.getDouble("games_played").intValue();
                        int roundsPlayed = snapshot.getDouble("total_turns").intValue();
                        int gamesWon = snapshot.getDouble("games_won").intValue();
                        userGamesWonView.setText(" Games won: " + gamesWon + " (" + Math.round(((float)gamesWon/(float)gamesPlayed)*100) + "%)");
                        userGamePlayedView.setText("Games played: " + gamesPlayed);
                        userHighestWinScoreView.setText("Highest win score: " + snapshot.get("highest_win_score").toString());
                        if (gamesPlayed == 0) {
                            userAvgPPG.setText("Average points per game: 0");
                        } else {
                            userAvgPPG.setText("Average points per game: " + totalPoints / gamesPlayed);
                        }
                        if (roundsPlayed == 0) {
                            userAvgPPT.setText("Average points per turn: 0");
                        } else {
                            userAvgPPT.setText("Average points per turn: " + totalPoints / roundsPlayed);
                        }
                    }
                }
            });
        }

        logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });


    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(requireContext());
    }

    public void addUsername(String username) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);

        Log.d("ADD_USERNAME", accountViewModel.getUser().getValue().getUid());
        db.collection("players").document(accountViewModel.getUser().getValue().getUid()).set(data, SetOptions.merge());
    }
}
