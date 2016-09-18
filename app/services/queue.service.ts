/**
 * Created by niranjan94 on 9/18/16.
 */
import {Injectable} from '@angular/core';
import 'rxjs/add/operator/map';
import PouchDB = require('pouchdb');
import {AttendeeQueue} from "../interfaces/attendee.queue";
import {AttendeesService} from "./attendees.service";
import {UUID} from 'angular2-uuid'

@Injectable()
export class QueueService {
  db: any;

  constructor(private attendeeService: AttendeesService) {
    this.db = new PouchDB('queue');
  }

  setupQueueListener() {
    this.db.changes({
      since: 'now',
      live: true,
      include_docs: true
    }).on('change', (change) => {
      this.processQueueChange(change);
    }).on('complete', (info) => {
      console.log(info);
    }).on('error', (err) => {
      console.log(err);
    });
  }

  processQueueChange(change: any) {
    this.db.get(change.id).then((doc) => {
      this.processDocument(doc);
    });
  }

  processQueue() {
    this.db.allDocs({
      include_docs: true,
      attachments: true
    }).then( (result) => {
      result.rows.forEach(row => {
        this.processDocument(row.doc);
      })
    }).catch((err) => {
      console.log(err);
    });
  }

  addToQueue(attendeeQueue: AttendeeQueue) {
    this.db.put({
      _id: UUID.UUID(),
      event_id: attendeeQueue.event_id,
      attendee: attendeeQueue.attendee
    }).then(function (response) {
      console.log(response)
    }).catch(function (err) {
      console.log(err);
    });

  }

  processDocument(doc: any) {
    console.log(doc);
    if(doc.hasOwnProperty('attendee')) {
      this.attendeeService.checkInOut(doc.event_id, doc.attendee.id, doc.attendee.checked_in).subscribe(
        attendeeResult => {
          console.log(attendeeResult);
          this.db.remove(doc)
        },
        err => {
          console.log(err);
        }
      )
    }
  }
}
