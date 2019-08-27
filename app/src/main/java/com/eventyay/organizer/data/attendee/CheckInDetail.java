package com.eventyay.organizer.data.attendee;

import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInDetail {

    @Id(LongIdHandler.class)
    public long id;

    public String checkTime;
    public String scanAction;
}
