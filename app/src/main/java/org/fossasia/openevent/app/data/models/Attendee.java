package org.fossasia.openevent.app.data.models;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.event.attendees.viewholders.AttendeeViewHolder;

import java.util.List;

@Table(database = OrgaDatabase.class)
public class Attendee extends AbstractItem<Attendee, AttendeeViewHolder> implements Comparable<Attendee> {

    @PrimaryKey
    private long id;

    @Column
    @JsonProperty("checked_in")
    private boolean checkedIn;
    @Column
    private String email;
    @Column
    @JsonProperty("firstname")
    private String firstName;
    @Column
    @JsonProperty("lastname")
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
    @Column
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

    public static Attendee withTicket(Ticket ticket) {
        Attendee attendee = new Attendee();
        attendee.setTicket(ticket);

        return attendee;
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

    @Override
    public int compareTo(@NonNull Attendee other) {
        int compareFirstName;
        int compareLastName;
        return (compareFirstName = firstName.toLowerCase().compareTo(other.getFirstName().toLowerCase())) == 0 ? (compareLastName = lastName.toLowerCase().compareTo(other.getLastName().toLowerCase())) == 0 ? email.toLowerCase().compareTo(other.getEmail().toLowerCase()) : compareLastName : compareFirstName;
    }

    @Override
    public long getIdentifier() {
        return id;
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
    public AttendeeViewHolder getViewHolder(View view) {
        return new AttendeeViewHolder(DataBindingUtil.bind(view));
    }

    @Override
    public void bindView(AttendeeViewHolder holder, List<Object> list) {
        super.bindView(holder, list);
        holder.bindAttendee(this);
    }

    @Override
    public void unbindView(AttendeeViewHolder holder) {
        super.unbindView(holder);
        holder.unbindAttendee();
    }
}
