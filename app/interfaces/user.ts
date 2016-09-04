export interface UserDetail {
  avatar: string;
  lastname: string;
  contact: string;
  details: string;
  firstname: string;
  twitter: string;
  facebook: string;
}

export interface User {
  user_detail?: UserDetail;
  id: number;
  last_access_time: Date;
  signup_time: Date;
  email: string;
}
