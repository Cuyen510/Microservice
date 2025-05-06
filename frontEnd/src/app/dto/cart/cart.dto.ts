import { CartItemDTO } from "./cartItem.dto";

export class UpdateCartDTO {    
    user_id: number;
    
    cart_items: CartItemDTO[];
      
    constructor(data: any) {
        this.user_id = data.user_id;
        this.cart_items = data.cart_items;    
    
    }
}