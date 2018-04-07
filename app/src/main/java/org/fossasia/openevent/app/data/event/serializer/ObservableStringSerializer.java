package org.fossasia.openevent.app.data.event.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ObservableStringSerializer extends JsonSerializer<ObservableString> {
    @Override
    public void serialize(ObservableString observableString, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(observableString.get());
    }
}
