package com.insuk.shopping.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log by lazy { LoggerFactory.getLogger(this.javaClass) }

    /**
     * 아래 정의되지 않은 모든 예외를 서버 예외로 처리한다.
     */
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error("error message: ${ex.message}, exception: ${ex.cause}")
        val response =
            ErrorResponse(
                WebInfraErrorCode.INTERNAL_SERVER_ERROR,
                ex,
            )
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    /**
     * 미리 정의한 도메인 Exception 을 처리한다.
     */
    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): ResponseEntity<ErrorResponse> {
        log.error("domain error message: ${ex.message}, exception: ${ex.cause}")
        val response =
            ErrorResponse(
                WebInfraErrorCode.INTERNAL_SERVER_ERROR,
                ex,
            )
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    /**
     * `@RequestParam`이 누락되었을 때 커스텀 에러 메시지를 반환
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingRequestParamException(ex: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> {
        val response =
            ErrorResponse(
                WebInfraErrorCode.INVALID_INPUT_VALUE,
                ex,
            )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    /**
     * 지원하지 않은 HTTP method 호출할 경우 발생한다.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorResponse> {
        val response =
            ErrorResponse(
                WebInfraErrorCode.METHOD_NOT_ARROWED,
                ex,
            )
        return ResponseEntity(response, HttpStatus.METHOD_NOT_ALLOWED)
    }
}
