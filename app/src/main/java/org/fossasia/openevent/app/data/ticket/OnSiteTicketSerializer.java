package org.fossasia.openevent.app.data.ticket;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

public class OnSiteTicketSerializer extends JsonSerializer<List<OnSiteTicket>> {

    @Override
    public void serialize(List<OnSiteTicket> value, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        for (OnSiteTicket model : value) {
            jgen.writeStartObject();
            jgen.writeObjectFieldStart("data");
            jgen.writeObjectField("attributes", model);
            jgen.writeObjectField("type", "on-site-ticket");
            jgen.writeEndObject();
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
    }
}
