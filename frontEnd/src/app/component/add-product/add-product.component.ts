import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { InsertProductDTO } from '../../dto/product/insert.product.dto';
import { TokenService } from '../../service/token.service';
import { ProductService } from '../../service/product.service';
import { Category } from '../../model/category';
import { CategoryService } from '../../service/category.service';

@Component({
  selector: 'app-add-product',
  imports: [
    CommonModule,
    HeaderComponent,
    FooterComponent,
    MatSnackBarModule,
    ReactiveFormsModule,
  ],
  templateUrl: './add-product.component.html',
  styleUrl: './add-product.component.scss',
})
export class AddProductComponent {
  productForm: FormGroup;
  previewImages: string[] = [];
  selectedFiles: File[] = [];
  categories: Category[] = [];

  constructor(
    private fb: FormBuilder,
    private tokenService: TokenService,
    private productService: ProductService,
    private snackBar: MatSnackBar,
    private categoryService: CategoryService
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      price: [null, [Validators.required, Validators.min(0)]],
      stock: [null, [Validators.required, Validators.min(0)]],
      images: [null],
      category: [''],
    });
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

  onImageSelected(event: any) {
    if (event.target.files) {
      for (let file of event.target.files) {
        this.selectedFiles.push(file);

        const reader = new FileReader();
        reader.onload = (e: any) => this.previewImages.push(e.target.result);
        reader.readAsDataURL(file);
      }
    }
  }

  submitProduct() {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }
    const productDTO: InsertProductDTO = {
      name: this.productForm.get('name')?.value,
      description: this.productForm.get('description')?.value,
      price: this.productForm.get('price')?.value,
      stock: this.productForm.get('stock')?.value,
      category_id: this.productForm.get('category')?.value,
      user_id: this.tokenService.getUserId(),
      thumbnail: '',
    };

    this.productService.insertProduct(productDTO).subscribe({
      next: (res) => {
        this.productService.uploadImages(res.id, this.selectedFiles).subscribe({
          next: (res) => {
            this.selectedFiles = [];
            this.snackBar.open('Added product', 'Close', { duration: 3000 });
          },
        });
      },
      error: (err) => {
        this.snackBar.open(err.message || 'Failed', 'Close', {
          duration: 3000,
        });
      },
    });
  }
}
