package com.tangchao.shop.service;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.ShopOrderDTO;
import com.tangchao.shop.params.*;
import com.tangchao.shop.vo.OrderResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface ShopOrderService {

    OrderResponse createOrder(ShopOrderParam shopOrderParam, HttpServletRequest request);

    PageResult<ShopOrderDTO> getShopOrderList(Integer status, Integer pageNo, Integer pageSize);

    /**
     * 订单支付
     *
     * @param orderId
     * @return
     */
    ResponseEntity payOrder(String orderId, HttpServletRequest request);

    ResponseEntity cancelOrder(String orderId);

    ResponseEntity getBy(String orderId);

    ResponseEntity list(Integer pageNo, Integer pageSize, Integer status, String orderNo, String buyerNick, String username, String userCode, String phone, Long beforeDate, Long rearDate, Integer orderType);

    ResponseEntity delivery(DeliveryParam deliveryParam);

    ResponseEntity changeAddress(ChangeAddressParam changeAddressParam);

    ResponseEntity getById(String id);

    ResponseEntity testPay(ShopPayOrderParam payOrderParam);

    ResponseEntity payNotify(String orderId, String resultCode, String money, String type, String sign, String timeEnd,String outTradeNo) throws Exception;

    ResponseEntity buy(BuyParam buyParam);

    ResponseEntity modifyAddress(ModifyAddressParam modifyAddressParam);

    ResponseEntity endOrder(String orderId);

    ResponseEntity orderModifyAddress(ModifyAddressParam modifyAddressParam);

    ResponseEntity orderRefund(String id);

    ResponseEntity submitoOrderRefund(SubmitoOrderRefundParam submitoOrderRefundParam);

    ResponseEntity returnOrderList(Integer pageNo, Integer pageSize, Integer status);

    ResponseEntity reject(RejectParam rejectParam);

    ResponseEntity adminPasse(String orderNo);

    ResponseEntity orderModifyVirtualAccount(ModifyVirtualAccountParam modifyVirtualAccountParam);

    String userPaymentNotifyByPayBOB(HttpServletRequest request) throws Exception;

    Map<String,String> payOrderByBoB(ModifyAddressParam modifyAddressParam) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    String userPayCode(Long userCode, String urlText) throws Exception;

    Map<String, String> payOrderAgain(String orderId, HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException;


    Map<String,String> payOrderByBillplz(HttpServletRequest request,ModifyAddressParam modifyAddressParam);

    Map<String, String> payOrderAgainByBillplz(String orderId, HttpServletRequest request);

    Map<String,String> payOrderByBillplz(Long userCode,HttpServletRequest request,Double money);


}
