/**
 * Created by Niranjan on 30-Aug-16.
 */
import {Injectable} from '@angular/core';
import {Response} from '@angular/http';
import 'rxjs/add/operator/map';
import {Observable} from "rxjs/Rx";
import {User} from "../interfaces/user";
import {AuthHttp} from "angular2-jwt";
import {Config} from "../config";

@Injectable()
export class UserService {

  constructor(public authHttp: AuthHttp) { }

  getMe(): Observable<User> {
    return this.authHttp.get(Config.API_ENDPOINT + '/users/me').map((res:Response) => res.json());
  }
}
