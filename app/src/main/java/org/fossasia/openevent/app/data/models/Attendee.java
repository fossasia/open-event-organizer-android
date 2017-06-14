package org.fossasia.openevent.app.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.utils.Utils;

@Table(database = OrgaDatabase.class, allFields = true)
public class Attendee extends BaseModel implements Parcelable {

    @PrimaryKey
    private long id;

    @SerializedName("checked_in")
    private boolean checkedIn;
    private String email;
    @SerializedName("firstname")
    private String firstName;
    @SerializedName("lastname")
    private String lastName;
    @ForeignKey(
        saveForeignKeyModel = true,
        deleteForeignKeyModel = true,
        onDelete = ForeignKeyAction.CASCADE,
        onUpdate = ForeignKeyAction.CASCADE)
    private Order order;

    /**
     * The ticket itself should not be deleted if the Attendee is deleted
     * Neither it should be inserted when inserting the attendee, but the
     * model should load instantly with attendee, making the relationship
     * NOT stubbed.
     */
    @ForeignKey
    private Ticket ticket;

    // To associate attendees and event
    private long eventId;

    public Attendee() {}

    public Attendee(boolean checkedIn) {
        setCheckedIn(checkedIn);
    }

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getTicketMessage() {
        return Utils.formatOptionalString("%s %s \nTicket: %s",
            getFirstName(),
            getLastName(),
            getTicket().getType());
    }

    @Override
    public String toString() {
        return "Attendee{" +
            "id=" + id +
            ", checkedIn=" + checkedIn +
            ", email='" + email + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", order=" + order +
            ", ticket=" + ticket +
            ", eventId=" + eventId +
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
        dest.writeString(this.firstName);
        dest.writeLong(this.id);
        dest.writeString(this.lastName);
        dest.writeParcelable(this.order, flags);
        dest.writeParcelable(this.ticket, flags);
    }

    protected Attendee(Parcel in) {
        this.checkedIn = in.readByte() != 0;
        this.email = in.readString();
        this.firstName = in.readString();
        this.id = in.readLong();
        this.lastName = in.readString();
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
