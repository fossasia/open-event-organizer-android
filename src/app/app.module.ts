import {ErrorHandler, NgModule} from "@angular/core";
import {Http, HttpModule} from "@angular/http";
import {BrowserModule} from "@angular/platform-browser";
import {IonicStorageModule} from "@ionic/storage";
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
    BrowserModule,
    IonicModule.forRoot(OrganizerAppComponent),
    IonicStorageModule.forRoot({name: "open-event-organizer"}),
    MomentModule,
  ],
  providers: [
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
