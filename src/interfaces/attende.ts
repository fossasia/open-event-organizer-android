import {IOrder} from "./order";
import {ITicket} from "./ticket";

export interface IAttendee {
  lastname: string;
  order: IOrder;
  firstname: string;
  ticket: ITicket;
  checked_in: boolean;
  id: number;
  email: string;
}
