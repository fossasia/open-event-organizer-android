import {Injectable} from "@angular/core";
import {AlertController} from "ionic-angular";
import "rxjs/add/operator/map";

@Injectable()
export class NetworkCheck {

  constructor(public alertCtrl: AlertController) {

  }

  public showNoNetworkAlert() {
    const alert = this.alertCtrl.create({
      buttons: ["Ok"],
      message: "Please check your internet connection.",
      title: "No Internet Connection",
    });
    alert.present();
  }

}
