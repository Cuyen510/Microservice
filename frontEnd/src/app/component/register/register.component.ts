import { Component, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../service/user.service';
import { RegisterDTO } from '../../dto/user/register.dto';
import { FooterComponent } from '../footer/footer.component';
import { HeaderComponent } from '../header/header.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  standalone: true,
  imports: [
    FooterComponent,
    HeaderComponent,
    CommonModule,
    FormsModule,
  ]
})
export class RegisterComponent {
  
  @ViewChild('registerForm') registerForm!: NgForm;

  phoneNumber: string;
  password: string;
  retypePassword: string;
  fullName: string;
  address: string;
  isAccepted: boolean;
  dateOfBirth: Date;
  showPassword: boolean = false;
  
  
  constructor( private router: Router, private userService: UserService){
    this.phoneNumber = '';
    this.password = '';
    this.retypePassword = '';
    this.fullName = '';
    this.address = '';
    this.isAccepted = false;
    this.dateOfBirth = new Date();
    this.dateOfBirth.setFullYear(this.dateOfBirth.getFullYear() - 18);
  }


  onPhoneNumberChange(){
    console.log(`Phone typed = ${this.phoneNumber}`)
  }

  register(){
    const message = `phone:${this.phoneNumber}`+
                    `password:${this.password}`+
                    `retypePassword:${this.retypePassword}`+
                    `fullName:${this.fullName}`+
                    `address:${this.address}`+
                    `isAccepted:${this.isAccepted}`+
                    `dateOfBirth:${this.dateOfBirth}`
    //alert(message);
    
    const registerDTO:  RegisterDTO= {
        "fullname": this.fullName,
        "phone_number": this.phoneNumber,
        "address": this.address,
        "password": this.password,
        "retype_password": this.retypePassword,
        "date_of_birth": this.dateOfBirth,
    }

    this.userService.register(registerDTO)
      .subscribe({
        next: (response: any) =>{
          debugger
          this.router.navigate(['/login']);  
        },
        complete: () =>{
          debugger
        },

        error: (error: any) =>{
          alert(`Cannot register, error: ${error.error}`);
        }
      })

  }

  checkPasswordsMatch(){
    if(this.password !== this.retypePassword){
      this.registerForm.form.controls['retypePassword'].setErrors({'passwordMismatch': true});
    }else{
      this.registerForm.form.controls['retypePassword'].setErrors(null);
    }

  }

  togglePassword(){
    this.showPassword = !this.showPassword;
  }

  checkAge(){
    if(this.dateOfBirth){
      const today = new Date();
      const birthDay = new Date(this.dateOfBirth);
      let age = today.getFullYear() - birthDay.getFullYear();
      const monthDiff = today.getMonth() - birthDay.getMonth();
      if(monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDay.getDate())){
        age--;
      }
      if(age < 18){
        this.registerForm.controls['dateOfBirth'].setErrors({'InvalidAge': true});
      }else{
        this.registerForm.controls['dateOfBirth'].setErrors(null);
      }

    }
  }

  Login(){
    debugger
    this.router.navigate(['/login']);
  }
}
