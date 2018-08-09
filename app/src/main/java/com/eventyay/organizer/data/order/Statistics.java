package com.eventyay.organizer.data.order;

import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import com.eventyay.organizer.data.db.configuration.OrgaDatabase;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Type("statistics")
@NoArgsConstructor
@ToString()
@Table(database = OrgaDatabase.class, allFields = true)
public class Statistics {

    @Id(LongIdHandler.class)
    @PrimaryKey
    public Long id;

    public Long draft;
    public Long cancelled;
    public Long pending;
    public Long placed;
    public long total;
    public Long expired;
    public long completed;
}
