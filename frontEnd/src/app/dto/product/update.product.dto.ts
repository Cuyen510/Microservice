import {
    IsString, 
    IsNotEmpty     
} from 'class-validator';

export class UpdateProductDTO {
    @IsNotEmpty()
    @IsString()
    name: string;

    @IsNotEmpty()
    price: number;

    @IsString()
    description: string;

    @IsNotEmpty()
    category_id: number;
    
    constructor(data: any) {
        this.name = data.name;
        this.price = data.price;
        this.description = data.description;
        this.category_id = data.category_id;
    }
}