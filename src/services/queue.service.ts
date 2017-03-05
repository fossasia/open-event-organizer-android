import {Injectable} from "@angular/core";
import {UUID} from "angular2-uuid";
import * as PouchDB from "pouchdb";
import "rxjs/add/operator/map";
import {IAttendeeQueue} from "../interfaces/attendee.queue";
import {AttendeesService} from "./attendees.service";

@Injectable()
export class QueueService {

  private db: any;

  constructor(private attendeeService: AttendeesService) {
    this.db = new PouchDB("queue");
  }

  public setupQueueListener() {
    this.db.changes({
      include_docs: true,
      live: true,
      since: "now",
    }).on("change", (change) => {
      this.processQueueChange(change);
    }).on("error", () => { /* Errors can be ignored for now */ });
  }

  public processQueueChange(change: any) {
    this.db.get(change.id).then((doc) => {
      this.processDocument(doc);
    }).catch(() => { /* Errors can be ignored for now */ });
  }

  public processQueue() {
    this.db.allDocs({
      attachments: true,
      include_docs: true,
    }).then((result) => {
      result.rows.forEach((row) => {
        this.processDocument(row.doc);
      });
    }).catch(() => { /* Errors can be ignored for now. Queue will be processed again on next attempt. */ });
  }

  public addToQueue(attendeeQueue: IAttendeeQueue): Promise<any> {
    return this.db.put({
      _id: UUID.UUID(),
      attendee_identifier: attendeeQueue.attendee_identifier,
      checked_in: attendeeQueue.checked_in,
      event_id: attendeeQueue.event_id,
    });
  }

  private processDocument(doc: any) {
    if (doc.hasOwnProperty("attendee_identifier")) {
      this.attendeeService.checkInOut(doc.event_id, doc.attendee_identifier, doc.checked_in).subscribe(
        () => {
          this.db.remove(doc).catch(() => { /* Errors can be ignored for now */ });
        },
        () => { /* Errors can be ignored since processing will be attempted the next time queue is processed */ },
      );
    }
  }
}
