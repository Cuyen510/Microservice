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

  placeOrder(token: string,orderData: OrderDTO): Observable<any> {    
    return this.http.post(this.apiUrl, orderData,{
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      })
    });
  }
  
  getOrderById(token: string,orderId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/orders/${orderId}`,{
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      })
    });
  }

  getOrdersByUserId(token: string,userId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/orders/user/${userId}`,{
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      })
    });
  }

  getAllOrders(token: string,keyword:string, user_id: number,
      page: number, limit: number
  ): Observable<any> {
      const params = new HttpParams()
      .set('user_id', user_id.toString())
      .set('keyword', keyword)      
      .set('page', page.toString())
      .set('limit', limit.toString());    

      const headers = new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      });    
      return this.http.get<any>(this.apiUrl+'/user', { headers, params })
  }

  cancelOrder(token: string, orderId: number): Observable<any> {
    const params = new HttpParams()
    .set('id', orderId)
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });                 
    return this.http.put<any>(this.apiUrl, { headers, params });
  }

}
