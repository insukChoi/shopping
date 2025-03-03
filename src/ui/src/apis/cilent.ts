import axios from "axios";
import queryString from "query-string";
import { camelCase, snakeCase, isObject, isArray, mapKeys, mapValues } from "lodash";

const toSnakeCase = (obj) => {
    if (isArray(obj)) {
        return obj.map(toSnakeCase);
    } else if (isObject(obj)) {
        return mapValues(mapKeys(obj, (_, key) => snakeCase(key)), toSnakeCase);
    }
    return obj;
};

const toCamelCase = (obj) => {
    if (isArray(obj)) {
        return obj.map(toCamelCase);
    } else if (isObject(obj)) {
        return mapValues(mapKeys(obj, (_, key) => camelCase(key)), toCamelCase);
    }
    return obj;
};

const shoppingApiClient = axios.create({
    paramsSerializer(params) {
        return queryString.stringify(toSnakeCase(params), {
            skipNull: true,
            skipEmptyString: true,
            encode: false,
        });
    },
});

shoppingApiClient.interceptors.request.use((config) => {
    if (config.method === "get" && config.params) {
        config.params = toSnakeCase(config.params);
    }
    return config;
});

shoppingApiClient.interceptors.response.use((response) => {
    if (response.data) {
        response.data = toCamelCase(response.data);
    }
    return response;
});

export { shoppingApiClient };
