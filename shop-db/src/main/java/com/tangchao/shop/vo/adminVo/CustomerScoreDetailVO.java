package com.tangchao.shop.vo.adminVo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/31 13:57
 */
@Data
@ApiModel
public class CustomerScoreDetailVO {

    @ApiModelProperty(value = "积分",name = "score")
    private Long score;

    @ApiModelProperty(value = "积分来源{ 1：注册，2：订单，3：签到 ，4：充值 , 5：扣减，6：邀请, 7：晒单, 8：不中全返, 9：充值卡换积分}",name = "dataSource")
    private Integer dataSource;


    @ApiModelProperty(value = "订单编号",name = "orderCode")
    private String orderCode;


    @ApiModelProperty(value = "积分描述",name = "scoreDescribe")
    private String scoreDescribe;


    @ApiModelProperty(value = "积分标识{ 1：收入，2：支出 }",name = "scoreFlag")
    private Integer scoreFlag;


    @ApiModelProperty(value = "创建时间",name = "createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    private String dataStr;


    public String getDataStr() {

        switch (dataSource){
            case 1:
                this.dataStr="注册";
                break;
            case 2:
                this.dataStr="订单";
                break;
            case 3:
                this.dataStr="签到";
                break;
            case 4:
                this.dataStr="充值";
                break;
            case 5:
                this.dataStr="扣减";
                break;
            case 6:
                this.dataStr="邀请";
                break;
            case 7:
                this.dataStr="晒单";
                break;
            case 8:
                this.dataStr="不中全返";
                break;
            case 9:
                this.dataStr="充值卡换积分";
                break;
            default:
                this.dataStr="";
                break;
        }
        return dataStr;
    }

}
