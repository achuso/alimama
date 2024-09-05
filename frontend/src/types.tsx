export interface JwtPayload {
    sub: string;     
    fullName: string;
    role: string;
    userId: number;  
    exp: number;
  }

export interface Item {
    _id: string;
    vendorId: number;
    productName: string;
    numInStock: number;
    price: number;
    pictures: string[];
    tags: string[];
    ratingAvgTotal: number;
  }