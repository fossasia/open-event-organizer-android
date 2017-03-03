export interface IOrder {
  status: string;
  completed_at: Date;
  paid_via: string;
  id: number;
  payment_mode: string;
  invoice_number: string;
  amount: number;
  identifier: string;
}
