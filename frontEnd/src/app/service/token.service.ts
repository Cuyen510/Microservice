import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';

const TOKEN = 'ecom-token';
const USER = 'ecom-user';
@Injectable({
  providedIn: 'root'
})
export class TokenService {
  constructor() {}

  isTokenExpired(): any {
    if (!this.getToken()){
      this.LogOut();
      return true;
    } 
    const decodedToken: any = jwtDecode(this.getToken());
    const currentTime = Math.floor(Date.now() / 1000); 
    if(decodedToken.exp < currentTime){
      this.LogOut();
      return true;
    }else{
      return false;
    }
  }

  public saveToken(token: string):void{
    window.localStorage.removeItem(TOKEN);
    window.localStorage.setItem(TOKEN, token);
  }

  public saveUser(user: any): void{
    window.localStorage.removeItem(USER);
    window.localStorage.setItem(USER, JSON.stringify(user));
  }

  getToken():string {
    return window.localStorage.getItem(TOKEN)!;
  }

  LogOut(): void{
    window.localStorage.removeItem(TOKEN);
    window.localStorage.removeItem(USER);
  }

  getUserRole(): any{
    const user = this.getUser();
    if(user == null){
      return 0;
    }
    return user.role;
  }

  getUser(): any{
    return JSON.parse(window.localStorage.getItem(USER)!);
  }

  getUserId(): number {
    const user = this.getUser();
    if(user == null){
      return 0;
    }
    return user.id;
  }
  
  removeToken(): void {
    localStorage.removeItem(TOKEN);
  }  


}
