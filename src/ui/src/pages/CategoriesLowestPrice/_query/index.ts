import {useQuery} from "@tanstack/react-query";
import {shoppingApiClient} from "@/apis/cilent";

export const useCategoriesLowestPriceSearchQuery = () => {
    return useQuery<CategoriesLowestPriceResponse>(
        {
            queryKey: ['categoriesLowestPriceSearchQueryKey'],
            enabled: true,
            queryFn: async() => {
                const { data } = await shoppingApiClient.get<CategoriesLowestPriceResponse>('/shopping/v1/categories/lowest-price')
                return data;
            },
        }
    )
}
