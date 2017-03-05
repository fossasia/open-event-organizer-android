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
  public isLoading: boolean = true;

  private alphabetRe: RegExp;
  private qrRe: RegExp;

  constructor(private attendeesService: AttendeesService, private storage: Storage,
              private toastCtrl: ToastController, private queueService: QueueService) {

    // Matches an alphabet-only string
    this.alphabetRe = new RegExp("^[A-Za-z]");
    // Matches a valid QR Code pattern.
    this.qrRe = new RegExp("^[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}-[0-9]+$", "i");

    this.storage.get("event").then((event) => {
      this.event = event;
      attendeesService.loadAttendees(this.event.id).subscribe(
        (attendeesInner) => {
          this.storage.set("attendees", attendeesInner);
          this.attendees = attendeesInner;
          this.groupByAlphabets(this.attendees).then((attendeesGrouped) => {
            this.attendeesGrouped = attendeesGrouped;
            this.isLoading = false;
          });
        },
        () => {
          // Load from cache if server load fails for some reason
          this.storage.get("attendees").then((attendees) => {
            this.attendees = attendees;
            this.groupByAlphabets(this.attendees).then((attendeesGrouped) => {
              this.attendeesGrouped = attendeesGrouped;
              this.isLoading = false;
            });
          });
        },
      );
    });
  }

  public searchFilter(event) {
    this.groupByAlphabets(this.attendees, event.target.value).then((attendeesGrouped) => {
      this.attendeesGrouped = attendeesGrouped;
    });
  }

  public checkIn(attendee) {
    this.queueService
      .addToQueue({
        attendee_identifier: attendee.id,
        checked_in: attendee.checked_in,
        event_id: this.event.id,
      })
      .then(() => {
        attendee.checked_in = !attendee.checked_in;
      })
      .catch(() => {
        this.toastCtrl.create({
          duration: 500,
          message: "Error checking in attendee. Please try again..",
        }).present();
      });
  }

  public scanQrCode() {
    BarcodeScanner.scan().then((barcodeData) => {
      const identifier = barcodeData.text.replace("/", "-");
      if (!this.isQRValid(identifier)) {
        this.toastCtrl.create({
          duration: 1200,
          message: "Invalid QR Code. Please scan again",
        }).present();
        return;
      }

      this.queueService
        .addToQueue({
          attendee_identifier: identifier,
          checked_in: false,
          event_id: this.event.id,
        })
        .then(() => {
          this.toastCtrl.create({
            duration: 1000,
            message: "Attendee will be checked in",
          }).present();
        })
        .catch(() => {
          this.toastCtrl.create({
            duration: 1200,
            message: "Invalid QR Code. Please scan again.",
          }).present();
        });
    }, () => {
      this.toastCtrl.create({
        duration: 1200,
        message: "Only QR Codes are accepted.",
      }).present();
    });
  }

  private isQRValid(data: string): boolean {
    return this.qrRe.test(data);
  }

  private groupByAlphabets(data, query = null) {
    return new Promise((resolve) => {
      if (query && query !== "") {
        data = data.filter(
          (item) => item.firstname.includes(query) || item.lastname.includes(query) || item.email.includes(query),
        );
      }
      let attendees = data.sort((a, b) => {
        let name1 = a.lastname.toUpperCase();
        let name2 = b.lastname.toUpperCase();
        return name1 < name2 ? -1 : (name1 > name2 ? 1 : 0);
      });

      let attendeesGrouped = {};
      attendees.forEach((attendee) => {
        let lastname = attendee.lastname;
        const firstname = attendee.firstname;
        let letter = lastname.charAt(0).toUpperCase();
        if (!this.alphabetRe.test(letter)) {
          lastname = "";
        }
        const tempName = lastname + firstname;

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

      resolve(this.orderKeys(attendeesGrouped));
    });
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
