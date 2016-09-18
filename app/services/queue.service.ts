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
    console.log(change);
  }

  processQueue() {
    this.db.allDocs({
      include_docs: true,
      attachments: true
    }).then(function (result) {
      result.rows.forEach(row => {
        let attendeeQueueDoc = row.doc;
        this.attendeeService.checkInOut(attendeeQueueDoc.event_id, attendeeQueueDoc.attendee.id, attendeeQueueDoc.attendee.checked_in).subscribe(
          attendeeResult => {
            console.log(attendeeResult);
            this.db.remove(attendeeQueueDoc)
          },
          err => {
            console.log(err);
          }
        )
      })
    }).catch(function (err) {
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
}
