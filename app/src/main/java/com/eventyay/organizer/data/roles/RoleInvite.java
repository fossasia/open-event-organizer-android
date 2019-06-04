package com.eventyay.organizer.data.roles;

import com.eventyay.organizer.data.event.Event;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Type("role-invite")
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
public class RoleInvite {

    @Id(LongIdHandler.class)
    public Long id;

    @Relationship("event")
    public Event event;

    @Relationship("role")
    public Role role;

    public String email;
    public String createdAt;
    public String status;
    public String roleName;
}
