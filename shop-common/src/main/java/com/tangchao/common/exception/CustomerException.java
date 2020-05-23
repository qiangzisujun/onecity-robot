package com.tangchao.common.exception;

import com.tangchao.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CustomerException extends RuntimeException {

    private ExceptionEnum exceptionEnum;

}
