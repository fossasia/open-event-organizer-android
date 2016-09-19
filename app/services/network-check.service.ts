/**
 * Created by Niranjan on 30-Aug-16.
 */
import {Injectable} from '@angular/core';
import 'rxjs/add/operator/map';
import { AlertController } from 'ionic-angular';

@Injectable()
export class NetworkCheck {

  constructor(public alertCtrl: AlertController) {

  }

  showNoNetworkAlert() {
    let alert = this.alertCtrl.create({
      title: 'No Internet Connection',
      message: 'Please check your internet connection.',
      buttons: ['Ok']
    });
    alert.present();
  }

}
