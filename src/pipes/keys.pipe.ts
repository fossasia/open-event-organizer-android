import {Pipe, PipeTransform} from "@angular/core";

@Pipe({name: "keys"})
export class KeysPipe implements PipeTransform {
  public transform(value, args: string[]): any {
    let keys = [];
    for (let key in value) {
      if (value.hasOwnProperty(key)) {
        keys.push({key, value: value[key]});
      }
    }
    return keys;
  }
}
