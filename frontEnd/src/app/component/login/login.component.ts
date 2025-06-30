import { Component, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule, NgForm } from '@angular/forms';
import { LoginDTO } from '../../dto/user/login.dtos';
import { LoginResponse } from '../../response/user/login.response';
import { TokenService } from '../../service/token.service';
import { FooterComponent } from '../footer/footer.component';
import { HeaderComponent } from '../header/header.component';
import { CommonModule } from '@angular/common';
import { UserResponse } from '../../response/user/user.response';
import { UserService } from '../../service/user.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  standalone: true,
  imports: [
    FooterComponent,
    HeaderComponent,
    CommonModule,
    FormsModule,
  ]
})
export class LoginComponent {

  @ViewChild('loginForm') loginForm!: NgForm;

  phone: string = '';
  password: string = '';
  rememberMe: boolean = true;
  showPassword: boolean = false;
  userResponse?: UserResponse
  
  
  
  constructor(private router: Router, 
              private userService: UserService,
              private tokenService: TokenService){
    this.phone = '';
    this.password = '';
  }
  


  onPhoneChange(){
    console.log(`Phone typed = ${this.phone}`)
  }

  login(){         
    debugger
    const loginDTO:  LoginDTO= {
        "phone_number": this.phone,
        "password": this.password,
    }

    this.userService.login(loginDTO)
      .subscribe({
        next: (response: LoginResponse) =>{
          debugger
          if(this.rememberMe){
            debugger
            this.tokenService.saveToken(response.access_token);
            this.tokenService.saveUser(response.user)
        }
          
          this.router.navigate(['']);  
            
        },
        complete: () =>{
          debugger
        },

        error: (error: any) =>{
          debugger
          alert(`Cannot log in, error: ${error?.error?.message}`);
        }
      })

  }

  createAccount(){
    debugger
    this.router.navigate(['/register']);
  }

  togglePassword(){
    this.showPassword = !this.showPassword;
  }
}
