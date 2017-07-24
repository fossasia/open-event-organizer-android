package org.fossasia.openevent.app.data.models;

import android.support.annotation.VisibleForTesting;

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

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

import lombok.Data;
import lombok.ToString;

@Data
@Type("ticket")
@ToString(exclude = "event")
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
public class Ticket {
    @Id(LongIdHandler.class)
    @PrimaryKey
    public long id;

    public String description;
    public String type;
    public float price;
    public String name;
    public int maxOrder;
    public boolean isDescriptionVisible;
    public boolean isFeeAbsorbed;
    public int position;
    public long quantity;
    public boolean isHidden;
    public String salesEndsAt;
    public int minOrder;
    public String salesStartsAt;

    @Relationship("event")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Event event;

    public Ticket() {}

    @VisibleForTesting
    public Ticket(long id, long quantity) {
        setId(id);
        setQuantity(quantity);
    }

    @VisibleForTesting
    public Ticket(long quantity, String type) {
        this.quantity = quantity;
        this.type = type;
    }

    @VisibleForTesting
    public Ticket price(float price) {
        this.price = price;
        return this;
    }
}
