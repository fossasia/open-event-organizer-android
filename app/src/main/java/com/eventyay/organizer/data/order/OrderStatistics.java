package com.eventyay.organizer.data.order;

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
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@Type("order-statistics-event")
@NoArgsConstructor
@AllArgsConstructor
@ToString()
@Table(database = OrgaDatabase.class, allFields = true)
public class OrderStatistics {

    @Id(LongIdHandler.class)
    @PrimaryKey
    public Long id;

    @ForeignKey(stubbedRelationship = true)
    public Statistics sales;
    @ForeignKey(stubbedRelationship = true)
    public Statistics orders;
    @ForeignKey(stubbedRelationship = true)
    public Statistics tickets;

    @Relationship("event")
    @ForeignKey(onDelete = ForeignKeyAction.CASCADE)
    public Event event;
}
