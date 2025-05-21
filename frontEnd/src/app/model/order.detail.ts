import { Order } from "./order";
import { Product } from "./product";

export interface OrderDetail {
    id: number;
    order: Order;
    product: Product;
    price: number;
    numberOfProduct : number;
    totalMoney: number;
}