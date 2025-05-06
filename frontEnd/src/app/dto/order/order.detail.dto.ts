export class OrderDetailDTO {
    order_id: number;

    product_id: number;

    price: number;

    number_of_products: number;

    total_money: number;

    constructor(data: any) {
        this.order_id = data.order_id;

        this.product_id = data.product_id;

        this.price = data.price;

        this.number_of_products = data.number_of_products;

        this.total_money = data.total_money;

    }

}
