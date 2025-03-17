import { ProductImage } from "./productImage";

export interface Product {
    id : number

    name : string;

    price : number;

    thumbnail : string;

    description : string;

    stock: number;

    category_id : number ;

    user_id: number;

    url : string;

    product_images: ProductImage[];
}