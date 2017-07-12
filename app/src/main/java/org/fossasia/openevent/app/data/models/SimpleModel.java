package org.fossasia.openevent.app.data.models;

import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(database = OrgaDatabase.class, allFields = true)
public class SimpleModel {
    @PrimaryKey
    public long id;

    public String name;
    public String description;

    public SimpleModel() {}
}
