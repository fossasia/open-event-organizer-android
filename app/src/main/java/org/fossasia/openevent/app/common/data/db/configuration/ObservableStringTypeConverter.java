package org.fossasia.openevent.app.common.data.db.configuration;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.fossasia.openevent.app.common.data.models.dto.ObservableString;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class ObservableStringTypeConverter extends TypeConverter<String, ObservableString> {

    @Override
    public String getDBValue(ObservableString observableString) {
        return observableString.get();
    }

    @Override
    public ObservableString getModelValue(String s) {
        return new ObservableString(s);
    }
}
