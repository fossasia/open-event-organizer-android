/**
 * Created by Niranjan on 30-Aug-16.
 */
import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import 'rxjs/add/operator/map';
import {Config} from "../config";
import {Observable} from "rxjs/Rx";

@Injectable()
export class LoginService {
  constructor(private http:Http) { }

  login(credentials:Object): Observable<string> {
    let body = JSON.stringify(credentials);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(Config.API_ENDPOINT + "/login", body, options).map((res:Response) => res.json().access_token);
  }
}
