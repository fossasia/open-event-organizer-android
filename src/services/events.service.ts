import {Injectable} from "@angular/core";
import {Response} from "@angular/http";
import {AuthHttp} from "angular2-jwt";
import "rxjs/add/operator/map";
import {Observable} from "rxjs/Rx";
import {Config} from "../app/config";
import {IEvent} from "../interfaces/event";

@Injectable()
export class EventsService {

  constructor(public authHttp: AuthHttp) {
  }

  public getMyEvents(): Observable<IEvent[]> {
    return this.authHttp
      .get(Config.API_ENDPOINT + "/users/me/events")
      .map((res: Response) => res.json());
  }

  public getEvent(eventId): Observable<IEvent> {
    return this.authHttp
      .get(Config.API_ENDPOINT + "/events/" + eventId + "?include=tickets")
      .map((res: Response) => res.json());
  }
}
