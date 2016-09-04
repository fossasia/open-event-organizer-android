import {Component, ViewChild} from '@angular/core';
import {Platform, ionicBootstrap, Nav} from 'ionic-angular';
import {StatusBar} from 'ionic-native';
import {HTTP_PROVIDERS} from "@angular/http";
import {provideAuth, tokenNotExpired} from 'angular2-jwt';
import {Config} from "./config";
import {LocalStore} from "./services/helper.service";
import {EventsPage} from "./pages/events/events";
import {LoginPage} from "./pages/login/login";

@Component({
  templateUrl: 'build/app.html',
  queries: {
    nav: new ViewChild('content')
  }
})

export class OrganizerApp {

  @ViewChild(Nav) nav: Nav;

  constructor(private platform: Platform) {
    platform.ready().then(() => {
      // Okay, so the platform is ready and our plugins are available.
      // Here you can do any higher level native things you might need.
      StatusBar.styleDefault();
      this.handleRoot();
    });
  }

  handleRoot() {
    if(tokenNotExpired(Config.ACCESS_TOKEN_NAME)) {
      this.nav.setRoot(EventsPage);
    } else {
      this.nav.setRoot(LoginPage);
    }
  }
}


ionicBootstrap(OrganizerApp,  [
  HTTP_PROVIDERS,
  provideAuth({
    headerName: 'Authorization',
    headerPrefix: 'jwt',
    tokenName: Config.ACCESS_TOKEN_NAME,
  }),
  LocalStore
]);
