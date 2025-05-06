import { Component, OnInit } from '@angular/core';
import { Product } from '../../model/product';
import { ProductService } from '../../service/product.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductImage } from '../../model/productImage';
import { environment } from '../../environment/environment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { CartService } from '../../service/cart.service';
import { TokenService } from '../../service/token.service';
import { UpdateCartDTO } from '../../dto/cart/cart.dto';
import { CartItem } from '../../model/cartItem';
import { CartItemDTO } from '../../dto/cart/cartItem.dto';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AddToCartReponse } from '../../response/cart/addToCart.response';


@Component({
  selector: 'app-product-detail',
  imports: [CommonModule, FormsModule, HeaderComponent, FooterComponent, MatSnackBarModule],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss'
})
export class ProductDetailComponent implements OnInit {

  product?: Product;
  productId: number = 0;
  currentImageIndex: number = 0;
  quantity: number = 1;
  images: string[] = [];
  constructor(private productService: ProductService,
              private activatedRoute: ActivatedRoute,
              private cartService: CartService,
              private tokenService: TokenService,
              private snackBar: MatSnackBar
  ){

  }

  ngOnInit(){
    debugger
    const idParam: number = this.activatedRoute.snapshot.params['id'];
    if(idParam !== null){
      this.productId += idParam;
    }
    if(!isNaN(this.productId)){
      this.productService.getDetailProduct(this.productId).subscribe({
        next: (response: any) =>{
          if(response.product_images && response.product_images.length > 0){
            response.product_images.forEach((product_images :ProductImage) =>{
              this.images.push(product_images.imageUrl = `${environment.apiBaseUrl}/products/images/${product_images.imageUrl}`);
            });
          }

          debugger
          this.product = response;
          this.showImage(this.currentImageIndex);
        },
        complete: ()=>{
          debugger;
        },

        error: (error : any) =>{
          debugger;
          console.error(error);
        }
      });
    }else{
      console.error('Invalid Param: '+ idParam )
    }
  }

  selectedImage(index: number): void{
    this.currentImageIndex = index;
  }

  showImage(index: number): void{
    debugger
    if(this.product && this.product.product_images && this.product.product_images.length>0){
      if(index < 0){
        index = 0;
      }else if(index >= this.product.product_images.length){
        index = this.product.product_images.length - 1;
      }

      this.currentImageIndex = index;
    }
  }

  thumbNailClick(index: number){
    this.currentImageIndex = index;
  }

  nextImage(): void{
    debugger
    this.showImage(this.currentImageIndex + 1);
  }

  previousImage(): void{
    this.showImage(this.currentImageIndex - 1);
  }

  increaseQuantity(): void {
    debugger
    this.quantity++;
  }
  
  decreaseQuantity(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }
  getTotalPrice(): number {
    if (this.product) {
      return this.product.price * this.quantity;
    }
    return 0;
  }

  addToCart(): void {
    const cartItem: CartItemDTO = {
      product_id: this.productId,
      quantity: this.quantity
    }
  
    this.cartService.addToCart(this.tokenService.getToken(),this.tokenService.getUserId(),cartItem).subscribe({
      next: (response: AddToCartReponse) => {
        this.snackBar.open(response.message, 'Close', { duration: 3000 });
      }
    });
  }
  
}
