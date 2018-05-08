package org.fossasia.openevent.app.data.sponsor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.core.sponsor.list.viewholder.SponsorsViewHolder;
import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.data.event.Event;
import org.fossasia.openevent.app.common.model.HeaderProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Delegate;

@Data
@Builder
@Type("sponsor")
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@EqualsAndHashCode(callSuper = false, exclude = { "sponsorDelegate", "checking" })
@Table(database = OrgaDatabase.class)
public class Sponsor extends AbstractItem<Sponsor, SponsorsViewHolder> implements Comparable<Sponsor>, HeaderProvider {

    //Should I remove the implementation of HeaderProvider?

    @Delegate(types = SponsorDelegate.class)
    private final SponsorDelegateImpl sponsorDelegate = new SponsorDelegateImpl(this);

    @Id(LongIdHandler.class)
    @PrimaryKey
    public long id;
    public String name;
    public String description;
    public String url;
    public String logoUrl;
    public int level;
    @JsonProperty("type")
    public String sponsorType;

    // To associate sponsors and event
    @Relationship("event")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Event event;

    public Sponsor() { }

}
