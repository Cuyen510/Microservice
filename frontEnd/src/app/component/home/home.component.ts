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
            this.visiblePages = this.getVisiblePages();
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
        this.visiblePages = this.getVisiblePages();
      });
    });
  }

  changePage(page: number): void {
    this.currentPage = page < 0 ? 0 : page;
    this.searchProducts();
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

  onProductClick(productId: number) {
    debugger;
    this.router.navigate(['/products', productId]);
  }
}
