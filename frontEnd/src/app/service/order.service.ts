import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../response/api.response';
import { environment } from '../environment/environment';
import { OrderDTO } from '../dto/order/order.dto';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private apiUrl = `${environment.apiBaseUrl}/orders`;

  private apiOrderDetailUrl = `${environment.apiBaseUrl}/order_details`;

  constructor(private http: HttpClient) {}

  placeOrder(orderData: OrderDTO): Observable<any> {    
    return this.http.post(this.apiUrl, orderData);
  }
  
  getOrderById(orderId: number): Observable<any> {
    const url = `${environment.apiBaseUrl}/orders/${orderId}`;
    return this.http.get(url);
  }

  getOrdersByUserId(userId: number): Observable<any> {
    const url = `${environment.apiBaseUrl}/orders/user/${userId}`;
    return this.http.get(url);
  }

  getAllOrders(keyword:string, user_id: number,
      page: number, limit: number
  ): Observable<ApiResponse> {
      const params = new HttpParams()
      .set('user_id', user_id.toString())
      .set('keyword', keyword)      
      .set('page', page.toString())
      .set('limit', limit.toString());            
      return this.http.get<ApiResponse>(this.apiUrl+'/user', { params });
  }

  cancelOrder(orderId: number): Observable<ApiResponse> {
    const params = new HttpParams()
    .set('id', orderId)                 
    return this.http.put<ApiResponse>(this.apiUrl, { params });
  }

}
