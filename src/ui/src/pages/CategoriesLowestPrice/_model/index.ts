interface CategoriesLowestPriceResponse {
    contents: CategoriesLowestPriceContent[];
    totalPrice: number;
}

interface CategoriesLowestPriceContent {
    categoryName: string;
    brandName: string;
    price: number;
}
