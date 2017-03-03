import {Injectable} from "@angular/core";
import {Response} from "@angular/http";
import {AuthHttp} from "angular2-jwt";
import * as PouchDB from "pouchdb";
import "rxjs/add/operator/map";
import {Observable} from "rxjs/Rx";
import {Config} from "../app/config";
import {IAttendee} from "../interfaces/attende";

@Injectable()
export class AttendeesService {

  private db: any;

  constructor(public authHttp: AuthHttp) {
    this.db = new PouchDB("queue");
  }

  public loadAttendees(eventId): Observable<IAttendee[]> {
    return this.authHttp
      .get(Config.API_ENDPOINT + "/events/" + eventId + "/attendees/")
      .map((res: Response) => res.json());
  }

  public checkInOut(eventId, attendeeId, isCheckedIn): Observable<IAttendee> {
    let endpoint = Config.API_ENDPOINT + "/events/" + eventId + "/attendees/check_in_toggle/" + attendeeId + "/";
    if (isCheckedIn) {
      endpoint += "check_in";
    } else {
      endpoint += "check_out";
    }
    return this.authHttp
      .post(endpoint, {})
      .map((res: Response) => res.json());
  }
}
