package org.fossasia.openevent.app.common.data.models;

import android.databinding.ObservableBoolean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.fossasia.openevent.app.common.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.common.data.models.delegates.TicketDelegate;
import org.fossasia.openevent.app.common.data.models.dto.ObservableString;
import org.fossasia.openevent.app.common.utils.json.ObservableStringDeserializer;
import org.fossasia.openevent.app.common.utils.json.ObservableStringSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Delegate;

@Data
@Builder
@Type("ticket")
@AllArgsConstructor
@ToString(exclude = "event")
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
@EqualsAndHashCode(exclude = { "ticketDelegate", "creating", "deleting" })
@SuppressWarnings("PMD.TooManyFields")
public class Ticket implements Comparable<Ticket> {

    @Delegate
    private final TicketDelegate ticketDelegate = new TicketDelegate(this);

    @Id(LongIdHandler.class)
    @PrimaryKey
    public Long id;

    public String description;
    public String type;
    public Float price;
    public String name;
    public Integer maxOrder;
    public Boolean isDescriptionVisible;
    public Boolean isFeeAbsorbed;
    public Integer position;
    public Long quantity;
    public Boolean isHidden;
    @JsonSerialize(using = ObservableStringSerializer.class)
    @JsonDeserialize(using = ObservableStringDeserializer.class)
    public ObservableString salesStartsAt = new ObservableString();
    @JsonSerialize(using = ObservableStringSerializer.class)
    @JsonDeserialize(using = ObservableStringDeserializer.class)
    public ObservableString salesEndsAt = new ObservableString();
    public Integer minOrder;

    @Relationship("event")
    @ForeignKey(stubbedRelationship = true, onDelete = ForeignKeyAction.CASCADE)
    public Event event;

    // Non model entities

    @JsonIgnore
    public final ObservableBoolean creating = new ObservableBoolean();
    @JsonIgnore
    public final ObservableBoolean deleting = new ObservableBoolean();

    public Ticket() { }
}
