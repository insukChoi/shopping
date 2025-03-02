package com.insuk.shopping.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val message: String,
    val status: Int,
    val timestamp: LocalDateTime = LocalDateTime.now(),
) {
    constructor(code: WebInfraErrorCode) : this(code.message, code.status)

    constructor(code: WebInfraErrorCode, ex: Exception) : this(ex.message ?: code.message, code.status)
}

enum class WebInfraErrorCode(
    val status: Int,
    val message: String,
) {
    INVALID_INPUT_VALUE(400, "입력값이 올바르지 않습니다."),
    METHOD_NOT_ARROWED(405, "지원하지 않은 http 요청입니다."),
    INTERNAL_SERVER_ERROR(500, "문제가 발생하였습니다."),
}
