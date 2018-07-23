package org.fossasia.openevent.app.data.attendee;

import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInDetail {

    @Id(LongIdHandler.class)
    public long id;

    public String checkTime;
    public String scanAction;
}
