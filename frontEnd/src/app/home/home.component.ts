import { Component, OnInit } from '@angular/core';
import { Product } from '../model/product';
import { Category } from '../model/category';
import { ProductService } from '../service/product.service';
import { Router } from '@angular/router';
import { CategoryService } from '../service/category.service';
import { environment } from '../environment/environment';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule],
  standalone: true,
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
    products: Product[] = [];
    categories: Category[] = []; 
    selectedCategoryId: number  = 0; 
    currentPage: number = 0;
    itemsPerPage: number = 12;
    keyword:string = "";
  
    constructor(
      private http: HttpClient,
      private productService: ProductService,
      private categoryService: CategoryService,    
      private router: Router,   
      ) {}
  
    ngOnInit() {
      this.getProducts(this.keyword, this.selectedCategoryId, 0, this.itemsPerPage);
      this.getCategories();
    }
    getCategories() {
      this.categoryService.getCategories().subscribe({
        next: (categories: Category[]) => {
          debugger
          this.categories = categories;
        },
        complete: () => {
          debugger;
        },
        error: (error: any) => {
          console.error('Error fetching categories:', error);
        }
      });
    }
    searchProducts() {
      debugger
      this.getProducts(this.keyword, this.selectedCategoryId, this.currentPage, this.itemsPerPage );
    }

    getProducts(keyword: string, selectedCategoryId: number, page: number, itemsPerPage: number) {
      this.products = [];
      debugger
      this.productService.getProducts(keyword, selectedCategoryId, page, itemsPerPage).subscribe(res =>{
        res.products.forEach(element =>{
          element.url = `${environment.apiBaseUrl}/products/images/${element.thumbnail}`;
          this.products.push(element);
        });
      });    
    }
    
   
    onProductClick(productId: number) {
      debugger
      this.router.navigate(['/products', productId]);
    }  
  }
