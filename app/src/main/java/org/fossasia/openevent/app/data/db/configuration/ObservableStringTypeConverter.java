package org.fossasia.openevent.app.data.db.configuration;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.fossasia.openevent.app.data.models.dto.ObservableString;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class ObservableStringTypeConverter extends TypeConverter<String, ObservableString> {

    @Override
    public String getDBValue(ObservableString observableString) {
        return observableString == null ? null : observableString.get();
    }

    @Override
    public ObservableString getModelValue(String s) {
        return new ObservableString(s);
    }
}
