package com.insuk.shopping.exception

sealed class ApiException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause)

class UseCaseException(
    override val message: String,
    override val cause: Throwable? = null,
) : ApiException(
    message = message,
    cause = cause,
)

enum class UseCaseErrorMessage(val errorMessage: String) {
    LOAD_CATEGORIES_LOWEST_PRICE_EXCEPTION("상품별 브랜드 최저가 검색 중 오류 발생 하였습니다."),
    LOAD_BRAND_LOWEST_PRICE_EXCEPTION("최저가격 브랜드의 카테고리 상품 검색 중 오류 발생 하였습니다."),
    LOAD_LOWEST_HIGHEST_PRICE_BRANDS_EXCEPTION("최저, 최고 가격 브랜드와 상품 가격 검색 중 오류 발생 하였습니다."),
    CAN_NOT_FIND_BRAND_EXCEPTION("브랜드를 찾을 수 없습니다"),
    CAN_NOT_FIND_CATEGORY_EXCEPTION("카테고리를 찾을 수 없습니다"),
    ADD_PRODUCT_EXCEPTION("상품을 등록하는 도중 오류가 발생 하였습니다."),
    UPDATE_PRODUCT_EXCEPTION("상품을 업데이트하는 도중 오류가 발생 하였습니다."),
    DELETE_PRODUCT_EXCEPTION("상품을 삭제하는 도중 오류가 발생 하였습니다."),
}

enum class ValidationErrorMessage(val errorMessage: String) {
    PRICE_NONE_POSITIVE_EXCEPTION("가격이 올바르지 않습니다.")
}
