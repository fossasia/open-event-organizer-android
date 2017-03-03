import {ErrorHandler, NgModule} from "@angular/core";
import {Http, HttpModule} from "@angular/http";
import {Storage} from "@ionic/storage";
import {AuthConfig, AuthHttp} from "angular2-jwt";
import {MomentModule} from "angular2-moment";
import {IonicApp, IonicErrorHandler, IonicModule} from "ionic-angular";
import {EventAttendeesPage} from "../pages/event-attendees/event-attendees";
import {EventDashboardPage} from "../pages/event-dashboard/event-dashboard";
import {EventsPage} from "../pages/events/events";
import {LoginPage} from "../pages/login/login";
import {KeysPipe} from "../pipes/keys.pipe";
import {OrganizerAppComponent} from "./app.component";
import {Config} from "./config";

export function getAuthHttp(http) {
  return new AuthHttp(new AuthConfig({
    globalHeaders: [{Accept: "application/json"}],
    headerName: "Authorization",
    headerPrefix: "jwt",
    tokenGetter: (() => localStorage.getItem(Config.ACCESS_TOKEN_NAME)),
    tokenName: Config.ACCESS_TOKEN_NAME,
  }), http);
}

export function provideStorage() {
  return new Storage(["localstorage"], {name: "open_event_organizer"});
}

@NgModule({
  bootstrap: [
    IonicApp,
  ],
  declarations: [
    EventAttendeesPage,
    EventDashboardPage,
    EventsPage,
    KeysPipe,
    LoginPage,
    OrganizerAppComponent,
  ],
  entryComponents: [
    EventAttendeesPage,
    EventDashboardPage,
    EventsPage,
    LoginPage,
    OrganizerAppComponent,
  ],
  imports: [
    HttpModule,
    IonicModule.forRoot(OrganizerAppComponent),
    MomentModule,
  ],
  providers: [
    {
      provide: Storage,
      useFactory: provideStorage,
    },
    {
      provide: ErrorHandler,
      useClass: IonicErrorHandler,
    },
    {
      deps: [Http],
      provide: AuthHttp,
      useFactory: getAuthHttp,
    },
  ],
})

export class AppModule {
}
