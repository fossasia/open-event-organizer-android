import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import {Event} from "../../interfaces/event";
import {EventsService} from "../../services/events.service";
import {LocalStore} from "../../services/helper.service";
import {EventDashboardPage} from "../event-dashboard/event-dashboard";

@Component({
  templateUrl: 'build/pages/events/events.html',
  providers: [EventsService]
})

export class EventsPage {

  events: Event[];
  isLoading: boolean;
  pickedEvent: Event;

  constructor(private navCtrl: NavController, private eventService: EventsService, private localStore:LocalStore) {
    this.isLoading = true;
    if(this.localStore.has('event')) {
      this.pickedEvent = this.localStore.get('event')
    }
    eventService.getMyEvents().subscribe(
      res => {
        this.isLoading = false;
        this.events = res;
      },
      err => {
        this.isLoading = false;
        console.log(err);
      }
    )
  }

  pickEvent(event) {
    this.pickedEvent = event;
    this.localStore.set('event', event);
    this.navCtrl.push(EventDashboardPage)
  }

}
