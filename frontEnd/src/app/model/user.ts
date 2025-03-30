import { Role } from "./role";

export interface User{
    id: number;
    fullname: string;
    phone_number: string;
    address:string;
    active: boolean;
    date_of_birth: Date;
    role: Role;    
}

