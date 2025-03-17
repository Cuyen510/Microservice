import {
    IsString, 
    IsNotEmpty     
} from 'class-validator';

export class InsertProductDTO {
    @IsNotEmpty()
    @IsString()
    name: string;

    @IsNotEmpty()
    price: number;

    @IsString()
    description: string;

    @IsNotEmpty()
    category_id: number;

    user_id: number;

    images: File[] = [];
    
    constructor(data: any) {
        this.name = data.name;
        this.price = data.price;
        this.description = data.description;
        this.category_id = data.category_id;
        this.user_id = data.user_id;
    }
}