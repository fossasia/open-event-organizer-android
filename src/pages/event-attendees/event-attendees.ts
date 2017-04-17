import { Component } from "@angular/core";
import { Storage } from "@ionic/storage";
import { AlertController, ToastController } from "ionic-angular";
import { BarcodeScanner } from "ionic-native";
import { IAttendee } from "../../interfaces/attende";
import { IEvent } from "../../interfaces/event";
import { AttendeesService } from "../../services/attendees.service";
import { NetworkCheck } from "../../services/network-check.service";
import { QueueService } from "../../services/queue.service";

@Component({
  providers: [AttendeesService, QueueService],
  selector: "event-attendees-page",
  templateUrl: "event-attendees.html",
})

export class EventAttendeesPage {

  public attendees: IAttendee[];
  public attendeesGrouped: any;
  public event: IEvent;
  public isLoadingFirstTime: boolean = true;
  public isLoading: boolean;

  private alphabetRe: RegExp;
  private qrRe: RegExp;
  private attendeesStorageKey: string;

  constructor(private attendeesService: AttendeesService, private storage: Storage,
              private toastCtrl: ToastController, private queueService: QueueService,
              private networkCheckService: NetworkCheck, private alertCtrl: AlertController) {
    // Matches an alphabet-only string
    this.alphabetRe = new RegExp("^[A-Za-z]");
    // Matches a valid QR Code pattern.
    this.qrRe = new RegExp("^[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}-[0-9]+$", "i");

    this.storage.get("event").then((event) => {
      this.event = event;

      // Cache attendees for each event individually in a format "attendees_eventid'
      this.attendeesStorageKey = "attendees_" + this.event.id;

      // Update UI from Cache initially.
      this.storage.get(this.attendeesStorageKey).then((attendees) => {
        this.attendees = attendees;
        this.groupByAlphabets(this.attendees).then((attendeesGrouped) => {
          this.attendeesGrouped = attendeesGrouped;
        });
      });

      this.loadPageData(0);

    });
  }

  public doRefresh(refresher) {
    this.loadPageData(refresher, true);
  }

  public searchFilter(event) {
    this.groupByAlphabets(this.attendees, event.target.value).then((attendeesGrouped) => {
      this.attendeesGrouped = attendeesGrouped;
    });
  }

  public checkIn(attendee: IAttendee) {
    const confirm = this.alertCtrl.create({
      title: attendee.checked_in ? "Checking Out" : "Checking In",
      subTitle: attendee.firstname + " " + attendee.lastname,
      message: "Ticket: " + attendee.ticket.name,
      buttons: [
        {
          text: "Cancel",
          handler: () => {
            // cancelled
          },
        },
        {
          text: "Ok",
          handler: () => {
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
          },
        },
      ],
    });
    confirm.present();
  }

  public scanQrCode() {
    BarcodeScanner.scan().then((barcodeData) => {
      const identifier = barcodeData.text.replace("/", "-");
      if (this.qrRe.test(identifier)) {
        const orderIdentifier: string = identifier.substr(0, 36);
        const attendeeId: number = +identifier.substring(37);
        for (const attendee of this.attendees) {
          if (orderIdentifier === attendee.order.identifier && attendeeId === attendee.id) {
            if (attendee.checked_in) {
              this.presentToastCtrl("Already Checked In!");
              return;
            }
            const confirm = this.alertCtrl.create({
              title: "Checking In",
              subTitle: attendee.firstname + " " + attendee.lastname,
              message: "Ticket: " + attendee.ticket.name,
              buttons: [
                {
                  text: "Cancel",
                  handler: () => {
                    this.presentToastCtrl("Check In cancelled.");
                  },
                },
                {
                  text: "Ok",
                  handler: () => {
                    this.queueService
                      .addToQueue({
                        attendee_identifier: identifier,
                        checked_in: false,
                        event_id: this.event.id,
                      })
                      .then(() => {
                        this.presentToastCtrl("Attendee will be checked in");
                      })
                      .catch(() => {
                        this.presentToastCtrl("Invalid QR Code. Please scan again.");
                      });
                  },
                },
              ],
            });
            confirm.present();
            return;
          }
        }
        this.presentToastCtrl("Invalid QR Code. Please scan again");
      } else {
        this.presentToastCtrl("Invalid QR Code. Please scan again");
      }
    }, () => {
      this.presentToastCtrl("Only QR Codes are accepted.");
    });
  }

  private presentToastCtrl(messageBody: string) {
    this.toastCtrl.create({
      duration: 1200,
      message: messageBody,
    }).present();
  }

  private groupByAlphabets(data, query = null) {
    return new Promise((resolve) => {
      if (query && query !== "") {
        query = query.toLowerCase();
        data = data.filter(
          (item) => item.firstname.toLowerCase().includes(query) ||
            item.lastname.toLowerCase().includes(query) || item.email.toLowerCase().includes(query),
        );
      }
      const attendees = data.sort((a, b) => {
        const name1 = a.lastname.toUpperCase();
        const name2 = b.lastname.toUpperCase();
        return name1 < name2 ? -1 : (name1 > name2 ? 1 : 0);
      });

      const attendeesGrouped = {};
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
    const keys = Object.keys(obj).sort((k1, k2) => (k1 < k2) ? -1 : ((k1 > k2) ? 1 : 0));
    let i;
    const after = {};
    for (i = 0; i < keys.length; i++) {
      after[keys[i]] = obj[keys[i]];
      delete obj[keys[i]];
    }

    for (i = 0; i < keys.length; i++) {
      obj[keys[i]] = after[keys[i]];
    }
    return obj;
  }

  private loadPageData(refresher: any, isRefresher: boolean = false) {
    this.isLoading = true;
    this.attendeesService.loadAttendees(this.event.id).subscribe(
      (attendeesInner) => {
        // Store new data in cache and update UI
        this.storage.set(this.attendeesStorageKey, attendeesInner);
        this.attendees = attendeesInner;
        this.groupByAlphabets(attendeesInner).then((attendeesGrouped) => {
          this.attendeesGrouped = attendeesGrouped;
          this.isLoading = false;
          this.isLoadingFirstTime = false;
          if (isRefresher) {
            refresher.complete();
          }
        });
      },
      () => {
        this.isLoading = false;
        this.isLoadingFirstTime = false;
        if (isRefresher) {
          refresher.complete();
        }
        this.networkCheckService.showNoNetworkAlert();
      },
    );
  }

}
