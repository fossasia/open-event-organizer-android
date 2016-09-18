/**
 * Created by Niranjan on 30-Aug-16.
 */
import {Injectable} from '@angular/core';
import 'rxjs/add/operator/map';

@Injectable()
export class NetworkCheck {

  constructor(private navCtrl: NavController, private platform: Platform) {

  }
  hasNetwork() {
    this.platform.ready().then(() => {
      var networkState = navigator.connection.type;
      return networkState !== Connection.NONE;
    });
  }


}
