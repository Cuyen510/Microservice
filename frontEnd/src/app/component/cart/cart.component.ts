import { Component } from '@angular/core';
import { FooterComponent } from '../footer/footer.component';
import { HeaderComponent } from '../header/header.component';
import { CommonModule } from '@angular/common';
import { Product } from '../../model/product';
import { Router } from '@angular/router';
import { TokenService } from '../../service/token.service';
import { ProductService } from '../../service/product.service';
import { environment } from '../../environment/environment';
import { CartService } from '../../service/cart.service';
import { Cart } from '../../model/cart';
import { CartItemDTO } from '../../dto/cart/cartItem.dto';
import { OrderDTO } from '../../dto/order/order.dto';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CartItem } from '../../model/cartItem';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { OrderService } from '../../service/order.service';
import { UserService } from '../../service/user.service';

@Component({
  selector: 'app-cart',
  imports: [CommonModule, HeaderComponent, FooterComponent, ReactiveFormsModule, MatSnackBarModule],
  standalone: true,
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent {
  cartItem: CartItem[] = [];
  groupedCartItems: { [seller_id: number]: CartItem[] } = {};
  productIds: number[] = [];
  totalAmount: number = 0; 
  cartItemMap = new Map<number, { quantity: number }>();
  orderForm: FormGroup;
  showOrderForm: boolean = false;
  

 
  constructor(
    private cartService: CartService,
    private productService: ProductService,
    private tokenService: TokenService,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    private orderService: OrderService,
    private userService: UserService
  ) {
    this.orderForm = this.formBuilder.group({
      fullname: [this.tokenService.getUser().fullname, Validators.required],    
      phone_number: [this.tokenService.getUser().phoneNumber, [Validators.required, Validators.minLength(6)]], 
      address: ["", [Validators.required, Validators.minLength(5)]], 
      note: [''],
      shipping_method: [''],
      payment_method: [''],
      shipping_address: [this.tokenService.getUser().address]
    });
  }
  
  ngOnInit(): void {     
    this.getCart();
  }

  groupCartItemsBySellerId():void {
    this.groupedCartItems = this.cartItem.reduce((acc, item) => {
      const uid = item.productSellerId;
      if (!acc[uid]) {
        acc[uid] = [];
      }
      acc[uid].push(item);
      return acc;
    }, {} as { [seller_id: number]: CartItem[] });
  }

  getCart(): void {
    this.cartService.getCart(this.tokenService.getToken(), this.tokenService.getUserId()).subscribe({
      next: (cart: Cart) => {
        debugger
        cart.cartItems?.forEach(item => {
          debugger
          this.cartItemMap.set(item.productId, { quantity: item.quantity });
          this.productIds.push(item.productId);
        });
  
        this.productService.getProductsByIds(this.productIds).subscribe({
          next: (products: Product[]) => {
            debugger
            this.cartItem = products.map(product => {
              const quantity = this.cartItemMap.get(product.id)?.quantity || 0;
              return {
                name: product.name,
                price: product.price,
                quantity: quantity,
                thumbnail: `${environment.apiBaseUrl}/products/images/${product.thumbnail}`,
                productSellerId: product.userId,
                productId: product.id
              };
            });
            this.groupCartItemsBySellerId();
            this.calculateTotal(); 
          },
          error: err => this.snackBar.open(err.error.message || 'Cart is empty', 'Close', { duration: 3000 })
        });
      },
      error: err => this.snackBar.open(err.error.message || 'Cart is empty', 'Close', { duration: 3000 })
    });
  }
  
  placeOrder(): void {
    this.showOrderForm = !this.showOrderForm;
  }

  increaseQuantity(item: any): void {
    debugger
    item.quantity++;
    const cartItem: CartItemDTO = {
      product_id: item.productId,
      quantity: item.quantity,
      price: item.price
    }
    this.cartService.updateCartItem(
      this.tokenService.getToken(),
      this.tokenService.getUserId(),
      cartItem
    ).subscribe({
      next: () => this.getCart(),
      error: err => this.snackBar.open(err.error.message || 'An error occurred', 'Close', { duration: 3000 })
    });
  }
  
  decreaseQuantity(item: any): void {
    debugger
    item.quantity--;
    const cartItem: CartItemDTO = {
      product_id: item.productId,
      quantity: item.quantity,
      price: item.price
    }
    this.cartService.updateCartItem(
      this.tokenService.getToken(),
      this.tokenService.getUserId(),
      cartItem
    ).subscribe({
        next: () => {
          if (item.quantity === 0) {
            const index = this.cartItem.indexOf(item);
            if (index > -1) {
              this.cartItem.splice(index, 1);
            }
          }
         
        },
        error: err => this.snackBar.open(err.error.message || 'An error occurred', 'Close', { duration: 3000 })
    });
    this.getCart();
  }
  

  confirmOrder(): void {
    Object.entries(this.groupedCartItems).forEach(([sellerId, items]) => {
      let total = 0;
      const cart_item: CartItemDTO[] = [];
  
      items.forEach(item => {
        cart_item.push({ product_id: item.productId, quantity: item.quantity, price: item.price });
        total += item.price * item.quantity;
      });
  
      this.userService.getUserAddress(parseInt(sellerId,10)).subscribe({
        next: (response: any) => {
          debugger
          const sellerAddress = response.address;
  
          const order: OrderDTO = {
            user_id: this.tokenService.getUserId(),
            fullname: this.orderForm.get('fullname')?.value,
            phone_number: this.orderForm.get('phone_number')?.value,
            shipping_address: this.orderForm.get('shipping_address')?.value,
            note: this.orderForm.get('note')?.value,
            payment_method: this.orderForm.get('payment_method')?.value,
            shipping_method: this.orderForm.get('shipping_method')?.value,
            cart_items: cart_item,
            total_money: total,
            address: sellerAddress
          };
  
          this.orderService.placeOrder(this.tokenService.getToken(), order).subscribe({
            next: (res) => {
              this.snackBar.open(res.message, 'Close', { duration: 3000 });
            },
            error: (err) => {
              this.snackBar.open(err.message || 'Order failed', 'Close', { duration: 3000 });
            }
          });
        },
        error: (err) => {
          this.snackBar.open(err.message || 'Failed to fetch seller address', 'Close', { duration: 3000 });
        }
      });
    });
    this.getCart();
  }
  
  

  calculateTotal(): void {
    debugger
    this.totalAmount = 0;
      for(let i = 0; i < this.cartItem.length; i++){
        this.totalAmount += this.cartItem[i].price * this.cartItem[i].quantity;
      }
  }
}
