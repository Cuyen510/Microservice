import { Component, OnInit } from '@angular/core';
import { Product } from '../../model/product';
import { Category } from '../../model/category';
import { ProductService } from '../../service/product.service';
import { Router } from '@angular/router';
import { CategoryService } from '../../service/category.service';
import { environment } from '../../environment/environment';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule, HeaderComponent, FooterComponent],
  standalone: true,
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = [];
  selectedCategoryId: number = 0;
  currentPage: number = 0;
  itemsPerPage: number = 9;
  pages: number[] = [];
  totalPages: number = 0;
  visiblePages: number[] = [];
  keyword: string = '';

  constructor(
    private http: HttpClient,
    private productService: ProductService,
    private categoryService: CategoryService,
    private router: Router
  ) {}

  ngOnInit() {
    this.getProducts(0, this.itemsPerPage);
    this.getCategories();
  }
  getCategories() {
    this.categoryService.getCategories().subscribe({
      next: (categories: Category[]) => {
        debugger;
        this.categories = categories;
      },
      complete: () => {
        debugger;
      },
      error: (error: any) => {
        console.error('Error fetching categories:', error);
      },
    });
  }

  searchProducts() {
    if (this.keyword == '' && this.selectedCategoryId == 0) {
      this.getProducts(this.currentPage, this.itemsPerPage);
    } else {
      this.products = [];
      debugger;
      this.productService
        .searchProducts(
          this.keyword,
          this.selectedCategoryId,
          this.currentPage,
          this.itemsPerPage
        )
        .subscribe((res) => {
          res.products.forEach((element) => {
            element.url = `${environment.apiBaseUrl}/products/images/${element.thumbnail}`;
            this.products.push(element);
            this.totalPages = res.totalPages;
            this.visiblePages = this.generateVisiblePageArray(
              this.currentPage,
              this.totalPages
            );
          });
        });
    }
  }

  getProducts(page: number, itemsPerPage: number) {
    this.products = [];
    debugger;
    this.productService.getProducts(page, itemsPerPage).subscribe((res) => {
      res.products.forEach((element) => {
        element.url = `${environment.apiBaseUrl}/products/images/${element.thumbnail}`;
        this.products.push(element);
        this.totalPages = res.totalPages;
        this.visiblePages = this.generateVisiblePageArray(
          this.currentPage,
          this.totalPages
        );
      });
    });
  }

  onPageChange(page: number) {
    debugger;
    this.currentPage = page;
    this.searchProducts();
  }

  generateVisiblePageArray(currentPage: number, totalPages: number): number[] {
    const maxVisiblePages = 5;
    const halfVisiblePages = Math.floor(maxVisiblePages / 2);

    let startPage = Math.max(currentPage - halfVisiblePages, 1);
    let endPage = Math.min(startPage + maxVisiblePages - 1, totalPages);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(endPage - maxVisiblePages + 1, 1);
    }

    return new Array(endPage - startPage + 1)
      .fill(0)
      .map((_, index) => startPage + index);
  }

  onProductClick(productId: number) {
    debugger;
    this.router.navigate(['/products', productId]);
  }
}
