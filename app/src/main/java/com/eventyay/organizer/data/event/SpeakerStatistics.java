package com.eventyay.organizer.data.event;


import com.eventyay.organizer.data.db.configuration.OrgaDatabase;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Type("speakerStatistics")
@NoArgsConstructor
@ToString()
@Table(database = OrgaDatabase.class, allFields = true)
public class SpeakerStatistics {

    @Id(LongIdHandler.class)
    @PrimaryKey
    public Long id;

    public Long accepted;
    public Long confirmed;
    public Long pending;
    public Long rejected;
    public Long total;
}
