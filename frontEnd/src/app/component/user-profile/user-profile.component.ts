import { Component, OnInit } from '@angular/core';
import { User } from '../../model/user';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { UserService } from '../../service/user.service';
import { TokenService } from '../../service/token.service';
import { UpdateUserDTO } from '../../dto/user/update.user.dto';


@Component({
  selector: 'app-user-profile',
  imports: [CommonModule, FormsModule, HeaderComponent, FooterComponent, ReactiveFormsModule],
  providers: [DatePipe],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss'
})
export class UserProfileComponent implements OnInit {
  user: User = {} as User;
  profileForm!: FormGroup;
  showPassword: boolean = false;
  constructor(private userService: UserService,
              private tokenService: TokenService,
              private fb: FormBuilder,
              private datePipe: DatePipe
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadUserProfile();
  }

  initForm() {
    this.profileForm = this.fb.group({
      fullname: ['', [Validators.required, Validators.maxLength(100)]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      address: [''],
      dateOfBirth: [''],
      active: [false],
      password: [''],
      retypePassword: ['']
    });
  }

  loadUserProfile() {
    debugger
    this.profileForm.patchValue({
          fullname: this.tokenService.getUser().fullname,
          phoneNumber: this.tokenService.getUser().phoneNumber,
          address: this.tokenService.getUser().address,
          dateOfBirth: this.datePipe.transform(this.tokenService.getUser().dateOfBirth, 'yyyy-MM-dd'),
        });
  }

  onSubmit() {
    const formValue = this.profileForm.value;

    const updatedUserDTO: UpdateUserDTO = {
      phone_number: formValue.phoneNumber,
      fullname: formValue.fullname,
      address: formValue.address,
      password: formValue.password,
      retype_password: formValue.retypePassword,
      date_of_birth: formValue.dateOfBirth 
    };
    debugger
    this.userService.updateUserDetail(this.tokenService.getToken(), updatedUserDTO).subscribe({
      next: () => alert('Profile updated successfully!'),
      error: () => alert('Failed to update profile')
    });
  }

  togglePassword(){
    this.showPassword = !this.showPassword;
  }
    
}

