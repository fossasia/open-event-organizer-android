package org.fossasia.openevent.app.common.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.fossasia.openevent.app.common.data.models.dto.ObservableString;

import java.io.IOException;

public class ObservableStringDeserializer extends JsonDeserializer<ObservableString> {
    @Override
    public ObservableString deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return new ObservableString(jsonParser.getValueAsString());
    }
}
