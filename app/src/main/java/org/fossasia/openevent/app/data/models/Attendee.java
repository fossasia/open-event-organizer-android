package org.fossasia.openevent.app.data.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.IItem;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.databinding.AttendeeLayoutBinding;
import org.fossasia.openevent.app.event.attendees.viewholders.AttendeeViewHolder;
import org.fossasia.openevent.app.utils.Utils;

import java.util.Collections;
import java.util.List;

@Table(database = OrgaDatabase.class, allFields = true)
public class Attendee extends BaseModel implements Parcelable, Comparable<Attendee>, IItem<Attendee, AttendeeViewHolder> {

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
        setFirstName("testFirstName" + id);
        setLastName("testLastName" + id);
        setEmail("testEmail" + id + "@test.com");
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

    @Override
    public int compareTo(@NonNull Attendee other) {
        int compareFirstName;
        int compareLastName;
        return (compareFirstName = firstName.toLowerCase().compareTo(other.getFirstName().toLowerCase())) == 0 ? (compareLastName = lastName.toLowerCase().compareTo(other.getLastName().toLowerCase())) == 0 ? email.toLowerCase().compareTo(other.getEmail().toLowerCase()) : compareLastName : compareFirstName;
    }

    @Override
    public Object getTag() {
        return null;
    }

    @Override
    public Attendee withTag(Object o) {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public Attendee withEnabled(boolean enabled) {
        return null;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public Attendee withSetSelected(boolean selected) {
        return null;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public Attendee withSelectable(boolean selectable) {
        return null;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.attendee_layout;
    }

    @Override
    public View generateView(Context context) {
        AttendeeViewHolder holder = getViewHolder((ViewGroup) LayoutInflater.from(context).inflate(getLayoutRes(), null, false));

        bindView(holder, Collections.EMPTY_LIST);

        return holder.itemView;
    }

    @Override
    public View generateView(Context context, ViewGroup parent) {
        AttendeeViewHolder holder = getViewHolder(parent);

        bindView(holder, Collections.EMPTY_LIST);

        return holder.itemView;
    }

    @Override
    public AttendeeViewHolder getViewHolder(ViewGroup parent) {
        return new AttendeeViewHolder(AttendeeLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void bindView(AttendeeViewHolder holder, List<Object> list) {
        holder.bindAttendee(this);
    }

    @Override
    public void unbindView(AttendeeViewHolder holder) {
        holder.unbindAttendee();
    }

    /**
     * View got attached to the window
     *
     * @param holder
     */
    @Override
    public void attachToWindow(AttendeeViewHolder holder) {

    }

    /**
     * View got detached from the window
     *
     * @param holder
     */
    @Override
    public void detachFromWindow(AttendeeViewHolder holder) {

    }

    /**
     * When RecyclerView fails to recycle that viewHolder because it's in a transient state
     * Implement this and clear any animations, to allow recycling. Return true in that case
     * As our viewHolder not using any animations currently, returning false
     *
     * @param holder
     */
    @Override
    public boolean failedToRecycle(AttendeeViewHolder holder) {
        return false;
    }

    @Override
    public boolean equals(int id) {
        return this.id == id;
    }

    @Override
    public Attendee withIdentifier(long id) {
        return null;
    }

    @Override
    public long getIdentifier() {
        return id;
    }

}
