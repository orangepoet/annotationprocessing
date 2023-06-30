package cn.orangepoet.annotationprocessing.usage.factory;

import cn.orangepoet.annotationprocessing.processor.factory.Factory;

/**
 * @Author: chengzhi
 * @Date: 2020/3/28
 */
@Factory
public class Farm {

    public static void main() {
        Farm farm = FarmFactory.newInstance();
    }
}