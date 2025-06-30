import { Component, OnInit } from '@angular/core';
import { UserService } from '../../service/user.service';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { TokenService } from '../../service/token.service';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { UserResponse } from '../../response/user/user.response';
import { AddUserDTO } from '../../dto/user/add.user.dto';

@Component({
  selector: 'app-manage-user',
  imports: [
    CommonModule,
    HeaderComponent,
    FooterComponent,
    MatSnackBarModule,
    ReactiveFormsModule,
  ],
  templateUrl: './manage-user.component.html',
  styleUrl: './manage-user.component.scss',
})
export class ManageUserComponent implements OnInit {
  users: UserResponse[] = [];
  currentPage = 0;
  pageSize = 5;
  totalPages = 0;
  userForm!: FormGroup;
  constructor(
    private userService: UserService,
    private tokenService: TokenService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit(): void {
    this.fetchUsers(1);
    this.initForm();
  }
  initForm() {
    this.userForm = this.formBuilder.group({
      fullname: [''],
      phone_number: ['', [Validators.required, Validators.minLength(10)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      retype_password: ['', [Validators.required, Validators.minLength(6)]],
      address: ['', [Validators.required]],
      date_of_birth: new Date(),
      role: [''],
    });
  }

  fetchUsers(page: number): void {
    this.userService
      .getAllUsers(
        this.tokenService.getToken(),
        this.currentPage,
        this.pageSize
      )
      .subscribe((data: any) => {
        this.users = data.users;
        this.totalPages = data.totalPages;
      });
  }

  changePage(page: number): void {
    if (page < 0 || page > this.totalPages) return;
    this.currentPage = page;
    this.fetchUsers(page);
  }

  getVisiblePages(): number[] {
    const pages = [];
    const total = this.totalPages;
    const current = this.currentPage;
    const maxVisible = 5;

    let start = Math.max(1, current - Math.floor(maxVisible / 2));
    let end = Math.min(total, start + maxVisible - 1);

    if (end - start < maxVisible - 1) {
      start = Math.max(1, end - maxVisible + 1);
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  }

  deleteUser(id: number): void {
    this.userService.deleteUser(this.tokenService.getToken(), id).subscribe({
      next: () => this.fetchUsers(1),
      error: (err) => console.error(err),
    });
  }

  editUser(id: number): void {}

  addUser(): void {
    const formValue = this.userForm.value;
    const addUserDTO: AddUserDTO = {
      phone_number: formValue.phone_number,
      fullname: formValue.fullname,
      address: formValue.address,
      password: formValue.password,
      retype_password: formValue.retype_password,
      date_of_birth: formValue.date_of_birth,
      role: formValue.role,
    };
    this.userService
      .addUser(this.tokenService.getToken(), addUserDTO)
      .subscribe({
        next: () => {
          this.fetchUsers(1);
        },
        error: (err) => console.error(err),
      });
  }
}
