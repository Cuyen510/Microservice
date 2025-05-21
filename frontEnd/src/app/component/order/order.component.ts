import { Component } from '@angular/core';
import { OrderService } from '../../service/order.service';
import { ActivatedRoute, Router } from '@angular/router';
import { TokenService } from '../../service/token.service';
import { ApiResponse } from '../../response/api.response';
import { OrderResponse } from '../../response/order/order.response';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-order',
  imports: [CommonModule, HeaderComponent, FooterComponent,  MatSnackBarModule, FormsModule],
  templateUrl: './order.component.html',
  styleUrl: './order.component.scss'
})
export class OrderComponent {
  orders: OrderResponse[] = [];
  currentPage: number = 0;
  itemsPerPage: number = 12;
  pages: number[] = [];
  totalPages:number = 0;
  keyword: string = "";
  visiblePages: number[] = [];

  constructor(
    private orderService: OrderService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private tokenService: TokenService
  ) {}

  ngOnInit(): void {
    debugger
    this.getAllOrders(this.tokenService.getUserId(), this.currentPage, this.itemsPerPage);
  }
  searchOrders() {
    this.currentPage = 0;
    this.itemsPerPage = 12;
    debugger
    this.getAllOrders(this.tokenService.getUserId(), this.currentPage, this.itemsPerPage);
  }

  getAllOrders( user_id: number, page: number, itemsPerPage: number) {
    debugger
    this.orderService.getAllOrdersByUserId(this.tokenService.getToken(), user_id, page, itemsPerPage).subscribe({
      next: (response: any) => {
        debugger        
        this.orders = response.orders;
        this.totalPages = response.totalPages;
        this.visiblePages = this.generateVisiblePageArray(this.currentPage, this.totalPages);
      },
      complete: () => {
        debugger;
      },
      error: (error: any) => {
        debugger;
        console.error('Error fetching products:', error);
      }
    });    
  }


  onPageChange(page: number) {
    debugger;
    this.currentPage = page < 0 ? 0 : page;        
    this.getAllOrders(this.tokenService.getUserId(), this.currentPage, this.itemsPerPage );
  }

  generateVisiblePageArray(currentPage: number, totalPages: number): number[] {
    const maxVisiblePages = 5;
    const halfVisiblePages = Math.floor(maxVisiblePages / 2);

    let startPage = Math.max(currentPage - halfVisiblePages, 1);
    let endPage = Math.min(startPage + maxVisiblePages - 1, totalPages);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(endPage - maxVisiblePages + 1, 1);
    }

    return new Array(endPage - startPage + 1).fill(0)
        .map((_, index) => startPage + index);
  }

  cancel(id:number) {
    const confirmation = window
      .confirm('Are you sure you want to delete this order?');
    if (confirmation) {
      debugger
      this.orderService.cancelOrder(this.tokenService.getToken() ,id).subscribe({
        next: (response: ApiResponse) => {
          debugger
          console.log(response);         
        },
        complete: () => {
          debugger;          
        },
        error: (error: any) => {
          debugger;
          console.error('Error fetching order:', error);
        }
      });    
    }
  }


  viewDetails(order:OrderResponse) {
    debugger
    this.router.navigate(['/order_details/', order.id]);
  }

}

