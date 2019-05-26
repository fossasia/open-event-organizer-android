package com.eventyay.organizer.data.faq;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import com.eventyay.organizer.data.event.Event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@Type("faq")
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "event")
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Entity
@EqualsAndHashCode
public class Faq {

    @Id(LongIdHandler.class)
    @PrimaryKey
    public Long id;

    @Relationship("event")
    @ForeignKey(entity = Event.class, parentColumns = "id", childColumns = "event", onDelete = ForeignKey.CASCADE)
    public Event event;

    public String question;
    public String answer;
}
