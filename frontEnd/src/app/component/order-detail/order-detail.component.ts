import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OrderResponse } from '../../response/order/order.response';
import { OrderService } from '../../service/order.service';
import { TokenService } from '../../service/token.service';
import { environment } from '../../environment/environment';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { ProductService } from '../../service/product.service';
import { Product } from '../../model/product';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.component.html',
  styleUrl: './order-detail.component.scss',
  imports: [CommonModule, HeaderComponent, FooterComponent,  MatSnackBarModule],
  standalone: true
})
export class OrderDetailComponent {
  productIds: number[] = [];
  orderItemMap = new Map<number, { quantity: number }>();
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
    tracking_number: '',
    order_details: [] 
  };  
  constructor(
    private orderService: OrderService,
    private route: ActivatedRoute,
    private tokenService: TokenService,
    private productService: ProductService
    ) {}

  ngOnInit(): void {
    this.getOrderDetails();
  }

  subTotal(item : any): number{
    return item.quantity*item.price;
  }
  
  getOrderDetails(): void {
    debugger
    const orderId = Number(this.route.snapshot.params['order_id']);
    this.orderService.getOrderById(this.tokenService.getToken() ,orderId).subscribe({
      next: (response: OrderResponse) => {        
        debugger;     
        this.orderResponse.id = response.id;
        this.orderResponse.user_id = response.user_id;
        this.orderResponse.fullname = response.fullname;
        this.orderResponse.phone_number = response.phone_number;
        this.orderResponse.address = response.address; 
        this.orderResponse.note = response.note;
        this.orderResponse.order_date = response.order_date;   
        this.orderResponse.shipping_address = response.shipping_address;
        this.orderResponse.status = response.status;
        this.orderResponse.tracking_number = response.tracking_number;
        
        response.order_details.forEach(item => {
            this.orderItemMap.set(item.productId, { quantity: item.quantity });
            this.productIds.push(item.productId);
        });
        this.productService.getProductsByIds(this.productIds).subscribe({
                  next: (products: Product[]) => {
                    debugger
                    this.orderResponse.order_details = products.map(product => {
                      const quantity = this.orderItemMap.get(product.id)?.quantity || 0;
                      return {
                        name: product.name,
                        price: product.price,
                        quantity: quantity,
                        thumbnail: `${environment.apiBaseUrl}/products/images/${product.thumbnail}`,
                        productSellerId: product.userId,
                        productId: product.id,
                      };
                    });
              }
        })
        this.orderResponse.payment_method = response.payment_method;
        this.orderResponse.shipping_date = response.order_date;
        
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
