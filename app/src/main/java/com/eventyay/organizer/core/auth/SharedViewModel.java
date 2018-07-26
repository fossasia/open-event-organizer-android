package com.eventyay.organizer.core.auth;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<String> email = new MutableLiveData<>();

    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public LiveData<String> getEmail() {
        return email;
    }
}
