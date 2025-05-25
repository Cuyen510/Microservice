import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { Product } from '../../model/product';
import { ProductService } from '../../service/product.service';
import { TokenService } from '../../service/token.service';
import { environment } from '../../environment/environment';

@Component({
  selector: 'app-user-product',
  imports: [CommonModule, FormsModule, HeaderComponent, FooterComponent, MatSnackBarModule],
  templateUrl: './user-product.component.html',
  styleUrl: './user-product.component.scss',
  standalone: true
})
export class UserProductComponent implements OnInit {

  products: Product[] = [];
  pagedProducts: Product[] = [];
  currentPage = 0;
  pageSize = 5;
  totalPages = 0;

  constructor(private productService: ProductService,
              private tokenService: TokenService
  ) {}

  ngOnInit(): void {
    this.fetchUserProducts();
  }

  fetchUserProducts(): void {
    debugger
    this.productService.getProductsByUserId(this.tokenService.getUserId(), this.currentPage, this.pageSize)
    .subscribe((data: any) => {
      this.products = data.products;
      this.products.forEach(product => {
        product.thumbnail = `${environment.apiBaseUrl}/products/images/${product.thumbnail}`
      });
      this.totalPages = data.totalPages;
      this.updatePagedProducts();
    });
  }

  updatePagedProducts(): void {
    const start = (this.currentPage) * this.pageSize;
    this.pagedProducts = this.products.slice(start, start + this.pageSize);
  }

  changePage(page: number): void {
    if (page < 0 || page > this.totalPages) return;
    this.currentPage = page;
    this.updatePagedProducts();
  }

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

  deleteProduct(productId: number): void {
    console.log('Delete product with id', productId);
  }
}
