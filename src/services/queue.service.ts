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
    }).on("error", () => {
      // TODO Should show error
    });
  }

  public processQueueChange(change: any) {
    this.db.get(change.id).then((doc) => {
      this.processDocument(doc);
    }).catch(() => {
      // Can ignore error
    });
  }

  public processQueue() {
    this.db.allDocs({
      attachments: true,
      include_docs: true,
    }).then((result) => {
      result.rows.forEach((row) => {
        this.processDocument(row.doc);
      });
    }).catch(() => {
      // TODO Should show error
    });
  }

  public addToQueue(attendeeQueue: IAttendeeQueue) {
    this.db.put({
      _id: UUID.UUID(),
      attendee: attendeeQueue.attendee,
      event_id: attendeeQueue.event_id,
    }).catch(() => {
      // TODO Should show error
    });

  }

  private processDocument(doc: any) {
    if (doc.hasOwnProperty("attendee")) {
      this.attendeeService.checkInOut(doc.event_id, doc.attendee.id, doc.attendee.checked_in).subscribe(
        () => {
          this.db.remove(doc).catch(() => {
            // TODO Should show error
          });
        },
        () => {
          // TODO Should show error
        },
      );
    }
  }
}
