import {Component, ViewChild} from '@angular/core';
import {Platform, ionicBootstrap, Nav} from 'ionic-angular';
import {StatusBar} from 'ionic-native';
import {HTTP_PROVIDERS} from "@angular/http";
import {provideAuth, tokenNotExpired} from 'angular2-jwt';
import {Config} from "./config";
import {LocalStore} from "./services/helper.service";
import {EventsPage} from "./pages/events/events";
import {LoginPage} from "./pages/login/login";
import {Network} from 'ionic-native';
import {QueueService} from "./services/queue.service";
import {AttendeesService} from "./services/attendees.service";

@Component({
  templateUrl: 'build/app.html',
  providers: [QueueService, AttendeesService],
  queries: {
    nav: new ViewChild('content')
  }
})

export class OrganizerApp {

  @ViewChild(Nav) nav: Nav;

  constructor(private platform: Platform, private queueService: QueueService) {
    platform.ready().then(() => {
      StatusBar.styleDefault();
      this.handleRoot();
      this.handleNetworkChanges();
    });
    this.queueService.setupQueueListener();
    this.queueService.processQueue();
  }

  handleRoot() {
    if (tokenNotExpired(Config.ACCESS_TOKEN_NAME)) {
      this.nav.setRoot(EventsPage);
    } else {
      this.nav.setRoot(LoginPage);
    }
  }

  handleNetworkChanges() {
    Network.onDisconnect().subscribe(() => {
      console.log('network was disconnected :-(');
    });
    Network.onConnect().subscribe(() => {
      console.log('network connected!');
      setTimeout(() => {
        this.queueService.processQueue();
      }, 3000);
    });
  }
}

ionicBootstrap(OrganizerApp, [
  HTTP_PROVIDERS,
  provideAuth({
    headerName: 'Authorization',
    headerPrefix: 'jwt',
    tokenName: Config.ACCESS_TOKEN_NAME,
  }),
  LocalStore
]);
