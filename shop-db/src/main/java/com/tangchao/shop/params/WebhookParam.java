package com.tangchao.shop.params;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @Class WebhookParam
 * @Description TODO billplz支付参数类
 * @Author Aquan
 * @Date 2020/5/29 14:11
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@Component
public class WebhookParam {


    private String id;
    private String collection_id;
    private String paid;
    private String state;
    private String amount;
    private String paid_amount;
    private String due_at;
    private String email;
    private String mobile;
    private String name;
    private String url;
    private String paid_at;
    private String x_signature;

    @Override
    public String toString() {
        return  "amount" + amount + "|" +
                "collection_id" + collection_id + "|" +
                "due_at" + due_at + "|" +
                "email" + email + "|" +
                "id" + id + "|" +
                "mobile" + mobile + "|" +
                "name" + name + "|" +
                "paid_amount" + paid_amount + "|" +
                "paid_at" + paid_at + "|" +
                "paid" + paid + "|" +
                "state" + state + "|" +
                "url" + url;
    }

}
