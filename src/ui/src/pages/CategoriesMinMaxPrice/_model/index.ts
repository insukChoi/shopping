interface LowestAndHighestBrandWithPriceResponse {
    categoryName: string;
    lowestPrice?: BrandWithPriceContent;
    highestPrice?: BrandWithPriceContent;
}

interface BrandWithPriceContent {
    brandName: string;
    price: number;
}
