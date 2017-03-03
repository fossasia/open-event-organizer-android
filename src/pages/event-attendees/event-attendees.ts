import {Component} from "@angular/core";
import {Storage} from "@ionic/storage";
import {ToastController} from "ionic-angular";
import {BarcodeScanner} from "ionic-native";
import {IAttendee} from "../../interfaces/attende";
import {IEvent} from "../../interfaces/event";
import {AttendeesService} from "../../services/attendees.service";
import {QueueService} from "../../services/queue.service";

@Component({
  providers: [AttendeesService, QueueService],
  selector: "event-attendees-page",
  templateUrl: "event-attendees.html",
})

export class EventAttendeesPage {

  public attendees: IAttendee[];
  public attendeesGrouped: any;
  public event: IEvent;

  constructor(private attendeesService: AttendeesService, private storage: Storage,
              private toastCtrl: ToastController, private queueService: QueueService) {

    this.storage.get("attendees").then((attendees) => {
      this.attendees = attendees;
      this.groupByAlphabets(this.attendees);
      this.storage.get("event").then((event) => {
        this.event = event;
        attendeesService.loadAttendees(this.event.id).subscribe(
          (attendeesInner) => {
            this.storage.set("attendees", attendeesInner);
            this.attendees = attendeesInner;
            this.groupByAlphabets(this.attendees);
          },
          () => {
            // Show errors
          },
        );
      });
    });
  }

  public searchFilter(event) {
    this.groupByAlphabets(this.attendees, event.target.value);
  }

  public checkIn(attendee) {
    attendee.checked_in = !attendee.checked_in;
    this.queueService.addToQueue({
      event_id: this.event.id,
      attendee,
    });
  }

  public scanQrCode() {
    BarcodeScanner.scan().then((barcodeData) => {
      let toast = this.toastCtrl.create({
        duration: 1000,
        message: "Processing QR Code",
      });
      toast.present();
      this.attendeesService.checkInOut(this.event.id, barcodeData.text, true).subscribe(
        (attendeeResult) => {
          let toastSecondary = this.toastCtrl.create({
            duration: 500,
            message: attendeeResult.lastname + ", " + attendeeResult.firstname + " has been checked in.",
          });
          toastSecondary.present();
        },
        () => {
          let toastSecondary = this.toastCtrl.create({
            duration: 500,
            message: "Invalid QR Code. Please scan again.",
          });
          toastSecondary.present();
        },
      );
    }, () => {
      let toastSecondary = this.toastCtrl.create({
        duration: 500,
        message: "Invalid QR Code. Please scan again.",
      });
      toastSecondary.present();
    });
  }

  private groupByAlphabets(data, query = null) {

    if (query && query !== "") {
      data = data.filter((item) => {
        return item.firstname.includes(query) || item.lastname.includes(query) || item.email.includes(query);
      });
    }

    let attendees = data.sort((a, b) => {
      let lastname1 = a.lastname.toUpperCase();
      let lastname2 = b.lastname.toUpperCase();
      if (lastname1 < lastname2) {
        return -1;
      }
      if (lastname1 > lastname2) {
        return 1;
      }
      return 0;
    });

    let attendeesGrouped = {};
    attendees.forEach((attendee) => {
      let letter = attendee.lastname.charAt(0).toUpperCase();
      if (attendeesGrouped[letter] === undefined) {
        attendeesGrouped[letter] = [];
      }
      attendeesGrouped[letter].push(attendee);
    });

    this.attendeesGrouped = attendeesGrouped;
  }
}
