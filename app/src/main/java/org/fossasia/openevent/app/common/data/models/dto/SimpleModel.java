package org.fossasia.openevent.app.common.data.models.dto;

import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.common.data.db.configuration.OrgaDatabase;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table(database = OrgaDatabase.class, allFields = true)
public class SimpleModel {
    @PrimaryKey
    public long id;

    public String name;
    public String description;

    SimpleModel() {}
}
