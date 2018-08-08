package org.fossasia.openevent.app.data.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.data.ticket.OnSiteTicketSerializer;
import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.data.ticket.OnSiteTicket;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@Type("order")
@AllArgsConstructor
@ToString(exclude = {"event"})
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
@SuppressWarnings({ "PMD.ExcessivePublicCount", "PMD.TooManyFields" })
public class Order {

    @PrimaryKey
    @Id(LongIdHandler.class)
    public Long id;

    public float amount;
    public String completedAt;
    public String identifier;
    public String paidVia;
    public String paymentMode;
    public String status;

    // Migration 6 deletions
    // invoiceNumber

    // Migration 6 additions
    public String address;
    public String zipcode;
    public String city;
    public String state;
    public String country;
    public String expMonth;
    public String expYear;
    public String transactionId;
    public String discountCodeId;
    public String brand;
    public String last4;

    @Relationship("event")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Event event;

    @ColumnIgnore
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using= OnSiteTicketSerializer.class)
    public List<OnSiteTicket> onSiteTickets;

    public Order() { }
}
