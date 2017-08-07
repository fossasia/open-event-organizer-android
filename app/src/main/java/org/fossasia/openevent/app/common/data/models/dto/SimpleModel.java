package org.fossasia.openevent.app.common.data.models.dto;

import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.common.data.db.configuration.OrgaDatabase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(database = OrgaDatabase.class, allFields = true)
public class SimpleModel extends BaseModel implements Cloneable {
    @PrimaryKey
    public long id;

    public String name;
    public String description;

    SimpleModel() { }

    public static SimpleModel fromModel(SimpleModel model) {
        try {
            return (SimpleModel) model.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
