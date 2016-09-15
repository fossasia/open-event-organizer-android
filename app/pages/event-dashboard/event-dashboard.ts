import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import {Event} from "../../interfaces/event";
import {LocalStore} from "../../services/helper.service";
import {AttendeesService} from "../../services/attendees.service";
import {EventsService} from "../../services/events.service";
import {Attendee} from "../../interfaces/attende";
import {Ticket} from "../../interfaces/ticket";
import {DateFormatPipe} from "angular2-moment/index";
import {EventAttendeesPage} from "../event-attendees/event-attendees";

@Component({
  templateUrl: 'build/pages/event-dashboard/event-dashboard.html',
  providers: [EventsService, AttendeesService],
  pipes: [DateFormatPipe],
})

export class EventDashboardPage {

  event: Event;
  stats: {
    tickets: {
      sold:number,
      total:number
    },
    attendees: {
      total:number,
      present:number
    }
  };

  constructor(private navCtrl: NavController, private localStore:LocalStore, private eventsService:EventsService, private attendeesService:AttendeesService) {
    this.event = this.localStore.get('event');
    this.stats = {
      tickets: {
        sold: 0,
        total: 0
      },
      attendees: {
        total: 0,
        present: 0
      }
    };

    eventsService.getEvent(this.event.id).subscribe(
      event => {
        this.event = event;
        this.localStore.set('event', event);
        this.loadTicketsStats(event.tickets);
      },
      err => {
        console.log(err);
      }
    );

    attendeesService.loadAttendees(this.event.id).subscribe(
      attendees => {
        this.localStore.set('attendees', attendees);
        this.loadAttendeesStats(attendees);
      },
      err => {
        console.log(err);
      }
    )
  }

  loadTicketsStats(tickets: Ticket[]) {
    tickets.forEach((ticket: Ticket) => {
      this.stats.tickets.total += ticket.quantity;
    });
  }

  loadAttendeesStats(attendees: Attendee[]) {
    this.stats.attendees.total = this.stats.tickets.sold = attendees.length;
    attendees.forEach((attendee: Attendee) => {
      this.stats.tickets.total += (attendee.checked_in ? 1 : 0);
    });
  }

  percentage(value, total) {
    if (total <= 0) {
      return 0;
    }
    var result = ((value/total)*100);
    return Math.round(result);
  }

  openAttendeesList() {
    this.navCtrl.push(EventAttendeesPage);
  }
}
