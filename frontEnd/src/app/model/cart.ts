import { CartItem } from "./cartItem";

export interface Cart {
    id : number

    user_id: number;

    cartItems: CartItem[];
}