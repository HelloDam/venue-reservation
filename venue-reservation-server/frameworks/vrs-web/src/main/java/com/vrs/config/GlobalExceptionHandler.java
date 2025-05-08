package com.vrs.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.AbstractException;
import com.vrs.convention.exception.ClientException;
import com.vrs.convention.exception.RemoteException;
import com.vrs.convention.exception.ServiceException;
import com.vrs.convention.result.Result;
import com.vrs.convention.result.Results;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

/**
 * 全局异常处理器
 */
@Component
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 拦截参数验证异常
     */
    @SneakyThrows
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Result> validExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        FieldError firstFieldError = CollectionUtil.getFirst(bindingResult.getFieldErrors());
        String exceptionStr = Optional.ofNullable(firstFieldError)
                .map(FieldError::getDefaultMessage)
                .orElse(StrUtil.EMPTY);
        log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), exceptionStr);
        Result errorResult = Results.failure(BaseErrorCode.CLIENT_ERROR.code(), exceptionStr);
        // 返回400状态码
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    /**
     * 请求参数解析错误，比如当请求体为空或无法正确解析为对象时。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result> handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException ex) {
        String exceptionMessage = "Invalid request payload: " + ex.getMessage();
        log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), exceptionMessage);
        Result errorResult = Results.failure(BaseErrorCode.CLIENT_ERROR.code(), exceptionMessage);
        // 返回400状态码
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    /**
     * 拦截应用内抛出的异常
     */
    @ExceptionHandler(value = {AbstractException.class})
    public ResponseEntity<Result> abstractException(HttpServletRequest request, AbstractException ex) {
        // 检查具体的异常类型
        if (ex instanceof ClientException) {
            // --if-- 客户端错误
            log.error("[{}] {} [ClientException] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString());
            return new ResponseEntity<>(Results.failure(ex), HttpStatus.BAD_REQUEST);
        } else if (ex instanceof RemoteException) {
            // --if-- 远程服务错误
            log.error("[{}] {} [RemoteException] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString());
            return new ResponseEntity<>(Results.failure(ex), HttpStatus.SERVICE_UNAVAILABLE);
        } else if (ex instanceof ServiceException) {
            // --if-- 系统服务错误
            log.error("[{}] {} [ServiceException] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString());
            return new ResponseEntity<>(Results.failure(ex), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            // 如果不是上述任何一种，记录基本错误信息
            return new ResponseEntity<>(Results.failure(ex), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 拦截未捕获异常
     */
    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity defaultErrorHandler(HttpServletRequest request, Throwable throwable) {
        log.error("[{}] {} ", request.getMethod(), getUrl(request), throwable);
        // 返回400状态码
        return new ResponseEntity<>(Results.failure(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getUrl(HttpServletRequest request) {
        if (StringUtils.isEmpty(request.getQueryString())) {
            return request.getRequestURL().toString();
        }
        return request.getRequestURL().toString() + "?" + request.getQueryString();
    }

}
