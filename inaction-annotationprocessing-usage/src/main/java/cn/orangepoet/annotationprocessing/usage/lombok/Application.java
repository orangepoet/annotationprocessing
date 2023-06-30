package cn.orangepoet.annotationprocessing.usage.lombok;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * @author chengzhi
 * @date 2021/02/23
 */
public class Application {
    public static void main(String[] args) {
        ValueUse valueUse = new ValueUse("orange", 30);
        //System.out.println(valueUse.toString());
        //
        //String s = JSON.toJSONString(valueUse);
        //
        //ValueUse valueUse1 = JSON.parseObject(s, ValueUse.class);
        //
        //System.out.println(valueUse1.toString());

        Double rate = 1.5;
        int amt = BigDecimal.valueOf(Integer.parseInt("11"))
                .multiply(BigDecimal.valueOf(Optional.ofNullable(rate).orElse(1.0d)))
                .setScale(0, RoundingMode.CEILING)
                .intValue();

        System.out.println(amt);
    }
}
