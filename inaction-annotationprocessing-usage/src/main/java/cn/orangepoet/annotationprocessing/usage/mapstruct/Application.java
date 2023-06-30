package cn.orangepoet.annotationprocessing.usage.mapstruct;

import org.apache.commons.lang3.StringUtils;

/**
 * @author chengzhi
 * @date 2019/09/19
 */
public class Application {
    public static void main(String[] args) {
        String s = StringUtils.stripEnd("任务已完成，xx奖励已发放,", ",");
        System.out.println(s);
        Woo woo = new Woo();
        woo.setName("woo");

        Foo foo = MapStructUse.INSTANCE.convert2Foo(woo);
        System.out.println(foo);
    }

}
