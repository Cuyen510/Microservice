import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RegisterDTO } from '../dto/user/register.dto';
import { LoginDTO } from '../dto/user/login.dtos';
import { environment } from '../environment/environment';
import { UpdateUserDTO } from '../dto/user/update.user.dto';
import { User } from '../model/user';
import { AddUserDTO } from '../dto/user/add.user.dto';


@Injectable({
  providedIn: 'root'
})
export class UserService {
  

  private apiRegister= `${environment.apiBaseUrl}/users/register`;
  private apiLogin= `${environment.apiBaseUrl}/users/login`;
  private apiUserDetail = `${environment.apiBaseUrl}/users`;
  private apiConfig = {
    headers: this.createHeaders()
  }
  constructor(private http: HttpClient) 
  {}

  private createHeaders() :HttpHeaders{
    return new HttpHeaders({'Content-Type': 'application/json'});
  }
  register(registerDTO: RegisterDTO): Observable<any>{
    return this.http.post(this.apiRegister, registerDTO,this.apiConfig);
  }

  login(loginDTO: LoginDTO): Observable<any> {
    return this.http.post(this.apiLogin, loginDTO,this.apiConfig);
  }

  getUserDetail(token: string, userId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUserDetail}/${userId}`, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      })
    })
  }

  getUserAddress(userId: number): Observable<any>{
    const params = {
      userId: userId};
    return this.http.get<any>(`${this.apiUserDetail}/userAddress`, {params})
  }


  updateUserDetail(token: string, updateUserDTO: UpdateUserDTO): Observable<any>  {
    debugger
    let userResponse = JSON.parse(window.localStorage.getItem('ecom-user')!);        
    return this.http.put<any>(`${this.apiUserDetail}/${userResponse?.id}`,updateUserDTO,{
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      })
    })
  }

  removeUserFromLocalStorage():void {
    try {
      window.localStorage.removeItem('ecom-user');
      console.log('User data removed from local storage.');
    } catch (error) {
      console.error('Error removing user data from local storage:', error);
    }
  }

  getAllUsers(token: string, currentPage: number, pageSize: number): Observable<any>{
    const params = {
      page: currentPage,
      limit: pageSize
    };
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
    return this.http.get<any>(`${this.apiUserDetail}`, {headers, params})
  }

  deleteUser(token: string, userId: number): Observable<any>{
    const params = {userId: userId};
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });   
    return this.http.delete<any>(`${this.apiUserDetail}`, {headers, params})
  }

  addUser(token: string, user: AddUserDTO): Observable<any>{
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });   
    return this.http.post<any>(`${this.apiUserDetail}/add`,user, {headers})
  }
  
}
