package org.fossasia.openevent.app.data.models;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.data.models.contract.IHeaderProvider;
import org.fossasia.openevent.app.event.attendees.viewholders.AttendeeViewHolder;
import org.fossasia.openevent.app.utils.CompareUtils;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@EqualsAndHashCode(callSuper = false)
@Table(database = OrgaDatabase.class)
public class Attendee extends AbstractItem<Attendee, AttendeeViewHolder> implements Comparable<Attendee>, IHeaderProvider {

    @PrimaryKey
    public long id;

    @Column
    public boolean checkedIn;
    @Column
    public String email;
    @Column
    @JsonProperty("firstname")
    public String firstName;
    @Column
    @JsonProperty("lastname")
    public String lastName;
    @ForeignKey(
        saveForeignKeyModel = true,
        deleteForeignKeyModel = true,
        onDelete = ForeignKeyAction.CASCADE,
        onUpdate = ForeignKeyAction.CASCADE)
    public Order order;

    /**
     * The ticket itself should not be deleted if the Attendee is deleted
     * Neither it should be inserted when inserting the attendee, but the
     * model should load instantly with attendee, making the relationship
     * NOT stubbed.
     */
    @ForeignKey(onDelete = ForeignKeyAction.CASCADE)
    public Ticket ticket;

    // To associate attendees and event
    @Column
    public long eventId;

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

    @VisibleForTesting
    public static Attendee withTicket(Ticket ticket) {
        Attendee attendee = new Attendee();
        attendee.setTicket(ticket);

        return attendee;
    }

    @VisibleForTesting
    public Attendee withCheckedIn() {
        this.checkedIn = true;

        return this;
    }

    @Override
    public int compareTo(@NonNull Attendee other) {
        return CompareUtils.compareCascading(this, other,
            Attendee::getFirstName, Attendee::getLastName, Attendee::getEmail
        );
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

    @Override
    public String getHeader() {
        return getFirstName().substring(0, 1);
    }

    @Override
    public long getHeaderId() {
        return getHeader().charAt(0);
    }
}
