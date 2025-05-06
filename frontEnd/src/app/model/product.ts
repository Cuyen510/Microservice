import { ProductImage } from "./productImage";

export interface Product {
    id : number

    name : string;

    price : number;

    thumbnail : string;

    description : string;

    stock: number;

    categoryId : number ;

    userId: number;

    url : string;

    product_images: ProductImage[];
}