import{
    IsString,
    IsPhoneNumber,
    IsNotEmpty,
    IsDate
} from 'class-validator'

export class RegisterDTO {
    @IsString()
    fullname: string;

    @IsPhoneNumber()
    phone_number: string;

    @IsString()
    @IsNotEmpty()
    address: string;

    @IsString()
    password: string;

    @IsString()
    retype_password: string;

    @IsDate()
    date_of_birth: Date;

    constructor(data: any){
       this.fullname = data.fullName; 
       this.phone_number = data.phone_number;
       this.address = data.address;
       this.password = data.password;
       this.retype_password = data.retype_password;
       this.date_of_birth = data.date_of_birth;
    }
}