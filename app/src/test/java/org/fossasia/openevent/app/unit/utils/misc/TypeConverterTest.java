package org.fossasia.openevent.app.unit.utils.misc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.fossasia.openevent.app.common.data.db.configuration.ObservableStringTypeConverter;
import org.fossasia.openevent.app.common.data.models.dto.ObservableString;
import org.fossasia.openevent.app.common.utils.json.ObservableStringDeserializer;
import org.fossasia.openevent.app.common.utils.json.ObservableStringSerializer;
import org.junit.Test;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TypeConverterTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObservableStringTypeConverter CONVERTER = new ObservableStringTypeConverter();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestModel {
        @JsonDeserialize(using = ObservableStringDeserializer.class)
        @JsonSerialize(using = ObservableStringSerializer.class)
        private ObservableString name;
    }

    @Test
    public void testStringToObservableStringJsonConversion() throws IOException {
        String test = "{ \"name\" : \"Ferris Bueller\" }";

        TestModel parsed = OBJECT_MAPPER.readValue(test, TestModel.class);

        assertNotNull(parsed.getName());
        assertEquals("Ferris Bueller", parsed.getName().get());
    }

    @Test
    public void testObservableStringToStringJsonConversion() throws JsonProcessingException {
        TestModel model = new TestModel(new ObservableString("John Wick"));

        String json = OBJECT_MAPPER.writeValueAsString(model);

        assertNotNull(json);
        assertEquals("{\"name\":\"John Wick\"}", json);
    }

    @Test
    public void testStringToObservableStringDbConversion() {
        String value = "Ferris Bueller";

        assertEquals(value, CONVERTER.getModelValue(value).get());
    }

    @Test
    public void testObservableStringToStringDbConversion() {
        String value = "John Wick";
        ObservableString observableString = new ObservableString(value);

        assertEquals(value, CONVERTER.getDBValue(observableString));
    }

    @Test
    public void testNullConversion() {
        assertNull(CONVERTER.getDBValue(null));
        assertNotNull(CONVERTER.getModelValue(null));
    }

}
