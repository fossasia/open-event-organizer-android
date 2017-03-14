import {Component} from "@angular/core";
import {Storage} from "@ionic/storage";
import {NavController} from "ionic-angular";
import {IAttendee} from "../../interfaces/attende";
import {IEvent} from "../../interfaces/event";
import {ITicket} from "../../interfaces/ticket";
import {AttendeesService} from "../../services/attendees.service";
import {EventsService} from "../../services/events.service";
import { EventAttendeesPage } from "../event-attendees/event-attendees";
import { NetworkCheck } from "../../services/network-check.service";

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

  constructor(private navCtrl: NavController, private storage: Storage, private eventsService: EventsService,
              private attendeesService: AttendeesService, private networkCheckService: NetworkCheck) {

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

    this.storage.get("event").then((event) => {
      this.event = event;
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
    this.stats.tickets.total = 0;
    tickets.forEach((ticket: ITicket) => {
      this.stats.tickets.total += ticket.quantity;
    });
  }

  private loadAttendeesStats(attendees: IAttendee[]) {
    this.stats.attendees.total = this.stats.tickets.sold = attendees.length;
    this.stats.attendees.present = 0;
    attendees.forEach((attendee: IAttendee) => {
      this.stats.attendees.present += (attendee.checked_in ? 1 : 0);
    });
  }

  private loadPageData(refresher: any, isRefresher: boolean = false) {
    this.isLoading = true;
    this.eventsService.getEvent(this.event.id).subscribe(
      (eventInner) => {
        this.event = eventInner;
        this.storage.set("event", eventInner);
        this.loadTicketsStats(eventInner.tickets);
        this.attendeesService.loadAttendees(this.event.id).subscribe(
          (attendees) => {
            this.storage.set("attendees", attendees);
            this.loadAttendeesStats(attendees);
            this.isLoading = false;
            if(isRefresher) {
              refresher.complete();
            }
          },
          () => {
            this.isLoading = false;
            if(isRefresher) {
              refresher.complete();
            }
            this.networkCheckService.showNoNetworkAlert();
          },
        );
      },
      () => {
        this.isLoading = false;
        if(isRefresher) {
          refresher.complete();
        }
        this.networkCheckService.showNoNetworkAlert();
      },
    );
  }
}
