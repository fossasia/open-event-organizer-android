package com.eventyay.organizer.data.attendee;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.view.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.mikepenz.fastadapter.items.AbstractItem;

import com.eventyay.organizer.common.model.HeaderProvider;
import com.eventyay.organizer.core.attendee.list.viewholders.AttendeeViewHolder;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.order.Order;
import com.eventyay.organizer.data.ticket.Ticket;

import lombok.experimental.Delegate;

@Entity
@SuppressWarnings({ "PMD.ExcessivePublicCount", "PMD.TooManyFields" })
public class Attendee extends AbstractItem<Attendee, AttendeeViewHolder> implements Comparable<Attendee>, HeaderProvider {

    @Delegate(types = AttendeeDelegate.class)
    private final AttendeeDelegateImpl attendeeDelegate = new AttendeeDelegateImpl(this);

    @Id(LongIdHandler.class)
    @PrimaryKey
    public long id;
    @ColumnInfo
    public String city;
    @ColumnInfo
    public String firstname;
    @ColumnInfo
    public String lastname;
    @ColumnInfo
    public boolean isCheckedIn;
    @ColumnInfo
    public String state;
    @ColumnInfo
    public String address;
    @ColumnInfo
    public String pdfUrl;
    @ColumnInfo
    public String country;
    @ColumnInfo
    public String email;

    @Relationship("ticket")
    @ForeignKey(entity = Ticket.class, parentColumns = "id", childColumns = "ticket", onDelete = ForeignKey.CASCADE)
    public Ticket ticket;

    @Relationship("order")
    @ForeignKey(entity = Order.class, parentColumns = "id", childColumns = "order", onDelete = ForeignKey.CASCADE)
    public Order order;

    // To associate attendees and event
    @Relationship("event")
    @ForeignKey(entity = Event.class, parentColumns = "id", childColumns = "event", onDelete = ForeignKey.CASCADE)
    public Event event;

    // Migration 7 additions

    @ColumnInfo
    public String blog;
    @ColumnInfo
    public String homeAddress;
    @ColumnInfo
    public String workAddress;
    @ColumnInfo
    public String jobTitle;
    @ColumnInfo
    public String taxBusinessInfo;
    @ColumnInfo
    public String phone;
    @ColumnInfo
    public String gender;
    @ColumnInfo
    public String company;
    @ColumnInfo
    public String workPhone;
    @ColumnInfo
    public String birthDate;
    @ColumnInfo
    public String twitter;
    @ColumnInfo
    public String facebook;
    @ColumnInfo
    public String github;
    @ColumnInfo
    public String website;
    @ColumnInfo
    public String shippingAddress;
    @ColumnInfo
    public String billingAddress;

    // Migration 8 additions

    @ColumnInfo
    public String checkinTimes;

    // Migration 16 additions

    @ColumnInfo
    public String checkoutTimes;

    // Non model entities

    @JsonIgnore
    @ColumnInfo
    public boolean checking;

    public Attendee() { }

    @Override
    public AttendeeViewHolder getViewHolder(View v) {
        return null;
    }

    @Override
    public String getHeader() {
        return null;
    }

    @Override
    public long getHeaderId() {
        return 0;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return 0;
    }

    @Override
    public int compareTo(@NonNull Attendee attendee) {
        return 0;
    }
}
