import {useQuery} from "@tanstack/react-query";
import {shoppingApiClient} from "@/apis/cilent";

export const useCategoriesMinMaxPriceSearchQuery = (categoryName: string, options?: { enabled?: boolean }) => {
    return useQuery<LowestAndHighestBrandWithPriceResponse>({
        queryKey: ['categoriesMinMaxPriceSearchQueryKey', categoryName],
        enabled: options?.enabled ?? !!categoryName,
        queryFn: async () => {
            const { data } = await shoppingApiClient.get<LowestAndHighestBrandWithPriceResponse>(
                `/shopping/v1/categories/prices/min-max`,
                { params: { categoryName } }
            );
            return data;
        },
    });
};
