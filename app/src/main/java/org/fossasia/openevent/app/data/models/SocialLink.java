package org.fossasia.openevent.app.data.models;

import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Table(database = OrgaDatabase.class, allFields = true)
public class SocialLink extends BaseModel {
    @PrimaryKey
    public long id;
    public String link;
    public String name;

    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Event event;
}
