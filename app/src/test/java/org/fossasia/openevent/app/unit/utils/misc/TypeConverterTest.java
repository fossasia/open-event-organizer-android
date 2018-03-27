package org.fossasia.openevent.app.unit.utils.misc;

import android.databinding.ObservableBoolean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.fossasia.openevent.app.data.db.configuration.ObservableBooleanTypeConverter;
import org.fossasia.openevent.app.data.db.configuration.ObservableStringTypeConverter;
import org.fossasia.openevent.app.data.models.dto.ObservableString;
import org.fossasia.openevent.app.data.models.serializer.ObservableStringDeserializer;
import org.fossasia.openevent.app.data.models.serializer.ObservableStringSerializer;
import org.junit.Test;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TypeConverterTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObservableStringTypeConverter STRING_CONVERTER = new ObservableStringTypeConverter();
    private static final ObservableBooleanTypeConverter BOOLEAN_CONVERTER = new ObservableBooleanTypeConverter();

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

        assertEquals(value, STRING_CONVERTER.getModelValue(value).get());
    }

    @Test
    public void testObservableStringToStringDbConversion() {
        String value = "John Wick";
        ObservableString observableString = new ObservableString(value);

        assertEquals(value, STRING_CONVERTER.getDBValue(observableString));
    }

    @Test
    public void testNullStringConversion() {
        assertNull(STRING_CONVERTER.getDBValue(null));
        assertNotNull(STRING_CONVERTER.getModelValue(null));
    }

    @Test
    public void testBooleanToObservableBooleanConversionTrue() {
        assertTrue(BOOLEAN_CONVERTER.getModelValue(1).get());
    }

    @Test
    public void testBooleanToObservableBooleanConversionFalse() {
        assertTrue(BOOLEAN_CONVERTER.getModelValue(1).get());
    }

    @Test
    public void testObservableBooleanToBooleanConversionTrue() {
        ObservableBoolean observableBoolean = new ObservableBoolean(true);
        assertEquals((Integer) 1, BOOLEAN_CONVERTER.getDBValue(observableBoolean));
    }

    @Test
    public void testObservableBooleanToBooleanConversionFalse() {
        ObservableBoolean observableBoolean = new ObservableBoolean(false);
        assertEquals((Integer) 0, BOOLEAN_CONVERTER.getDBValue(observableBoolean));
    }

    @Test
    public void testObservableBooleanToBooleanConversion() {
        ObservableBoolean observableBoolean = new ObservableBoolean();
        assertEquals((Integer) 0, BOOLEAN_CONVERTER.getDBValue(observableBoolean));
    }

    @Test
    public void testNullBooleanConversion() {
        assertEquals((Integer) 0, BOOLEAN_CONVERTER.getDBValue(null));
        assertFalse(BOOLEAN_CONVERTER.getModelValue(null).get());
    }

}
