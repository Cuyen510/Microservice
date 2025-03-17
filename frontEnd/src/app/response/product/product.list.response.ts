import { Product } from "../../model/product";

export interface ProductListResponse {
    products: Product[];
    totalPages: number;
}