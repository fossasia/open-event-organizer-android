import {NavController, AlertController} from 'ionic-angular'
import {LoginService} from '../../services/login.service'
import {Component} from "@angular/core";
import { provideAuth } from 'angular2-jwt';
import {Config} from "../../config";
import {EventsPage} from "../events/events";
import {UserService} from "../../services/user.service";
import {LocalStore} from "../../services/helper.service";

@Component({
  templateUrl: 'build/pages/login/login.html',
  providers: [LoginService, UserService]
})

export class LoginPage {

  credentials:{email:string, password:string};
  isLoading: boolean;

  constructor(private navCtrl:NavController,
              private loginService:LoginService,
              public alertCtrl:AlertController,
              private userService:UserService,
              private localStore:LocalStore) {
    this.credentials = {
      email: null,
      password: null
    };
    this.isLoading = false;
  }

  login() {
    this.isLoading = true;
    this.loginService.login(this.credentials).subscribe(
      res => {
        this.isLoading = false;
        localStorage.setItem(Config.ACCESS_TOKEN_NAME, res);
        if(provideAuth(Config.ACCESS_TOKEN_NAME)) {
          this.navCtrl.setRoot(EventsPage)
        }
        this.userService.getMe().subscribe(
          data => this.localStore.set('user', data),
          err => console.log(err),
          () => console.log('Request Complete')
        );
      },
      err => {
        this.isLoading = false;
        let alertConfig = {
          title: 'An unexpected error occurred',
          subTitle: 'Something happened. Please try again later.',
          buttons: ['Ok']
        };
        if (err.status === 401) {
          alertConfig.title = 'Login failed';
          alertConfig.subTitle = 'Please check your email and password';
        }
        let alert = this.alertCtrl.create(alertConfig);
        alert.present()
      }
    )
  }
}
