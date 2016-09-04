import {Component, Pipe, PipeTransform} from '@angular/core';
import {NavController, ToastController } from 'ionic-angular';
import {Attendee} from "../../interfaces/attende";
import {AttendeesService} from "../../services/attendees.service";
import {LocalStore} from "../../services/helper.service";
import {Event} from "../../interfaces/event";
import {NgClass} from "@angular/common";

/*
 Generated class for the EventAttendeesPage page.

 See http://ionicframework.com/docs/v2/components/#navigation for more info on
 Ionic pages and navigation.
 */

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
  providers: [AttendeesService],
  pipes: [KeysPipe],
  directives: [NgClass],
})


export class EventAttendeesPage {

  attendees:Attendee[];
  attendeesGrouped:any;
  event:Event;
  query:string;

  constructor(private attendeesService:AttendeesService, private localStore:LocalStore, private toastCtrl: ToastController) {
    this.attendees = this.localStore.get('attendees');
    this.groupByAlphabets(this.attendees);
    this.event = this.localStore.get('event');
    attendeesService.loadAttendees(this.event.id).subscribe(
      attendees => {
        this.localStore.set('attendees', attendees);
        this.attendees = attendees;
        this.groupByAlphabets(this.attendees);
        console.log(this.attendeesGrouped);
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
    this.attendeesService.checkInOut(this.event.id, attendee.id, attendee.checked_in).subscribe(
      attendeeResult => {
        attendee = attendeeResult;
      },
      err => {
        console.log(err);
        attendee.checked_in = !attendee.checked_in;
        let toast = this.toastCtrl.create({
          message: 'An error occurred. Please try again later.',
          duration: 1000
        });
        toast.present();
      }
    )
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

    console.log(attendees);


    var attendeesGrouped = {};
    for (var i = 0; i < attendees.length; i++) {
      var letter = attendees[i].lastname.charAt(0).toUpperCase();
      if (attendeesGrouped[letter] == undefined) {
        attendeesGrouped[letter] = []
      }
      attendeesGrouped[letter].push(attendees[i]);
    }

    console.log(attendeesGrouped);


    this.attendeesGrouped = attendeesGrouped;
  }


}
