import { Routes } from '@angular/router';
import { HomeComponent } from './component/home/home.component';
import { ProductDetailComponent } from './component/product-detail/product-detail.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'products/:id', component: ProductDetailComponent}
    
];
