package com.eventyay.organizer.data.ticket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@Type("on-site-ticket")
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@EqualsAndHashCode()
public class OnSiteTicket {

    @Id(LongIdHandler.class)
    @PrimaryKey
    @JsonIgnore
    public Long ticketId;

    @JsonSerialize(using = ToStringSerializer.class)
    public Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    public Long quantity;

}
