package org.fossasia.openevent.app.common.data.models;

import android.databinding.ObservableBoolean;

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

import org.fossasia.openevent.app.common.data.db.configuration.ObservableBooleanTypeConverter;
import org.fossasia.openevent.app.common.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.common.data.models.contract.IHeaderProvider;
import org.fossasia.openevent.app.common.data.models.delegates.AttendeeDelegate;
import org.fossasia.openevent.app.common.data.models.delegates.contract.IAttendeeDelegate;
import org.fossasia.openevent.app.module.attendee.list.viewholders.AttendeeViewHolder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Delegate;

@Data
@Builder
@Type("attendee")
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@EqualsAndHashCode(callSuper = false)
@Table(database = OrgaDatabase.class)
public class Attendee extends AbstractItem<Attendee, AttendeeViewHolder> implements Comparable<Attendee>, IHeaderProvider {

    @Delegate(types = IAttendeeDelegate.class)
    private final AttendeeDelegate attendeeDelegate = new AttendeeDelegate(this);

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

    @Column(typeConverter = ObservableBooleanTypeConverter.class)
    public ObservableBoolean checking = new ObservableBoolean();

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

    public Attendee() { }
}
