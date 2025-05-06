import { AfterViewInit, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import 'bootstrap/dist/css/bootstrap.min.css';
import { TokenService } from '../../service/token.service';




@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  standalone: true,
  imports: [    
    CommonModule,
    RouterModule
  ]
})
export class HeaderComponent  {
  isLoggedIn: boolean = false; 
  username: string = '';
  user_id: number = 0;
  constructor(private tokenService: TokenService) { 
    this.checkLoginStatus();
  }

   

  checkLoginStatus() {
    this.isLoggedIn = !this.tokenService.isTokenExpired(); 
    if (this.isLoggedIn) {
      this.username = this.tokenService.getUser().fullname; 
      this.user_id = this.tokenService.getUserId();
    }
  }

  logout() {
    this.tokenService.LogOut();
    this.isLoggedIn = false;
  }

}
