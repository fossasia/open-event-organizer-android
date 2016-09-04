import {Ticket} from './ticket'
import {Order} from './order'

export interface Attendee {
  lastname: string;
  order: Order;
  firstname: string;
  ticket: Ticket;
  checked_in: boolean;
  id: number;
  email: string;
}
