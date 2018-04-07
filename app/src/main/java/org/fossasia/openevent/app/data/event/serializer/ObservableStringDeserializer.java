package org.fossasia.openevent.app.data.event.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ObservableStringDeserializer extends JsonDeserializer<ObservableString> {
    @Override
    public ObservableString deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return new ObservableString(jsonParser.getValueAsString());
    }
}
