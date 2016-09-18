/**
 * Created by Niranjan on 30-Aug-16.
 */
import {Injectable} from '@angular/core';
import {Response} from '@angular/http';
import 'rxjs/add/operator/map';
import {Observable} from "rxjs/Rx";
import {AuthHttp} from "angular2-jwt";
import {Config} from "../config";
import {Attendee} from "../interfaces/attende";
import PouchDB = require('pouchdb');
import {UUID} from 'angular2-uuid'

@Injectable()
export class AttendeesService {

  db: any;

  constructor(public authHttp: AuthHttp) {
    this.db = new PouchDB('queue')
  }

  loadAttendees(eventId): Observable<Attendee[]> {
    return this.authHttp.get(Config.API_ENDPOINT + "/events/" + eventId + "/attendees/").map((res:Response) => res.json());
  }

  checkInOut(eventId, attendeeId, isCheckedIn): Observable<Attendee> {
    var endpoint = Config.API_ENDPOINT + "/events/" + eventId + "/attendees/check_in_toggle/" + attendeeId + "/";
    if(isCheckedIn) {
      endpoint += "check_in";
    } else {
      endpoint += "check_out";
    }
    return this.authHttp.post(endpoint, {}).map((res:Response) => res.json());
  }
}
