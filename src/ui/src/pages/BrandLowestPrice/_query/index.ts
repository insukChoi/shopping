import {useQuery} from "@tanstack/react-query";
import {shoppingApiClient} from "@/apis/cilent";

export const useBrandsLowestPriceSearchQuery = () => {
    return useQuery<BrandsLowestPriceResponse>(
        {
            queryKey: ['brandsLowestPriceSearchQueryKey'],
            enabled: true,
            queryFn: async() => {
                const { data } = await shoppingApiClient.get<BrandsLowestPriceResponse>('/shopping/v1/brand/lowest-price')
                return data;
            },
        }
    )
}
