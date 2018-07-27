package com.eventyay.organizer.data.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import com.eventyay.organizer.data.db.configuration.OrgaDatabase;
import com.eventyay.organizer.data.event.Event;

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

    public Order() { }
}
