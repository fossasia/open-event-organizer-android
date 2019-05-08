package com.eventyay.organizer.data.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Type("event-statistics-general")
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class EventStatistics {

    @Id
    public String id;

    public Long sessions;
    public SpeakerStatistics speakers;
    public Long sessionsPending;
    public Long sponsors;
    public Long sessionsSubmitted;
    public Long sessionsRejected;
    public String identifier;
    public Long sessionsConfirmed;
    public Long sessionsAccepted;
    public Long sessionsDraft;
}

