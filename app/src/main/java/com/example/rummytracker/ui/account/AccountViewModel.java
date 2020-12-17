package com.example.rummytracker.ui.account;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AccountViewModel extends ViewModel {
    private MutableLiveData<FirebaseUser> userAccount;
    private MutableLiveData<String> newUserCreated;

    private FirebaseAuth mAuth;

    public AccountViewModel() {
        userAccount = new MutableLiveData<>();
        newUserCreated = new MutableLiveData<>(null);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                userAccount.setValue(firebaseAuth.getCurrentUser());
            }
        });

    }

    public LiveData<FirebaseUser> getUser() { return userAccount; }

    public void updateUser(FirebaseUser newUser) { userAccount.setValue(newUser); }

    public LiveData<String> getNewUserCreated() { return newUserCreated;}

    public void setNewUserCreated (String username) { newUserCreated.setValue(username);}

}
