import {Injectable} from "@angular/core";
import {Response} from "@angular/http";
import {AuthHttp} from "angular2-jwt";
import "rxjs/add/operator/map";
import {Observable} from "rxjs/Rx";
import {Config} from "../app/config";
import {IUser} from "../interfaces/user";

@Injectable()
export class UserService {

  constructor(public authHttp: AuthHttp) {
  }

  public getMe(): Observable<IUser> {
    return this.authHttp.get(Config.API_ENDPOINT + "/users/me").map((res: Response) => res.json());
  }
}
