export class CartItemDTO {    
    product_id: number;
    
    quantity: number;

    price: number;
    
      
    constructor(data: any) {
        this.product_id = data.product_id;
        this.quantity = data.quantity;    
        this.price = data.price;
    }
}