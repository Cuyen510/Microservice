import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';
import { ApiResponse } from '../response/api.response';
import { UpdateProductDTO } from '../dto/product/update.product.dto';
import { InsertProductDTO } from '../dto/product/insert.product.dto';
import { ProductListResponse } from '../response/product/product.list.response';
import { Product } from '../model/product';

@Injectable({
  providedIn: 'root', 
})
export class ProductService {
  private apiBaseUrl = environment.apiBaseUrl;
  constructor(private http : HttpClient) { }

  getProducts(
    page: number,
    limit: number
  ): Observable<ProductListResponse> {
    const params = {
      page: page,
      limit: limit
    };
    return this.http.get<ProductListResponse>(`${this.apiBaseUrl}/products`, { params });
  }

  searchProducts(
    keyword: string,
    categoryId: number,
    page: number,
    limit: number
  ): Observable<ProductListResponse> {
    const params = {
      keyword: keyword,
      category_id: categoryId.toString(),
      page: page,
      limit: limit
    };
    return this.http.get<ProductListResponse>(`${this.apiBaseUrl}/products/search`, { params });
  }

  getDetailProduct(productId: number): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.apiBaseUrl}/products/${productId}`);
  }

  getProductImages(imageName: string): Observable<any> {
    return this.http.get<any>(`${this.apiBaseUrl}/products/images/${imageName}`);
  }

  getProductsByIds(productIds: number[]): Observable<Product[]> {
    const params = new HttpParams().set('ids', productIds.join(','));
    return this.http.get<Product[]>(`${this.apiBaseUrl}/products/by-ids?`+ params);
  }

  deleteProduct(productId: number): Observable<ApiResponse> {
    debugger
    return this.http.delete<ApiResponse>(`${this.apiBaseUrl}/products/${productId}`);
  }
  updateProduct(productId: number, updatedProduct: UpdateProductDTO): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(`${this.apiBaseUrl}/products/${productId}`, updatedProduct);
  }  
  insertProduct(insertProductDTO: InsertProductDTO): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.apiBaseUrl}/products`, insertProductDTO);
  }
  uploadImages(productId: number, files: File[]): Observable<ApiResponse> {
    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
      formData.append('files', files[i]);
    }
    return this.http.post<ApiResponse>(`${this.apiBaseUrl}/products/uploads/${productId}`, formData);
  }
  deleteProductImage(id: number): Observable<any> {
    debugger
    return this.http.delete<string>(`${this.apiBaseUrl}/product_images/${id}`);
  }
}
