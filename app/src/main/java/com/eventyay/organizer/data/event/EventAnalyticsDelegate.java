package com.eventyay.organizer.data.event;

import android.databinding.ObservableFloat;
import android.databinding.ObservableLong;

import lombok.Data;

@Data
public class EventAnalyticsDelegate {
    public final ObservableLong totalAttendees = new ObservableLong();
    public final ObservableLong totalTickets = new ObservableLong();
    public final ObservableLong checkedIn = new ObservableLong();
    public final ObservableFloat totalSale = new ObservableFloat();

    public final ObservableLong freeTickets = new ObservableLong();
    public final ObservableLong paidTickets = new ObservableLong();
    public final ObservableLong donationTickets = new ObservableLong();

    public final ObservableLong soldFreeTickets = new ObservableLong();
    public final ObservableLong soldPaidTickets = new ObservableLong();
    public final ObservableLong soldDonationTickets = new ObservableLong();
}
