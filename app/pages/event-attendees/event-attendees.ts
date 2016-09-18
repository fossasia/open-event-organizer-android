import {Component, Pipe, PipeTransform} from '@angular/core';
import {NavController, ToastController } from 'ionic-angular';
import {Attendee} from "../../interfaces/attende";
import {AttendeesService} from "../../services/attendees.service";
import {LocalStore} from "../../services/helper.service";
import {Event} from "../../interfaces/event";
import {NgClass} from "@angular/common";
import { BarcodeScanner } from 'ionic-native';
import {QueueService} from "../../services/queue.service";

@Pipe({name: 'keys'})
export class KeysPipe implements PipeTransform {
  transform(value, args:string[]):any {
    let keys = [];
    for (let key in value) {
      keys.push({key: key, value: value[key]});
    }
    return keys;
  }
}

@Component({
  templateUrl: 'build/pages/event-attendees/event-attendees.html',
  providers: [AttendeesService, QueueService],
  pipes: [KeysPipe],
  directives: [NgClass],
})


export class EventAttendeesPage {

  attendees:Attendee[];
  attendeesGrouped:any;
  event:Event;
  query:string;

  constructor(private attendeesService:AttendeesService, private localStore:LocalStore, private toastCtrl: ToastController, private queueService: QueueService) {
    this.attendees = this.localStore.get('attendees');
    this.groupByAlphabets(this.attendees);
    this.event = this.localStore.get('event');
    attendeesService.loadAttendees(this.event.id).subscribe(
      attendees => {
        this.localStore.set('attendees', attendees);
        this.attendees = attendees;
        this.groupByAlphabets(this.attendees);
      },
      err => {
        console.log(err);
      }
    )
  }

  searchFilter() {
    this.groupByAlphabets(this.attendees, this.query);
  }

  checkIn(attendee) {
    attendee.checked_in = !attendee.checked_in;
    this.queueService.addToQueue({
      event_id: this.event.id,
      attendee: attendee
    });
  }

  groupByAlphabets(data, query = null) {

    if (query && query !== "") {
      data = data.filter(item => {
        return item.firstname.includes(query) || item.lastname.includes(query) || item.email.includes(query)
      });
    }

    var attendees = data.sort((a, b) => {
      var lastname1 = a.lastname.toUpperCase();
      var lastname2 = b.lastname.toUpperCase();
      if (lastname1 < lastname2) {
        return -1;
      }
      if (lastname1 > lastname2) {
        return 1;
      }
      return 0;
    });

    var attendeesGrouped = {};
    for (var i = 0; i < attendees.length; i++) {
      var letter = attendees[i].lastname.charAt(0).toUpperCase();
      if (attendeesGrouped[letter] == undefined) {
        attendeesGrouped[letter] = []
      }
      attendeesGrouped[letter].push(attendees[i]);
    }

    this.attendeesGrouped = attendeesGrouped;
  }

  scanQrCode() {
    BarcodeScanner.scan().then((barcodeData) => {
      let toast = this.toastCtrl.create({
        message: "Processing QR Code",
        duration: 1000
      });
      toast.present();
      this.attendeesService.checkInOut(this.event.id, barcodeData.text, true).subscribe(
        attendeeResult => {
          let toast = this.toastCtrl.create({
            message: attendeeResult.lastname + ", " + attendeeResult.firstname + " has been checked in.",
            duration: 500
          });
          toast.present();
        },
        err => {
          console.log(err);
          let toast = this.toastCtrl.create({
            message: 'Invalid QR Code. Please scan again.',
            duration: 500
          });
          toast.present();
        }
      );
    }, (err) => {
      console.log(err);
      let toast = this.toastCtrl.create({
        message: 'Invalid QR Code. Please scan again.',
        duration: 500
      });
      toast.present();
    });

  }


}
