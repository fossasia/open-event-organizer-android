import {Component} from "@angular/core";
import {Storage} from "@ionic/storage";
import {NavController} from "ionic-angular";
import {IEvent} from "../../interfaces/event";
import {EventsService} from "../../services/events.service";
import {EventDashboardPage} from "../event-dashboard/event-dashboard";

@Component({
  providers: [EventsService],
  selector: "events-page",
  templateUrl: "events.html",
})

export class EventsPage {

  public events: IEvent[];
  public isLoading: boolean;
  public pickedEvent: IEvent;

  constructor(private navCtrl: NavController, private eventService: EventsService, private storage: Storage) {
    this.isLoading = true;

    this.storage.get("event").then((event) => {
      if (event) {
        this.pickedEvent = event;
      }
    });

    eventService.getMyEvents().subscribe(
      (res) => {
        this.isLoading = false;
        this.events = res;
      },
      () => {
        this.isLoading = false;
      },
    );
  }

  public pickEvent(event) {
    this.pickedEvent = event;
    this.storage.set("event", event).then(() => {
      this.navCtrl.push(EventDashboardPage);
    });
  }
}
