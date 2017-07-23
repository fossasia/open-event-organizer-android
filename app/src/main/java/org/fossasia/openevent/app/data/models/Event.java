package org.fossasia.openevent.app.data.models;

import android.databinding.ObservableField;
import android.databinding.ObservableFloat;
import android.databinding.ObservableLong;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.fossasia.openevent.app.data.db.configuration.OrgaDatabase;
import org.fossasia.openevent.app.data.models.contract.IHeaderProvider;
import org.fossasia.openevent.app.utils.DateService;

import java.text.ParseException;
import java.util.List;

import lombok.Data;

@Data
@Type("event")
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class)
@Table(database = OrgaDatabase.class, allFields = true)
public class Event implements Comparable<Event>, IHeaderProvider {
    @Id(LongIdHandler.class)
    @PrimaryKey
    public long id;
    public String paymentCountry;
    public String paypalEmail;
    public String thumbnailImageUrl;
    public String schedulePublishedOn;
    public String paymentCurrency;
    public String organizerDescription;
    public boolean isMapShown;
    public String originalImageUrl;
    public String onsiteDetails;
    public String organizerName;
    public boolean canPayByStripe;
    public String largeImageUrl;
    public String timezone;
    public boolean canPayOnsite;
    public String deletedAt;
    public String ticketUrl;
    public boolean canPayByPaypal;
    public String locationName;
    public boolean isSponsorsEnabled;
    public boolean hasOrganizerInfo;
    public boolean isSessionsSpeakersEnabled;
    public String privacy;
    public String codeOfConduct;
    public String state;
    public double latitude;
    public String startsAt;
    public String searchableLocationName;
    public boolean isTicketingEnabled;
    public boolean canPayByCheque;
    public String description;
    public String pentabarfUrl;
    public String xcalUrl;
    public String logoUrl;
    public String externalEventUrl;
    public boolean isTaxEnabled;
    public String iconImageUrl;
    public String icalUrl;
    public String name;
    public boolean canPayByBank;
    public String endsAt;
    public String createdAt;
    public double longitude;
    public String bankDetails;
    public String chequeDetails;
    public String identifier;

    public boolean isComplete;

    @ColumnIgnore
    @Relationship("tickets")
    public List<Ticket> tickets;

    // For Data Binding
    public final ObservableField<String> startDate = new ObservableField<>();
    public final ObservableField<String> endDate = new ObservableField<>();
    public final ObservableField<String> eventStartTime = new ObservableField<>();

    public final ObservableLong totalAttendees = new ObservableLong();
    public final ObservableLong totalTickets = new ObservableLong();
    public final ObservableLong checkedIn = new ObservableLong();
    public final ObservableFloat totalSale = new ObservableFloat();

    public final ObservableLong freeTickets = new ObservableLong();
    public final ObservableLong paidTickets = new ObservableLong();
    public final ObservableLong donationTickets = new ObservableLong();

    public final ObservableLong soldFreeTickets = new ObservableLong();
    public final ObservableLong soldPaidTickets = new ObservableLong();
    public final ObservableLong soldDonationTickets = new ObservableLong();

    public Event() {}

    public Event(long id) {
        this.id = id;
    }

    public Event(long id, String startTime, String endTime) {
        this.id = id;
        this.startsAt = startTime;
        this.endsAt = endTime;
    }

    // One to Many implementation
    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "tickets")
    List<Ticket> getEventTickets() {
        if(tickets != null && !tickets.isEmpty()) {
            for (Ticket ticket : tickets)
                ticket.setEvent(this);

            return tickets;
        }

        tickets = SQLite.select()
            .from(Ticket.class)
            .where(Ticket_Table.event_id.eq(id))
            .queryList();

        return tickets;
    }

    @Override
    public int compareTo(@NonNull Event otherEvent) {
        return DateService.compareEventDates(this, otherEvent);
    }

    @Override
    public String getHeader() {
        try {
            return DateService.getEventStatus(this);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "INVALID";
    }

    @Override
    public long getHeaderId() {
        return getHeader().hashCode();
    }
}
