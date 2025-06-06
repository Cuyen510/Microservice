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
    return this.http.get(`${this.apiUrl}/search/${orderId}`,{
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      })
    });
  }

  getAllOrdersByUserId(token: string, user_id: number,
    page: number, limit: number
  ): Observable<any> {
    const params = new HttpParams()
    .set('userId', user_id.toString()) 
    .set('page', page.toString())
    .set('limit', limit.toString());    

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });    
    return this.http.get<any>(`${this.apiUrl}`, { headers, params })
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
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });                 
    return this.http.delete<any>(`${this.apiUrl}/${orderId}`, { headers });
  }

}
