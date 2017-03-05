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

  private alphabetRe: RegExp;

  constructor(private attendeesService: AttendeesService, private storage: Storage,
              private toastCtrl: ToastController, private queueService: QueueService) {

    this.alphabetRe = new RegExp("^[A-Za-z]");

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
      const toast = this.toastCtrl.create({
        duration: 1000,
        message: "Processing QR Code",
      });
      toast.present();
      this.attendeesService.checkInOut(this.event.id, barcodeData.text, true).subscribe(
        (attendeeResult) => {
          const toastSecondary = this.toastCtrl.create({
            duration: 1000,
            message: attendeeResult.firstname + " " + attendeeResult.lastname + " has been checked in.",
          });
          toast.dismiss();
          toastSecondary.present();
        },
        () => {
          const toastSecondary = this.toastCtrl.create({
            duration: 500,
            message: "Invalid QR Code. Please scan again.",
          });
          toast.dismiss();
          toastSecondary.present();
        },
      );
    }, () => {
      const toastSecondary = this.toastCtrl.create({
        duration: 500,
        message: "Only QR Codes are accepted.",
      });
      toastSecondary.present();
    });
  }

  private groupByAlphabets(data, query = null) {

    if (query && query !== "") {
      data = data.filter(
        item => item.firstname.includes(query) || item.lastname.includes(query) || item.email.includes(query)
      );
    }

    let attendees = data.sort((a, b) => {
      let name1 = a.lastname.toUpperCase();
      let name2 = b.lastname.toUpperCase();
      return name1 < name2 ? -1 : (name1 > name2 ? 1 : 0);
    });

    let attendeesGrouped = {};
    attendees.forEach((attendee) => {
      let letter = attendee.lastname.charAt(0).toUpperCase();
      if (!this.alphabetRe.test(letter)) {
        attendee.lastname = "";
      }
      const tempName = attendee.lastname + attendee.firstname;

      if (tempName.trim().length === 0) {
        return;
      }

      letter = tempName.charAt(0).toUpperCase();
      if (!this.alphabetRe.test(letter)) {
        letter = "1@#";
      }
      if (attendeesGrouped[letter] === undefined) {
        attendeesGrouped[letter] = [];
      }
      attendeesGrouped[letter].push(attendee);
    });

    this.attendeesGrouped = this.orderKeys(attendeesGrouped);
  }

  private orderKeys(obj): any {
    let keys = Object.keys(obj).sort((k1, k2) => (k1 < k2) ? -1 : ((k1 > k2) ? 1 : 0));
    let i;
    let after = {};
    for (i = 0; i < keys.length; i++) {
      after[keys[i]] = obj[keys[i]];
      delete obj[keys[i]];
    }

    for (i = 0; i < keys.length; i++) {
      obj[keys[i]] = after[keys[i]];
    }
    return obj;
  }

}
