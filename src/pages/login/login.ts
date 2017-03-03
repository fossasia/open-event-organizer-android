import {Component} from "@angular/core";
import {Storage} from "@ionic/storage";
import {provideAuth} from "angular2-jwt";
import {AlertController, NavController} from "ionic-angular";
import {Config} from "../../app/config";
import {LoginService} from "../../services/login.service";
import {UserService} from "../../services/user.service";
import {EventsPage} from "../events/events";

@Component({
  providers: [LoginService, UserService],
  selector: "login-page",
  templateUrl: "login.html",
})

export class LoginPage {

  public credentials: {email: string, password: string};
  public isLoading: boolean;

  constructor(private navCtrl: NavController,
              private loginService: LoginService,
              public alertCtrl: AlertController,
              private userService: UserService,
              private storage: Storage) {

    this.credentials = {
      email: null,
      password: null,
    };

    this.isLoading = false;
  }

  public login() {
    this.isLoading = true;
    this.loginService.login(this.credentials).subscribe(
      (res) => {
        this.isLoading = false;
        localStorage.setItem(Config.ACCESS_TOKEN_NAME, res);
        if (provideAuth(Config.ACCESS_TOKEN_NAME)) {
          this.navCtrl.setRoot(EventsPage);
        }
        this.userService.getMe().subscribe(
          (data) => this.storage.set("user", data),
          () => {
            // Should show error message.
          },
        );
      },
      (err) => {
        this.isLoading = false;
        let alertConfig = {
          buttons: ["Ok"],
          subTitle: "Something happened. Please try again later.",
          title: "An unexpected error occurred",
        };
        if (err.status === 401) {
          alertConfig.title = "Login failed";
          alertConfig.subTitle = "Please check your email and password";
        }
        let alert = this.alertCtrl.create(alertConfig);
        alert.present();
      },
    );
  }
}
