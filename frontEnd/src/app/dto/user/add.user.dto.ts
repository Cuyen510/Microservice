export class AddUserDTO {
    fullname: string;

    phone_number: string;

    address: string;

    password: string;

    retype_password: string;

    date_of_birth: Date;
    
    role: string;

    constructor(data: any){
       this.fullname = data.fullName; 
       this.phone_number = data.phone_number;
       this.address = data.address;
       this.password = data.password;
       this.retype_password = data.retype_password;
       this.date_of_birth = data.date_of_birth;
       this.role = data.role;
    }
}