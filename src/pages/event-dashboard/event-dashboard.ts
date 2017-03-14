import {Component} from "@angular/core";
import {Storage} from "@ionic/storage";
import {NavController} from "ionic-angular";
import {IAttendee} from "../../interfaces/attende";
import {IEvent} from "../../interfaces/event";
import {ITicket} from "../../interfaces/ticket";
import {AttendeesService} from "../../services/attendees.service";
import {EventsService} from "../../services/events.service";
import {EventAttendeesPage} from "../event-attendees/event-attendees";
import {NetworkCheck} from "../../services/network-check.service";

@Component({
  providers: [EventsService, AttendeesService],
  selector: "event-dashboard-page",
  templateUrl: "event-dashboard.html",
})

export class EventDashboardPage {

  public event: IEvent;

  public stats: {
    attendees: {
      present: number,
      total: number,
    },
    tickets: {
      sold: number,
      total: number,
    },
  };

  public isLoading: boolean = true;

  private eventStorageKey: string;
  private attendeesStorageKey: string;

  constructor(private navCtrl: NavController,
              private storage: Storage,
              private eventsService: EventsService,
              private attendeesService: AttendeesService,
              private networkCheckService: NetworkCheck) {

    this.stats = {
      attendees: {
        present: 0,
        total: 0,
      },
      tickets: {
        sold: 0,
        total: 0,
      },
    };

    this.isLoading = true;
    this.storage.get("event").then((event) => {

      this.event = event;

      this.eventStorageKey = "event_" + this.event.id;
      this.attendeesStorageKey = "attendees_" + this.event.id;

      this.storage.get(this.eventStorageKey).then((cachedEvent) => {
        if (cachedEvent != null) {
          this.event = cachedEvent;
          this.loadTicketsStats(this.event.tickets);
        }
      });

      this.storage.get(this.attendeesStorageKey).then((cachedAttendees) => {
        if (cachedAttendees != null) {
          this.loadAttendeesStats(cachedAttendees);
        }
      });

      this.loadPageData(0);

    });
  }

  public percentage(value, total) {
    if (total <= 0) {
      return 0;
    }
    const result = ((value / total) * 100);
    return Math.round(result);
  }

  public openAttendeesList() {
    this.navCtrl.push(EventAttendeesPage);
  }

  public doRefresh(refresher) {
    this.loadPageData(refresher, true);
  }

  private loadTicketsStats(tickets: ITicket[]) {
    let totalTickets = 0;
    tickets.forEach((ticket: ITicket) => {
      totalTickets += ticket.quantity;
    });
    this.stats.tickets.total = totalTickets;
  }

  private loadAttendeesStats(attendees: IAttendee[]) {
    this.stats.attendees.total = this.stats.tickets.sold = attendees.length;
    let presentAttendees = 0;
    attendees.forEach((attendee: IAttendee) => {
      presentAttendees += (attendee.checked_in ? 1 : 0);
    });
    this.stats.attendees.present = presentAttendees;
  }

  private loadPageData(refresher: any, isRefresher: boolean = false) {
    this.isLoading = true;
    this.eventsService.getEvent(this.event.id).subscribe(
      (eventInner) => {
        this.event = eventInner;
        this.storage.set(this.eventStorageKey, eventInner);
        this.loadTicketsStats(eventInner.tickets);
        this.attendeesService.loadAttendees(this.event.id).subscribe(
          (attendees) => {
            this.storage.set(this.attendeesStorageKey, attendees);
            this.loadAttendeesStats(attendees);
            this.isLoading = false;
            if (isRefresher) {
              refresher.complete();
            }
          },
          () => {
            this.isLoading = false;
            if (isRefresher) {
              refresher.complete();
            }
            this.networkCheckService.showNoNetworkAlert();
          },
        );
      },
      () => {
        this.isLoading = false;
        if (isRefresher) {
          refresher.complete();
        }
        this.networkCheckService.showNoNetworkAlert();
      },
    );
  }
}
