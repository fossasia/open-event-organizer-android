package com.eventyay.organizer.data.ticket;

import com.eventyay.organizer.data.db.configuration.OrgaDatabase;
import com.raizlabs.android.dbflow.annotation.QueryModel;

import lombok.Data;

@Data
@QueryModel(database = OrgaDatabase.class, allFields = true)
public class TypeQuantity {
    public String type;
    public long quantity;
}
