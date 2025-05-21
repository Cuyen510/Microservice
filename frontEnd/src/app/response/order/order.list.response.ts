import { Order } from "../../model/order";

export interface OrderListResponse{
    orders : Order[];
    totalPages: number;
}