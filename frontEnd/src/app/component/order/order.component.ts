import { Component } from '@angular/core';
import { OrderService } from '../../service/order.service';
import { Router } from '@angular/router';
import { TokenService } from '../../service/token.service';
import { ApiResponse } from '../../response/api.response';
import { OrderResponse } from '../../response/order/order.response';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-order',
  imports: [CommonModule, HeaderComponent, FooterComponent,  MatSnackBarModule, FormsModule],
  templateUrl: './order.component.html',
  styleUrl: './order.component.scss'
})
export class OrderComponent {
  displayOrder: OrderResponse[] = [];
  orders: OrderResponse[] = [];
  cancelOrders: OrderResponse[] = [];
  currentPage: number = 0;
  itemsPerPage: number = 12;
  pages: number[] = [];
  totalPages:number = 0;
  keyword: string = "";
  visiblePages: number[] = [];
  showCanceledOrders: boolean = false;
  pagedOrder: OrderResponse[] = [];



  constructor(
    private orderService: OrderService,
    private router: Router,
    private tokenService: TokenService,
    private sb: MatSnackBar
  ) {}

  ngOnInit(): void {
    debugger
    this.getAllOrders(this.tokenService.getUserId(), this.currentPage, this.itemsPerPage);
  }

  toggleOrders(): void {
    this.showCanceledOrders = !this.showCanceledOrders;
    if(this.showCanceledOrders){
      this.displayOrder = this.cancelOrders;
    }else{
      this.displayOrder = this.orders;
    }
  }

  searchOrders() {
    this.currentPage = 0;
    this.itemsPerPage = 12;
    debugger
    this.orderService.getAllOrders(this.tokenService.getToken(),this.keyword ,this.tokenService.getUserId() , this.currentPage, this.itemsPerPage)
    .subscribe({
      next: (response: any)=>{
        this.orders = response.orders;
        this.totalPages = response.totalPages;
        // this.visiblePages = this.generateVisiblePageArray(this.currentPage, this.totalPages);
        this.visiblePages = this.getVisiblePages();
      },
      complete: () =>{},
      error: (error: any) => {
        this.sb.open(error?.error?.message, 'Close', {duration: 3000})
      } 
    });
  }

  getAllOrders( user_id: number, page: number, itemsPerPage: number) {
    debugger
    this.orderService.getAllOrdersByUserId(this.tokenService.getToken(), user_id, page, itemsPerPage).subscribe({
      next: (response: any) => {
        debugger   
        response.orders.forEach((order: OrderResponse) => {
          if(order.status === "canceled"){
            this.cancelOrders.push(order);
          }else{
            this.orders.push(order);
          }
        });  
        this.displayOrder = this.orders;   
        this.totalPages = Math.floor(this.orders.length/this.itemsPerPage);
        // this.visiblePages = this.generateVisiblePageArray(this.currentPage, this.totalPages);
        this.visiblePages = this.getVisiblePages();
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

  updatePagedOrder(): void {
    const start = (this.currentPage) * this.itemsPerPage;
    this.pagedOrder = this.displayOrder.slice(start, start + this.totalPages);
  }

  changePage(page: number): void {
    this.currentPage = page < 0 ? 0 : page; 
    this.updatePagedOrder();
  }

  // onPageChange(page: number) {
  //   debugger;
  //   this.currentPage = page < 0 ? 0 : page;        
  //   this.getAllOrders(this.tokenService.getUserId(), this.currentPage, this.itemsPerPage );
  // }

  // generateVisiblePageArray(currentPage: number, totalPages: number): number[] {
  //   const maxVisiblePages = 5;
  //   const halfVisiblePages = Math.floor(maxVisiblePages / 2);

  //   let startPage = Math.max(currentPage - halfVisiblePages, 1);
  //   let endPage = Math.min(startPage + maxVisiblePages - 1, totalPages);

  //   if (endPage - startPage + 1 < maxVisiblePages) {
  //     startPage = Math.max(endPage - maxVisiblePages + 1, 1);
  //   }

  //   return new Array(endPage - startPage + 1).fill(0)
  //       .map((_, index) => startPage + index);
  // }

  getVisiblePages(): number[] {
    const pages = [];
    const total = this.totalPages;
    const current = this.currentPage;
    const maxVisible = 5;

    let start = Math.max(1, current - Math.floor(maxVisible / 2));
    let end = Math.min(total, start + maxVisible - 1);

    if (end - start < maxVisible - 1) {
      start = Math.max(1, end - maxVisible + 1);
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
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


  viewDetails(orderId: number) {
    debugger
    this.router.navigate(['/order_details', orderId]);
  }

}

