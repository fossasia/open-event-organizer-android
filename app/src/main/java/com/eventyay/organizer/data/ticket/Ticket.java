package com.eventyay.organizer.data.ticket;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import com.eventyay.organizer.data.db.configuration.OrgaDatabase;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.order.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Delegate;

@Data
@Builder
@Type("ticket")
@AllArgsConstructor
@Entity
@SuppressWarnings("PMD.TooManyFields")
public class Ticket implements Comparable<Ticket> {

    @Delegate
    private final TicketDelegateImpl ticketDelegate = new TicketDelegateImpl(this);

    @Id(LongIdHandler.class)
    @PrimaryKey
    public Long id;

    public String description;
    public String type;
    public Float price;
    public String name;
    public Integer maxOrder;
    public Boolean isDescriptionVisible;
    public Boolean isFeeAbsorbed;
    public Integer position;
    public Long quantity;
    public Boolean isHidden;
    public String salesStartsAt;
    public String salesEndsAt;
    public Integer minOrder;
    public boolean isCheckinRestricted;
    public boolean autoCheckinEnabled;

    @Relationship("event")
    @ForeignKey(entity = Event.class, parentColumns = "id", childColumns = "event", onDelete = ForeignKey.CASCADE)
    public Event event;

    @Relationship("order")
    @ForeignKey(entity = Order.class, parentColumns = "id", childColumns = "order", onDelete = ForeignKey.CASCADE)
    public Order order;

    public Ticket() { }

    @Override
    public int compareTo(@NonNull Ticket ticket) {
        return 0;
    }
}
