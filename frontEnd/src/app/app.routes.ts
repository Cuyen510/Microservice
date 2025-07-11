import { Routes } from '@angular/router';
import { HomeComponent } from './component/home/home.component';
import { ProductDetailComponent } from './component/product-detail/product-detail.component';
import { LoginComponent } from './component/login/login.component';
import { RegisterComponent } from './component/register/register.component';
import { CartComponent } from './component/cart/cart.component';
import { OrderComponent } from './component/order/order.component';
import { OrderDetailComponent } from './component/order-detail/order-detail.component';
import { UserProfileComponent } from './component/user-profile/user-profile.component';
import { UserProductComponent } from './component/user-product/user-product.component';
import { ManageUserComponent } from './component/manage-user/manage-user.component';
import { AddProductComponent } from './component/add-product/add-product.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'products/:id', component: ProductDetailComponent},
    { path: 'login', component: LoginComponent},
    { path: 'register', component: RegisterComponent},
    { path: 'cart/:user_id', component: CartComponent},
    { path: 'order/:user_id', component: OrderComponent},
    { path: 'order_details/:order_id', component: OrderDetailComponent},
    { path: 'user_profile', component: UserProfileComponent},
    { path: 'user_product/:user_id', component: UserProductComponent},
    { path: 'add_product', component: AddProductComponent},
    { path: 'user', component: ManageUserComponent},
];
