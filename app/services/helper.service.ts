import {Injectable} from "@angular/core";

@Injectable()
export class LocalStore {
  prefix:string = 'store_';
  constructor() {}
  set(name:string, value:any) {
    localStorage.setItem(this.prefix + name, JSON.stringify(value));
  }
  get(name:string): any {
    try {
      return JSON.parse(localStorage.getItem(this.prefix + name));
    } catch(e) {
      console.error(e);
      return null;
    }
  }
  has(name:string): boolean {
    return localStorage.getItem(this.prefix + name) !== null
  }
  remove(name:string) {
    localStorage.removeItem(this.prefix + name)
  }
}
