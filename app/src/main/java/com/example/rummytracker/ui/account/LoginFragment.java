package com.example.rummytracker.ui.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.rummytracker.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class LoginFragment extends Fragment{

    private AccountViewModel accountViewModel;
    private Button login_button;
    private static final int RC_SIGN_IN = 123;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        db = FirebaseFirestore.getInstance();
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        login_button = root.findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSignInIntent();
            }
        });

        return root;
    }

    public void createSignInIntent() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                //accountViewModel.updateUser(FirebaseAuth.getInstance().getCurrentUser());
                if(response.isNewUser()){
                    DialogFragment dialog = new UsernameDialogFragment();
                    dialog.show(getParentFragmentManager(), "usernameDialog");
                    addDatabaseUser();
                }else {
                    Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_LONG).show();
                }
                NavHostFragment.findNavController(this).popBackStack();
            }else{
                Toast.makeText(requireContext(), "Login Error: " + response.getError().getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addDatabaseUser() {
        Map<String, Object> newPlayer = new HashMap<>();
        Map<String, Object> stats = new HashMap<>();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        newPlayer.put("user_id", user.getUid());
        newPlayer.put("name", user.getDisplayName());

        stats.put("games_played", 0);
        stats.put("games_won", 0);
        stats.put("total_points", 0);
        stats.put("total_turns", 0);
        stats.put("highest_win_score", 0);
        stats.put("largest_win_margin", 0);

        db.collection("players").document(user.getUid()).set(newPlayer);
        db.collection("players").document(user.getUid()).collection("stats").document("overall").set(stats);

    }
}
