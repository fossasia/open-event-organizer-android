/**
 * Created by Niranjan on 30-Aug-16.
 */
import {Injectable} from '@angular/core';
import {Response} from '@angular/http';
import 'rxjs/add/operator/map';
import {Observable} from "rxjs/Rx";
import {Event} from "../interfaces/event";
import {AuthHttp} from "angular2-jwt";
import {Config} from "../config";

@Injectable()
export class EventsService {

  constructor(public authHttp: AuthHttp) { }

  getMyEvents(): Observable<Event[]> {
    return this.authHttp.get(Config.API_ENDPOINT + "/users/me/events").map((res:Response) => res.json());
  }

  getEvent(eventId): Observable<Event> {
    return this.authHttp.get(Config.API_ENDPOINT + "/events/" + eventId + "?include=tickets").map((res:Response) => res.json());
  }
}
