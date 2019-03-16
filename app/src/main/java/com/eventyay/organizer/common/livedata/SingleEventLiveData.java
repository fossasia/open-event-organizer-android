package com.eventyay.organizer.common.livedata;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A wrapper over MutableLiveData in order to send a single event from ViewModel.
 * @param <T> Data type of the LiveData.
 */
public class SingleEventLiveData<T> extends MutableLiveData<T> {

    private final AtomicBoolean pending = new AtomicBoolean(false);

    @MainThread
    public void observeSingle(LifecycleOwner owner, final Observer<T> observer) {

        if (hasActiveObservers()) {
            throw new IllegalStateException("Only one observer at a time may subscribe to a SingleEventLiveData");
        }

        // Observe the internal MutableLiveData
        super.observe(owner, t -> {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t);
            }
        });
    }

    @MainThread
    public void setValue(@Nullable T t) {
        pending.set(true);
        super.setValue(t);
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    public void call() {
        setValue(null);
    }
}
