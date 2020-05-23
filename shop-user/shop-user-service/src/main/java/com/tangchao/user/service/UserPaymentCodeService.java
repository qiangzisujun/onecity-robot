package com.tangchao.user.service;

import com.tangchao.shop.params.UpdateUserPaymentCodeParam;
import com.tangchao.shop.params.UserPaymentCodeParam;
import org.springframework.http.ResponseEntity;

public interface UserPaymentCodeService {
    ResponseEntity getUserPaymentCode();

    ResponseEntity bind(UserPaymentCodeParam userPaymentCodeParam);

    ResponseEntity getUserPaymentCodeById(String id);

    ResponseEntity updateBind(UpdateUserPaymentCodeParam updateUserPaymentCodeParam);
}
