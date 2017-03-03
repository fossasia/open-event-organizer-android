export interface IUserDetail {
  avatar: string;
  lastname: string;
  contact: string;
  details: string;
  firstname: string;
  twitter: string;
  facebook: string;
}

export interface IUser {
  user_detail?: IUserDetail;
  id: number;
  last_access_time: Date;
  signup_time: Date;
  email: string;
}
