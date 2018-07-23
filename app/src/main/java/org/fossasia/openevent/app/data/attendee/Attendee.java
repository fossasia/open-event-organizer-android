package org.fossasia.openevent.app.data.attendee;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

import org.fossasia.openevent.app.common.model.HeaderProvider;
import org.fossasia.openevent.app.core.attendee.list.viewholders.AttendeeViewHolder;
import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.order.Order;
import org.fossasia.openevent.app.data.ticket.Ticket;

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
@EqualsAndHashCode(callSuper = false, exclude = { "attendeeDelegate", "checking" })
@Table(database = OrgaDatabase.class)
@SuppressWarnings({ "PMD.ExcessivePublicCount", "PMD.TooManyFields" })
public class Attendee extends AbstractItem<Attendee, AttendeeViewHolder> implements Comparable<Attendee>, HeaderProvider {

    @Delegate(types = AttendeeDelegate.class)
    private final AttendeeDelegateImpl attendeeDelegate = new AttendeeDelegateImpl(this);

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

    @Relationship("order")
    @ForeignKey(onDelete = ForeignKeyAction.CASCADE, saveForeignKeyModel = true)
    public Order order;

    // To associate attendees and event
    @Relationship("event")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Event event;

    // Migration 7 additions

    @Column
    public String blog;
    @Column
    public String homeAddress;
    @Column
    public String workAddress;
    @Column
    public String jobTitle;
    @Column
    public String taxBusinessInfo;
    @Column
    public String phone;
    @Column
    public String gender;
    @Column
    public String company;
    @Column
    public String workPhone;
    @Column
    public String birthDate;
    @Column
    public String twitter;
    @Column
    public String facebook;
    @Column
    public String github;
    @Column
    public String website;
    @Column
    public String shippingAddress;
    @Column
    public String billingAddress;

    // Migration 8 additions

    @Column
    public String checkinTimes;

    // Migration 16 additions

    @Column
    public String checkoutTimes;

    // Non model entities

    @JsonIgnore
    @Column
    public boolean checking;

    public Attendee() { }
}
