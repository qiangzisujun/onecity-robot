package test;

import java.math.BigDecimal;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/1/16 17:32
 */
public class Test {


    @org.junit.Test
    public void test(){

        BigDecimal totalMoney=new BigDecimal("0.01");

        BigDecimal  money=new BigDecimal("100");
        BigDecimal num=totalMoney.divide(money);
        System.out.println(num.stripTrailingZeros().toString());
    }

    @org.junit.Test
    public void one() {

        StringBuilder address = new StringBuilder();
        System.out.println("==" + address.toString());
    }

}
