interface BrandsLowestPriceResponse {
    lowestPrice: BrandsLowestPriceContent;
}

interface BrandsLowestPriceContent {
    brandName: string;
    categories: BrandsLowestPriceCategory[];
    totalPrice: number;
}

interface BrandsLowestPriceCategory {
    categoryName: string;
    price: number;
}
