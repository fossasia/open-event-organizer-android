package org.fossasia.openevent.app.data.ticket;

import com.raizlabs.android.dbflow.annotation.QueryModel;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;

import lombok.Data;

@Data
@QueryModel(database = OrgaDatabase.class, allFields = true)
public class TypeQuantity {
    public String type;
    public long quantity;
}
