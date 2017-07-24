package org.fossasia.openevent.app.common.data.models;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.R;
import org.fossasia.openevent.app.common.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.common.data.models.contract.IHeaderProvider;
import org.fossasia.openevent.app.module.attendee.list.viewholders.AttendeeViewHolder;
import org.fossasia.openevent.app.common.utils.core.CompareUtils;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@Type("attendee")
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@EqualsAndHashCode(callSuper = false)
@Table(database = OrgaDatabase.class)
public class Attendee extends AbstractItem<Attendee, AttendeeViewHolder> implements Comparable<Attendee>, IHeaderProvider {
    @Id(LongIdHandler.class)
    @PrimaryKey
    public long id;
    @Column
    public String city;
    @Column
    public String firstname;
    @Column
    public String lastname;
    @Column
    public boolean isCheckedIn;
    @Column
    public String state;
    @Column
    public String address;
    @Column
    public String pdfUrl;
    @Column
    public String country;
    @Column
    public String email;

    @Relationship("ticket")
    @ForeignKey(onDelete = ForeignKeyAction.CASCADE)
    public Ticket ticket;

    // Not in API yet
    @ForeignKey(onDelete = ForeignKeyAction.CASCADE)
    public Order order;

    // To associate attendees and event
    @Relationship("event")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Event event;

    public Attendee() {}

    public Attendee(boolean checkedIn) {
        setCheckedIn(checkedIn);
    }

    public Attendee(long id) {
        setId(id);
        setFirstname("testFirstName" + id);
        setLastname("testLastName" + id);
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
        this.isCheckedIn = true;

        return this;
    }

    @Override
    public int compareTo(@NonNull Attendee other) {
        return CompareUtils.compareCascading(this, other,
            Attendee::getFirstname, Attendee::getLastname, Attendee::getEmail
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
        return getFirstname().substring(0, 1);
    }

    @Override
    public long getHeaderId() {
        return getHeader().charAt(0);
    }
}
