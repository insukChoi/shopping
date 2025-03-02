import applyCaseMiddleware from 'axios-case-converter';
import axios from 'axios';
import queryString from 'query-string'

export const shoppingApiClient = applyCaseMiddleware(
    axios.create({
        paramsSerializer(params: Record<string, any>) {
            return queryString.stringify(params, {
                skipNull: true,
                skipEmptyString: true,
            });
        },
    }),
);



