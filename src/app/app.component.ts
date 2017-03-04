import {Component, ViewChild} from "@angular/core";
import {tokenNotExpired} from "angular2-jwt";
import {MenuController, Nav, Platform} from "ionic-angular";
import {Network, Splashscreen, StatusBar} from "ionic-native";
import {EventsPage} from "../pages/events/events";
import {LoginPage} from "../pages/login/login";
import {AttendeesService} from "../services/attendees.service";
import {NetworkCheck} from "../services/network-check.service";
import {QueueService} from "../services/queue.service";
import {Config} from "./config";

@Component({
  providers: [AttendeesService, NetworkCheck, QueueService],
  queries: {
    nav: new ViewChild("content"),
  },
  templateUrl: "app.html",
})

export class OrganizerAppComponent {

  @ViewChild(Nav) public nav: Nav;
  public rootPage: any;

  constructor(private platform: Platform, private queueService: QueueService,
              private networkCheckService: NetworkCheck, private menuCtrl: MenuController) {
    platform.ready().then(() => {
      StatusBar.styleDefault();
      Splashscreen.hide();
      menuCtrl.enable(true);
      this.handleRoot();
      this.handleNetworkChanges();
    });
    this.queueService.setupQueueListener();
    this.queueService.processQueue();
  }

  public logout() {
    localStorage.removeItem(Config.ACCESS_TOKEN_NAME);
    this.nav.setRoot(LoginPage);
    this.menuCtrl.close();
  }

  private handleRoot() {
    if (tokenNotExpired(Config.ACCESS_TOKEN_NAME)) {
      this.nav.setRoot(EventsPage);
    } else {
      this.nav.setRoot(LoginPage);
    }
  }

  private handleNetworkChanges() {
    Network.onDisconnect().subscribe(() => {
      this.networkCheckService.showNoNetworkAlert();
    });
    Network.onConnect().subscribe(() => {
      setTimeout(() => {
        this.queueService.processQueue();
      }, 3000);
    });
  }
}
