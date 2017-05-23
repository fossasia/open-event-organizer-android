package org.fossasia.openevent.app.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.fossasia.openevent.app.utils.Utils;

public class Attendee implements Parcelable {

    @SerializedName("checked_in")
    private boolean checkedIn;
    @SerializedName("email")
    private String email;
    @SerializedName("firstname")
    private String firstname;
    @SerializedName("id")
    private long id;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("order")
    private Order order;
    @SerializedName("ticket")
    private Ticket ticket;

    public Attendee() {}

    public Attendee(long id) {
        setId(id);
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public String getTicketMessage() {
        return Utils.formatOptionalString("%s %s \nTicket: %s",
            getFirstname(),
            getLastname(),
            getTicket().getType());
    }

    @Override
    public String toString() {
        return "Attendee{" +
            "checkedIn=" + checkedIn +
            ", email='" + email + '\'' +
            ", firstname='" + firstname + '\'' +
            ", id=" + id +
            ", lastname='" + lastname + '\'' +
            ", order=" + order +
            ", ticket=" + ticket +
            '}';
    }

    // Parcelable Implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.checkedIn ? (byte) 1 : (byte) 0);
        dest.writeString(this.email);
        dest.writeString(this.firstname);
        dest.writeLong(this.id);
        dest.writeString(this.lastname);
        dest.writeParcelable(this.order, flags);
        dest.writeParcelable(this.ticket, flags);
    }

    protected Attendee(Parcel in) {
        this.checkedIn = in.readByte() != 0;
        this.email = in.readString();
        this.firstname = in.readString();
        this.id = in.readLong();
        this.lastname = in.readString();
        this.order = in.readParcelable(Order.class.getClassLoader());
        this.ticket = in.readParcelable(Ticket.class.getClassLoader());
    }

    public static final Parcelable.Creator<Attendee> CREATOR = new Parcelable.Creator<Attendee>() {
        @Override
        public Attendee createFromParcel(Parcel source) {
            return new Attendee(source);
        }

        @Override
        public Attendee[] newArray(int size) {
            return new Attendee[size];
        }
    };
}
