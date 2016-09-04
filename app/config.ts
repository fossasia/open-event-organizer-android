/**
 * Created by Niranjan on 30-Aug-16.
 */
export class Config {
  public static get API_ENDPOINT(): string {
    return 'https://open-event-dev.herokuapp.com/api/v2';
  }
  public static get ACCESS_TOKEN_NAME(): string {
    return 'access_token';
  }
}
