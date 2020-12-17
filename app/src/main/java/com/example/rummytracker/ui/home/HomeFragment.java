package com.example.rummytracker.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.rummytracker.R;
import com.example.rummytracker.ui.account.AccountViewModel;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private AccountViewModel accountViewModel;
    private boolean rrPlayersAdded = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    /*
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        accountViewModel = new ViewModelProvider(requireActivity()).get(AccountViewModel.class);
        if(!rrPlayersAdded){

            if(accountViewModel.getUser().getValue().getUid().equals("k4goLWnAn2RAUhqMwbG98VNl99s1")){
                //TODO popup add players
            }
        }


    }
    */
}