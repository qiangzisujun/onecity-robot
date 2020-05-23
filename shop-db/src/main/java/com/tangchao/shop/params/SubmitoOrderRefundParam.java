package com.tangchao.shop.params;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Class SubmitoOrderRefundParam
 * @Description TODO
 * @Author Aquan
 * @Date 2020/3/21 15:03
 * @Version 1.0
 **/
@Data
public class SubmitoOrderRefundParam {

    private String orderId;
    private String description;
    private List<String> images;

    public String getImages() {
        String str = StringUtils.join(this.images, ",");
        return str;
    }

}
