package com.eventyay.organizer.data.attendee;

import androidx.annotation.NonNull;

import com.eventyay.organizer.common.di.component.DaggerAppComponent;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import timber.log.Timber;

public class AttendeeCheckInWork extends Worker {

    @Inject
    AttendeeRepositoryImpl attendeeRepository;

    public static final String TAG = "attendee_check_in";

    @NonNull
    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public Worker.Result doWork() {
        DaggerAppComponent.create().inject(this);

        Timber.d("Running attendee checkin work");

        Iterable<Attendee> attendees = attendeeRepository.getPendingCheckIns().blockingIterable();
        for (Attendee attendee : attendees) {
            Timber.d("Trying to toggle attendee status -> %s", attendee);
            try {
                Attendee toggled = attendeeRepository.toggleAttendeeCheckStatus(attendee).blockingFirst();
                Timber.d("Attendee check in work succeeded for attendee: %s", toggled);
            } catch (Exception exception) {
                Timber.e("Attendee Check In Work Failed for attendee status -> %ss\n" +
                    "With error: %s\n" +
                    "The work is rescheduled", attendee, exception.getMessage());
                return Worker.Result.RETRY;
            }
        }

        return Worker.Result.SUCCESS;
    }

    public static void scheduleWork() {
        Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(AttendeeCheckInWork.class)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build();

        WorkManager.getInstance().beginUniqueWork(TAG, ExistingWorkPolicy.REPLACE, request).enqueue();
    }
}
