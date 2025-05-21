import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OrderResponse } from '../../response/order/order.response';
import { OrderService } from '../../service/order.service';
import { ApiResponse } from '../../response/api.response';
import { TokenService } from '../../service/token.service';
import { OrderDetail } from '../../model/order.detail';
import { environment } from '../../environment/environment';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.component.html',
  styleUrl: './order-detail.component.scss',
  imports: [CommonModule, HeaderComponent, FooterComponent,  MatSnackBarModule],
  standalone: true
})
export class OrderDetailComponent {
  orderResponse: OrderResponse = {
    id: 0, 
    user_id: 0,
    fullname: '',
    phone_number: '',
    address: '',
    note: '',
    order_date: new Date(),
    status: '',
    total_money: 0, 
    shipping_method: '',
    shipping_address: '',
    shipping_date: new Date(),
    payment_method: '',
    order_details: [] 
  };  
  constructor(
    private orderService: OrderService,
    private route: ActivatedRoute,
    private tokenService: TokenService
    ) {}

  ngOnInit(): void {
    this.getOrderDetails();
  }
  
  getOrderDetails(): void {
    debugger
    const orderId = Number(this.route.snapshot.params['id']);
    this.orderService.getOrderById(this.tokenService.getToken() ,orderId).subscribe({
      next: (apiResponse: ApiResponse) => {        
        debugger;   
        const response = apiResponse.data    
        this.orderResponse.id = response.id;
        this.orderResponse.user_id = response.user_id;
        this.orderResponse.fullname = response.fullname;
        this.orderResponse.phone_number = response.phone_number;
        this.orderResponse.address = response.address; 
        this.orderResponse.note = response.note;
        this.orderResponse.order_date = new Date(
          response.order_date[0], 
          response.order_date[1] - 1, 
          response.order_date[2]
        );        
        
        this.orderResponse.order_details = response.order_details
          .map((order_detail: OrderDetail) => {
          order_detail.product.thumbnail = `${environment.apiBaseUrl}/products/images/${order_detail.product.thumbnail}`;
          return order_detail;
        });        
        this.orderResponse.payment_method = response.payment_method;
        this.orderResponse.shipping_date = new Date(
          response.shipping_date[0], 
          response.shipping_date[1] - 1, 
          response.shipping_date[2]
        );
        
        this.orderResponse.shipping_method = response.shipping_method;
        
        this.orderResponse.status = response.status;
        this.orderResponse.total_money = response.total_money;
      },
      complete: () => {
        debugger;        
      },
      error: (error: any) => {
        debugger;
        console.error('Error fetching detail:', error);
      }
    });
  }
}
