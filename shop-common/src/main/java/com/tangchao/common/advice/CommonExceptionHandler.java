package com.tangchao.common.advice;

import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResult> handleException(CustomerException e) {
        return ResponseEntity.status(200).body(new ExceptionResult(e.getExceptionEnum()));
    }
}
