import { Injectable } from "@angular/core";
import { environment } from "../environment/environment";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { ApiResponse } from "../response/api.response";
import { Observable } from "rxjs";
import { UpdateCartDTO } from "../dto/cart/cart.dto";
import { CartItemDTO } from "../dto/cart/cartItem.dto";

@Injectable({
    providedIn: 'root'
  })
  export class CartService {
  
    private apiBaseUrl = environment.apiBaseUrl;
  
    constructor(private http: HttpClient) {}
  
    getCart(token:String, user_id : number): Observable<any>{
      return this.http.get(`${this.apiBaseUrl}/cart/${user_id}`,{
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        })
      })
    }

    addToCart(token: String, user_id: number, cartItemDTO: CartItemDTO): Observable<any>{
      return this.http.post(`${this.apiBaseUrl}/cart/${user_id}`,cartItemDTO, {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        })
      })
    }

    updateCartItem(token: String, user_id: number, cartItemDTO: CartItemDTO): Observable<any>{
      return this.http.put<any>(`${this.apiBaseUrl}/cart/cartItem/${user_id}`,cartItemDTO, {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        })
      })
    }
    
    deleteCart(user_id: number): Observable<ApiResponse> {
      debugger
      return this.http.delete<ApiResponse>(`${this.apiBaseUrl}/categories/${user_id}`);
    }
    
    updateCart(token: String, updatedCart: UpdateCartDTO): Observable<ApiResponse> {
      return this.http.put<ApiResponse>(`${this.apiBaseUrl}/cart`, updatedCart,{
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        })
      });
    }  
    
  }