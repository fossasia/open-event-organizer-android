package org.fossasia.openevent.app.common.data.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.common.data.db.configuration.OrgaDatabase;

import lombok.Data;

@Data
@Type("order")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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

    public Order() { }

    public Order(String identifier) {
        setIdentifier(identifier);
    }
}
