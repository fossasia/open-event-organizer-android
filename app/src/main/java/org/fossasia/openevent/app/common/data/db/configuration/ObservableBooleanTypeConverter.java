package org.fossasia.openevent.app.common.data.db.configuration;

import android.databinding.ObservableBoolean;

import com.raizlabs.android.dbflow.converter.BooleanConverter;
import com.raizlabs.android.dbflow.converter.TypeConverter;

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class ObservableBooleanTypeConverter extends TypeConverter<Integer, ObservableBoolean> {

    @Override
    public Integer getDBValue(ObservableBoolean observableBoolean) {
        BooleanConverter converter = new BooleanConverter();
        return observableBoolean == null ? (Integer) 0 : converter.getDBValue(observableBoolean.get());
    }

    @Override
    public ObservableBoolean getModelValue(Integer integer) {
        BooleanConverter converter = new BooleanConverter();
        return new ObservableBoolean(integer != null && converter.getModelValue(integer));
    }

}
