export class Config {
  public static get API_ENDPOINT(): string {
    return Config.SERVER + "/api/v1";
  }

  public static get SERVER(): string {
    return localStorage.getItem("server");
  }

  public static set SERVER(endpoint) {
    localStorage.setItem("server", endpoint);
  }

  public static get ACCESS_TOKEN_NAME(): string {
    return "access_token";
  }
}
