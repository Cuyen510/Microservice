import { Role } from "../../model/role";
export interface UserResponse {
    id: number;
    fullname: string;
    phone_number: string;
    address:string;
    isActive: boolean;
    date_of_birth: Date;
    role: Role;    
}

