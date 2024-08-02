package com.ldapauth.web;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import com.ldapauth.exception.BusinessException;
import com.ldapauth.pojo.vo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 全局异常处理器
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * 缺少请求体异常处理器
     * @param e 缺少请求体异常 使用get方式请求 而实体使用@RequestBody修饰
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> parameterBodyMissingExceptionHandler(HttpMessageNotReadableException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',请求体缺失'{}'", requestURI, e.getMessage(),e);
        return Result.failed("缺少请求体");
    }

    // get请求的对象参数校验异常
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public Result<Void> bindExceptionHandler(MissingServletRequestParameterException e,HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',get方式请求参数'{}'必传", requestURI, e.getMessage(),e);
        return Result.failed("请求的对象参数校验异常");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址 '{}',不支持'{}' 请求", requestURI, e.getMethod(),e);
        return Result.failed(HttpStatus.METHOD_NOT_ALLOWED.value(),HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
    }




    /**
     * 参数不正确
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String error = String.format("%s 应该是 %s 类型", e.getName(), e.getRequiredType().getSimpleName());
        log.error("请求地址'{}',{},参数类型不正确", requestURI,error,e);
        return Result.failed("参数类型不正确");
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return Result.failed(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    /**
     * 捕获转换类型异常
     * @param e
     * @return
     */
    @ExceptionHandler(UnexpectedTypeException.class)
    public Result unexpectedTypeHandler(UnexpectedTypeException e)
    {
        log.error("类型转换错误：{}",e.getMessage(), e);
        return  Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
    }

    /**
     * 捕获转换类型异常
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result methodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        BindingResult bindingResult =  e.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();
        log.error("参数验证异常：{}",e.getMessage(), e);
        if (!errors.isEmpty()) {
            // 只显示第一个错误信息
            return Result.error(HttpStatus.BAD_REQUEST.value(), errors.get(0).getDefaultMessage());
        }
        return  Result.error(HttpStatus.BAD_REQUEST.value(),"MethodArgumentNotValid");
    }

    // 运行时异常
    @ExceptionHandler(RuntimeException.class)
    public Result runtimeExceptionHandler(RuntimeException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',捕获运行时异常'{}'", requestURI, e.getMessage(),e);
        return Result.failed( e.getMessage());
    }
    // 系统级别异常
    @ExceptionHandler(Throwable.class)
    public Result throwableExceptionHandler(Throwable e,HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',捕获系统级别异常'{}'", requestURI,e.getMessage(),e);
        return Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    /**
     * IllegalArgumentException 捕获转换类型异常
     * @param e
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result illegalArgumentException(IllegalArgumentException e)
    {
        String message = e.getMessage();
        log.error("IllegalArgumentException：{}",e.getMessage(),e);
        if (Objects.nonNull(message)) {
            //错误信息
            return Result.error(HttpStatus.BAD_REQUEST.value(),message);
        }
        return  Result.error(HttpStatus.BAD_REQUEST.value(),"error");
    }
    /**
     * InvalidFormatException 捕获转换类型异常
     * @param e
     * @return
     */
    @ExceptionHandler(InvalidFormatException.class)
    public Result invalidFormatException(InvalidFormatException e)
    {
        String message = e.getMessage();
        log.error("InvalidFormatException：{}",e.getMessage(),e);
        if (Objects.nonNull(message)) {
            //错误信息
            return Result.error(HttpStatus.BAD_REQUEST.value(),message);
        }
        return  Result.error(HttpStatus.BAD_REQUEST.value(),"error");
    }



    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        BindingResult bindingResult =  e.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();
        log.error("参数验证异常：{}",e.getMessage(), e);
        if (!errors.isEmpty()) {
            // 只显示第一个错误信息
            return Result.error(HttpStatus.BAD_REQUEST.value(), errors.get(0).getDefaultMessage());
        }
        return  Result.error(HttpStatus.BAD_REQUEST.value(),"MethodArgumentNotValid");
    }

    /**
     * 业务异常处理
     * 业务自定义code 与 message
     *
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.error("业务自定义异常:{},{}",e.getCode(),e.getMessage(),e);
        return Result.error(e.getCode(),e.getMessage());
    }
}
